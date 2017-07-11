package com.lmig.dna.gradle.plugin.quality.report

import java.util.zip.ZipFile

import org.gradle.api.Project

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Prints codenarc errors (from xml report) into console.
 *
 * @author Mark McCann
 */
@CompileStatic
class CodeNarcReporter implements Reporter {

	@Override
	@SuppressWarnings('DuplicateStringLiteral')
	@CompileStatic(TypeCheckingMode.SKIP)
	void report(Project project, String type) {
		project.with {
			File reportFile = file("${extensions.codenarc.reportsDir}/${type}.xml")
			if (!reportFile.exists()) {
				return
			}
			Node result = new XmlParser().parse(reportFile)
			Node summary = result.PackageSummary[0]
			int fileCnt = summary.@filesWithViolations as Integer
			if (fileCnt > 0) {
				Integer p1 = summary.@priority1 as Integer
				Integer p2 = summary.@priority2 as Integer
				Integer p3 = summary.@priority3 as Integer
				Integer count = p1 + p2 + p3
				logger.error "$NL$count ($p1 / $p2 / $p3) CodeNarc violations were found in ${fileCnt} files$NL"

				Map<String, String> desc = [:]
				result.Rules.Rule.each {
					desc[it.@name] = ReportUtils.unescapeHtml(it.Description.text())
				}

				Properties props = loadCodenarcProperties(project)

				result.Package.each {
					String pkg = it.@path.replaceAll('/', '.')
					it.File.each {
						String src = it.@name
						it.Violation.each {
							String rule = it.@ruleName
							String[] path = props[rule].split('\\.')
							String group = path[path.length - 2]
							String priority = it.@priority
							String srcLine = ReportUtils.unescapeHtml(it.SourceLine.text())
							String message = ReportUtils.unescapeHtml(it.Message.text())
							// part in braces recognized by intellij IDEA and shown as link
							logger.error "[${group.capitalize()} | ${rule}] ${pkg}.($src:${it.@lineNumber})  " +
									"[priority ${priority}]" +
									"$NL\t>> ${srcLine}" +
									"$NL  ${message}" +
									"$NL  ${desc[rule]}" +
									"$NL  http://codenarc.sourceforge.net/codenarc-rules-${group}.html#$rule$NL"
						}
					}
				}
			}
		}
	}

	private Properties loadCodenarcProperties(Project project) {
		Properties props = new Properties()
		File codenarcJar = project.configurations.getByName('codenarc').files.find {
			it.name.contains('CodeNarc')
		}
		ZipFile file = new ZipFile(codenarcJar)
		props.load(file.getInputStream(file.getEntry('codenarc-base-rules.properties')))
		return props
	}
}
