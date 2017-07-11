package com.lmig.dna.gradle.plugin.quality

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Mark McCann
 */
class QualityTasksDisableKitTest extends AbstractKitTest {

	def "Check java and groovy checks disable"() {
		setup:
		build("""
            plugins {
                id 'groovy'
                id 'com.lmig.dna.quality'
            }

            quality {
                enabled = false
            }

            repositories {
                maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' } //required for testKit run
            }

            dependencies {
                compile localGroovy()
            }
        """)

		fileFromClasspath('src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')
		fileFromClasspath('src/main/groovy/sample/GSample.groovy', '/com/lmig/dna/gradle/plugin/quality/groovy/sample/GSample.groovy')

		when: "run check task with both sources"
		BuildResult result = run('check')

		then: "all plugins disabled"
		result.task(":check").outcome == TaskOutcome.UP_TO_DATE
		!result.output.contains('CodeNarc rule violations were found')
		!result.output.contains('Checkstyle rule violations were found')
		!result.output.contains('FindBugs rule violations were found')
		!result.output.contains('PMD rule violations were found')
	}

	def "Check direct task call"() {
		setup:
		build("""
            plugins {
                id 'groovy'
                id 'com.lmig.dna.quality'
            }

            quality {
                enabled = false
            }

            repositories {
                maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' } //required for testKit run
            }

            dependencies {
                compile localGroovy()
            }
        """)

		fileFromClasspath('src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')

		when: "run checkstyle task directly"
		BuildResult result = runFailed('checkstyleMain')

		then: "direct call performed check"
		result.task(":checkstyleMain").outcome == TaskOutcome.FAILED
		result.output.contains('Checkstyle rule violations were found')
	}

	def "Check task call through grouping task"() {
		setup:
		build("""
            plugins {
                id 'groovy'
                id 'com.lmig.dna.quality'
            }

            quality {
                enabled = false
            }

            repositories {
                maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' } //required for testKit run
            }

            dependencies {
                compile localGroovy()
            }
        """)

		fileFromClasspath('src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')

		when: "run grouping task"
		BuildResult result = runFailed('checkQualityMain')

		then: "direct quality task executed"
		result.task(":checkstyleMain").outcome == TaskOutcome.FAILED
		result.output.contains('Checkstyle rule violations were found')
	}
}