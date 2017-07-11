package com.lmig.dna.gradle.plugin.quality

import org.gradle.api.Project

/**
 * @author Mark McCann
 */
class JavaLintOptionsApplyTest extends AbstractTest {

	def "Check lint options applied"() {

		when: "apply plugin"
		file('src/main/java').mkdirs()

		Project project = project {
			apply plugin: 'java'
			apply plugin: 'com.lmig.dna.quality'
		}

		then: "lint options applied"
		project.tasks.compileJava.options.compilerArgs == [
			"-Xlint:deprecation",
			"-Xlint:unchecked"
		]
		project.tasks.compileTestJava.options.compilerArgs == [
			"-Xlint:deprecation",
			"-Xlint:unchecked"
		]
	}

	def "Check lint options applied with configuration"() {

		when: "apply plugin"
		file('src/main/java').mkdirs()

		Project project = project {
			apply plugin: 'java'
			apply plugin: 'com.lmig.dna.quality'

			quality { lintOptions = ['unchecked'] }
		}

		then: "lint options applied"
		project.tasks.compileJava.options.compilerArgs == ["-Xlint:unchecked"]
		project.tasks.compileTestJava.options.compilerArgs == ["-Xlint:unchecked"]
	}
}