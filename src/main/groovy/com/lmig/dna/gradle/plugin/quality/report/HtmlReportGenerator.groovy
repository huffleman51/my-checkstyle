package com.lmig.dna.gradle.plugin.quality.report

import org.gradle.api.Project

import groovy.transform.CompileStatic

/**
 * Responsible for html report generation for plugins not supporting that directly (findbugs).
 *
 * @author Mark McCann
 */
@CompileStatic
interface HtmlReportGenerator {

	/**
	 * Called after quality tool task to generate html report.
	 *
	 * @param project project instance
	 * @param type task type (main or test)
	 */
	void generateHtmlReport(Project project, String type)
}
