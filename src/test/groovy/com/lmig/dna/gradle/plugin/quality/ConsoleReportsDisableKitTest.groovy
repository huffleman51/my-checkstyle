package com.lmig.dna.gradle.plugin.quality

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Mark McCann
 */
class ConsoleReportsDisableKitTest extends AbstractKitTest {

	def "Check java and groovy checks disable"() {
		setup:
		build("""
            plugins {
                id 'groovy'
                id 'com.lmig.dna.quality'
            }

            quality {
                strict = false
                consoleReporting = false
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

		then: "all plugins detect violations"
		result.task(":check").outcome == TaskOutcome.SUCCESS
		result.output.contains('CodeNarc rule violations were found')
		result.output.contains('Checkstyle rule violations were found')
		result.output.contains('FindBugs rule violations were found')
		result.output.contains('PMD rule violations were found')

		then: "no console reporting performed"
		!result.output.contains('[Formatting | ClassJavadoc] sample.(GSample.groovy:3)  [priority 2]') // codenarc
		!result.output.contains('[Javadoc | JavadocType] sample.(Sample.java:3)') // checkstyle
		!result.output.contains('[Performance | URF_UNREAD_FIELD] sample.(Sample.java:8)  [priority 2]') // findbugs
		!result.output.contains('[Unused Code | UnusedPrivateField] sample.(Sample.java:5)') // pmd

		then: "findbugs html report generated"
		file('build/reports/findbugs/main.html').exists()
	}
}