package com.lmig.dna.gradle.plugin.quality

import org.gradle.testkit.runner.BuildResult

/**
 * @author Mark McCann
 */
class CleanBuildTest extends AbstractKitTest {

	def "Check clean build java run"() {
		setup:
		build("""
            plugins {
                id 'java'
                id 'com.lmig.dna.quality'
            }

            quality {
                strict false
            }

            repositories {
                maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' } //required for testKit run
            }
        """)

		fileFromClasspath('src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')
		fileFromClasspath('src/main/java/sample/Sample2.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample2.java')

		when: "run clean build"
		BuildResult result = run('clean', 'build')

		then: "temp files are in place"
		true
	}

	def "Check clean build groovy run"() {
		setup:
		build("""
            plugins {
                id 'groovy'
                id 'com.lmig.dna.quality'
            }

            quality {
                strict false
            }

            repositories {
                maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' } //required for testKit run
            }

            dependencies {
                compile localGroovy()
            }
        """)

		fileFromClasspath('src/main/groovy/sample/GSample.groovy', '/com/lmig/dna/gradle/plugin/quality/groovy/sample/GSample.groovy')
		fileFromClasspath('src/main/groovy/sample/GSample2.groovy', '/com/lmig/dna/gradle/plugin/quality/groovy/sample/GSample2.groovy')

		when: "run clean build"
		BuildResult result = run('clean', 'build')

		then: "temp files are in place"
		true
	}
}