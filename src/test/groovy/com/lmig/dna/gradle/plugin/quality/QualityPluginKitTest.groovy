package com.lmig.dna.gradle.plugin.quality

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Mark McCann 
 */
class QualityPluginKitTest extends AbstractKitTest {

	def "Check java checks"() {
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

		when: "run check task with java sources"
		BuildResult result = run('check')

		then: "all plugins detect violations"
		result.task(":check").outcome == TaskOutcome.SUCCESS
		result.output.contains('Checkstyle rule violations were found')
		result.output.contains('FindBugs rule violations were found')
		result.output.contains('PMD rule violations were found')

		then: "all html reports generated"
		file('build/reports/checkstyle/main.html').exists()
		file('build/reports/findbugs/main.html').exists()
		file('build/reports/pmd/main.html').exists()

		when: "run one more time"
		result = run('check', '--rerun-tasks')

		then: "ok"
		result.task(":check").outcome == TaskOutcome.SUCCESS
		result.output.contains('Checkstyle rule violations were found')
		result.output.contains('FindBugs rule violations were found')
		result.output.contains('PMD rule violations were found')
	}

	def "Check groovy checks"() {
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

		when: "run check task with groovy sources"
		BuildResult result = run('check')

		then: "plugin detect violations"
		result.task(":check").outcome == TaskOutcome.SUCCESS
		result.output.contains('CodeNarc rule violations were found')

		then: "html report generated"
		file('build/reports/codenarc/main.html').exists()

		when: "run one more time"
		result = run('check', '--rerun-tasks')

		then: "ok"
		result.task(":check").outcome == TaskOutcome.SUCCESS
		result.output.contains('CodeNarc rule violations were found')
	}

	def "Check java and groovy checks"() {
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

		fileFromClasspath('src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')
		fileFromClasspath('src/main/java/sample/Sample2.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample2.java')
		fileFromClasspath('src/main/groovy/sample/GSample.groovy', '/com/lmig/dna/gradle/plugin/quality/groovy/sample/GSample.groovy')
		fileFromClasspath('src/main/groovy/sample/GSample2.groovy', '/com/lmig/dna/gradle/plugin/quality/groovy/sample/GSample2.groovy')

		when: "run check task with both sources"
		BuildResult result = run('check')

		then: "all plugins detect violations"
		result.task(":check").outcome == TaskOutcome.SUCCESS
		result.output.contains('CodeNarc rule violations were found')
		result.output.contains('Checkstyle rule violations were found')
		result.output.contains('FindBugs rule violations were found')
		result.output.contains('PMD rule violations were found')
	}

	def "Check plugins config override"() {
		setup:
		build("""
            plugins {
                id 'java'
                id 'com.lmig.dna.quality'
            }

            repositories {
                maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' } //required for testKit run
            }

            afterEvaluate {
                checkstyle {
                    sourceSets = []
                }
                pmd {
                    sourceSets = []
                }
                findbugs {
                    sourceSets = []
                }
            }
        """)

		fileFromClasspath('src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')
		fileFromClasspath('src/main/java/sample/Sample2.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample2.java')

		when: "run check task with java sources"
		BuildResult result = run('check')

		then: "all plugins detect violations"
		result.task(":check").outcome == TaskOutcome.UP_TO_DATE
		!result.output.contains('Checkstyle rule violations were found')
		!result.output.contains('FindBugs rule violations were found')
		!result.output.contains('PMD rule violations were found')
	}
}