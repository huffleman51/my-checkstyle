package com.lmig.dna.gradle.plugin.quality.report

import org.gradle.api.Project

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Prints pmd errors (from xml report) into console.
 *
 * @author Mark McCann
 */
@CompileStatic
class PmdReporter implements Reporter {

	@Override
	@CompileStatic(TypeCheckingMode.SKIP)
	void report(Project project, String type) {
		project.with {
			File reportFile = file("${extensions.pmd.reportsDir}/${type}.xml")

			if (!reportFile.exists()) {
				return
			}
			Node result = new XmlParser().parse(reportFile)
			int cnt = result.file.violation.size()
			if (cnt > 0) {
				logger.error "$NL$cnt PMD rule violations were found in ${result.file.size()} files$NL"

				result.file.each { file ->
					String filePath = file.@name
					String sourceFile = ReportUtils.extractFile(filePath)
					String name = ReportUtils.extractJavaPackage(project, type, filePath)
					file.violation.each { violation ->
						String srcPos = violation.@beginline
						// part in braces recognized by intellij IDEA and shown as link
						logger.error "[${violation.@ruleset} | ${violation.@rule}] $name.($sourceFile:${srcPos})" +
								"$NL  ${violation.text().trim()}" +
								"$NL  ${violation.@externalInfoUrl}$NL"
					}
				}
			}
		}
	}
}
