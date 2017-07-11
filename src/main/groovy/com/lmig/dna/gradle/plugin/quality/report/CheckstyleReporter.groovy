package com.lmig.dna.gradle.plugin.quality.report

import org.gradle.api.Project

import com.lmig.dna.gradle.plugin.quality.ConfigLoader

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Prints checkstyle errors (from xml report) into console.
 *
 * @author Mark McCann
 */
@SuppressWarnings('DuplicateStringLiteral')
@CompileStatic
class CheckstyleReporter implements Reporter {

	ConfigLoader configLoader

	CheckstyleReporter(ConfigLoader configLoader) {
		this.configLoader = configLoader
	}

	@Override
	@CompileStatic(TypeCheckingMode.SKIP)
	void report(Project project, String type) {
		project.with {
			File reportFile = file("${extensions.checkstyle.reportsDir}/${type}.xml")
			if (!reportFile.exists()) {
				return
			}

			Node result = new XmlParser().parse(reportFile)
			int cnt = result.file.error.size()
			if (cnt > 0) {
				int filesCnt = result.file.findAll { it.error.size() > 0 }.size()
				logger.error "$NL$cnt Checkstyle rule violations were found in $filesCnt files$NL"

				result.file.each { file ->
					String filePath = file.@name
					String sourceFile = ReportUtils.extractFile(filePath)
					String name = ReportUtils.extractJavaPackage(project, type, filePath)

					file.error.each {
						String check = extractCheckName(it.@source)
						String group = extractGroupName(it.@source)
						String srcPointer = it.@line
						// part in braces recognized by intellij IDEA and shown as link
						logger.error "[${group.capitalize()} | $check] $name.($sourceFile:$srcPointer)" +
								"$NL  ${it.@message}" +
								"$NL  http://checkstyle.sourceforge.net/config_${group}.html#$check$NL"
					}
				}
			}
		}
	}

	private String extractCheckName(String source) {
		String check = source
		check = check[check.lastIndexOf('.') + 1..-1]
		check[0..(check.length() - 1 - 'Check'.length())]
	}

	private String extractGroupName(String source) {
		String[] path = source.split('\\.')
		String group = path[path.length - 2]
		if (group == 'checks') {
			group = 'misc'
		}
		return group
	}
}
