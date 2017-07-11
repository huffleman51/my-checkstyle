package com.lmig.dna.gradle.plugin.quality.task

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import com.lmig.dna.gradle.plugin.quality.AbstractKitTest

/**
 * @author Mark McCann 
 */
class InitQualityConfigTaskKitTest extends AbstractKitTest {

	def "Check configs init"() {
		setup:
		build("""
            plugins {
                id 'java'
                id 'com.lmig.dna.quality'
            }

            quality {
                configDir = 'config'
            }
        """)

		when: "run init configs task"
		BuildResult result = run('initQualityConfig')

		then: "configs copied"
		result.task(':initQualityConfig').outcome == TaskOutcome.SUCCESS
		file('config/checkstyle/DNA-checkstyle.xml').exists()
		file('config/codenarc/codenarc.xml').exists()
		file('config/findbugs/exclude.xml').exists()
		file('config/findbugs/html-report-style.xsl').exists()
		file('config/pmd/pmd.xml').exists()
	}
}
