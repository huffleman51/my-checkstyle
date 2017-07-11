package com.lmig.dna.gradle.plugin.quality.report

import org.gradle.api.Project

import groovy.transform.CompileStatic

/**
 * Reporter is responsible for printing violations into console and possible html report generation.
 *
 * @author Mark McCann
 */
@CompileStatic
interface Reporter {

	/**
	 * New line symbol.
	 */
	String NL = String.format('%n')

	/**
	 * Called after quality tool task to report violations.
	 *
	 * @param project project instance
	 * @param type task type (main or test)
	 */
	void report(Project project, String type)
}
