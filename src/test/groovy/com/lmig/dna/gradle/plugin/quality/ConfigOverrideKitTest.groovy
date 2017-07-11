package com.lmig.dna.gradle.plugin.quality

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

/**
 * @author Mark McCann 
 */
class ConfigOverrideKitTest extends AbstractKitTest {

	def "Check config override"() {
		setup:
		build("""
            plugins {
                id 'java'
                id 'com.lmig.dna.quality'
            }

            quality {
                strict false
                configDir 'config'
            }

            repositories {
                maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' } //required for testKit run
            }
        """)

		fileFromClasspath('src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')
		fileFromClasspath('config/checkstyle/DNA-checkstyle.xml', '/com/lmig/dna/quality/config/checkstyle/DNA-checkstyle.xml')

		when: "run check task with java sources"
		BuildResult result = run('check')

		then: "custom config used"
		result.task(":check").outcome == TaskOutcome.SUCCESS
		!file('build/quality-configs/checkstyle/DNA-checkstyle.xml').exists()
	}
}