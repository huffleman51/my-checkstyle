package com.lmig.dna.gradle.plugin.quality.task

import org.gradle.api.Project
import org.gradle.api.Task

import com.lmig.dna.gradle.plugin.quality.AbstractTest


/**
 * @author Mark McCann
 */
class GroupingTasksTest extends AbstractTest {

	def "Check grouping tasks registration"() {

		when: "apply plugin"
		file('src/main/java').mkdirs()

		Project project = project {
			apply plugin: 'java'
			apply plugin: 'com.lmig.dna.quality'

			quality {
				sourceSets = [
					project.sourceSets.main,
					project.sourceSets.test
				]
			}
		}

		then: "grouping tasks registered"
		project.tasks.checkQualityMain
		project.tasks.checkQualityTest

		then: "correct tasks grouped"
		dependsOn(project.tasks.checkQualityMain) == [
			'checkstyleMain',
			'pmdMain',
			'findbugsMain'] as Set
		dependsOn(project.tasks.checkQualityTest) == [
			'checkstyleTest',
			'pmdTest',
			'findbugsTest'] as Set
	}

	def "Check groovy grouping tasks registration"() {

		when: "apply plugin"
		file('src/main/groovy').mkdirs()

		Project project = project {
			apply plugin: 'groovy'
			apply plugin: 'com.lmig.dna.quality'

			quality {
				sourceSets = [
					project.sourceSets.main,
					project.sourceSets.test
				]
			}
		}

		then: "grouping tasks registered"
		project.tasks.checkQualityMain
		project.tasks.checkQualityTest

		then: "correct tasks grouped"
		dependsOn(project.tasks.checkQualityMain) == ['codenarcMain'] as Set
		dependsOn(project.tasks.checkQualityTest) == ['codenarcTest'] as Set
	}

	def "Check tasks created for all sources"() {

		when: "apply plugin"
		file('src/main/groovy').mkdirs()

		Project project = project {
			apply plugin: 'groovy'
			apply plugin: 'com.lmig.dna.quality'

			sourceSets { tata {} }

			quality {
				sourceSets = [project.sourceSets.main]
			}
		}

		then: "grouping tasks registered"
		project.tasks.checkQualityMain
		project.tasks.checkQualityTest
		project.tasks.checkQualityTata

		then: "correct tasks grouped"
		dependsOn(project.tasks.checkQualityMain) == ['codenarcMain'] as Set
		dependsOn(project.tasks.checkQualityTest) == ['codenarcTest'] as Set
		dependsOn(project.tasks.checkQualityTata) == ['codenarcTata'] as Set
	}

	private Set<String> dependsOn(Task task) {
		// dependsOn also contains implicit dependency to task sources
		task.dependsOn.collect { it instanceof Task ? it.name: null}.findAll{it} as Set
	}
}