package com.lmig.dna.gradle.plugin.quality

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Mark McCann
 */
class FindbugsPluginsKitTest extends AbstractKitTest {

	def "Check findbugs plugins"() {
		setup:
		build("""
            plugins {
                id 'java'
                id 'findbugs'
                id 'com.lmig.dna.quality'
            }

            quality {
                checkstyle false
                pmd false
                strict false
            }

            repositories { maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' } }
            dependencies {
                findbugsPlugins 'com.mebigfatguy.fb-contrib:fb-contrib:6.6.0'
            }
        """)

		fileFromClasspath('src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')
		fileFromClasspath('src/main/java/sample/Sample2.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample2.java')

		when: "run check task with java sources"
		BuildResult result = run('check')

		then: "all plugins detect violations"
		result.task(":check").outcome == TaskOutcome.SUCCESS
		result.output.contains('FindBugs rule violations were found')
	}

	def "Check findbugs plugins 2"() {
		setup:
		build("""
            plugins {
                id 'java'
                id 'com.lmig.dna.quality'
            }

            quality {
                checkstyle false
                pmd false
                strict false
            }

            repositories { maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' }}
            afterEvaluate {
                dependencies {
                    findbugsPlugins 'com.mebigfatguy.fb-contrib:fb-contrib:6.6.0'
                }
            }
        """)

		fileFromClasspath('src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')
		fileFromClasspath('src/main/java/sample/Sample2.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample2.java')

		when: "run check task with java sources"
		BuildResult result = run('check')

		then: "all plugins detect violations"
		result.task(":check").outcome == TaskOutcome.SUCCESS
		result.output.contains('FindBugs rule violations were found')
	}
}