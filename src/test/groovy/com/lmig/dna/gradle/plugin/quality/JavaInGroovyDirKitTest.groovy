package com.lmig.dna.gradle.plugin.quality

import org.gradle.testkit.runner.BuildResult

/**
 * @author Mark McCann
 */
class JavaInGroovyDirKitTest extends AbstractKitTest {

	def "Check java and groovy checks disable"() {
		setup:
		build("""
            plugins {
                id 'groovy'
                id 'checkstyle'
                id 'com.lmig.dna.quality'
            }

            quality {
                strict = false
            }

            repositories {
                maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' } //required for testKit run
            }

            dependencies {
                compile localGroovy()
            }
        """)

		// java in groovy folder
		fileFromClasspath('src/main/groovy/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')

		when: "run main quality check"
		BuildResult result = run('checkstyleMain')

		then: "violations detected"
		result.output.contains('Checkstyle rule violations were found')

		then: "correct package build"
		result.output.contains('[Misc | NewlineAtEndOfFile] sample.(Sample.java:0)')
	}
}