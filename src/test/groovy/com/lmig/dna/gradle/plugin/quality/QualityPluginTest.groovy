package com.lmig.dna.gradle.plugin.quality

import org.gradle.api.Project
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.api.plugins.quality.CodeNarcPlugin
import org.gradle.api.plugins.quality.FindBugsPlugin
import org.gradle.api.plugins.quality.PmdPlugin

/**
 * @author Mark McCann 
 */
class QualityPluginTest extends AbstractTest {

	def "Check plugins registration"() {

		when: "apply plugin"
		file('src/main/java').mkdirs()

		Project project = project {
			apply plugin: 'java'
			apply plugin: 'com.lmig.dna.quality'
		}

		then: "plugins registered"
		project.plugins.findPlugin(CheckstylePlugin)
		project.plugins.findPlugin(PmdPlugin)
		project.plugins.findPlugin(FindBugsPlugin)
		!project.plugins.findPlugin(CodeNarcPlugin)

		then: "tasks installed"
		project.tasks.initQualityConfig
		project.tasks.checkQualityMain
		project.tasks.checkQualityTest
	}

	def "Check plugins registration fo groovy"() {

		when: "apply plugin"
		file('src/main/groovy').mkdirs()

		Project project = project {
			apply plugin: 'groovy'
			apply plugin: 'com.lmig.dna.quality'
		}

		then: "plugins registered"
		!project.plugins.findPlugin(CheckstylePlugin)
		!project.plugins.findPlugin(PmdPlugin)
		!project.plugins.findPlugin(FindBugsPlugin)
		project.plugins.findPlugin(CodeNarcPlugin)
	}

	def "Check plugins disable options"() {

		when: "disable all plugins in config"
		file('src/main/java').mkdirs()

		Project project = project {
			apply plugin: 'java'
			apply plugin: 'com.lmig.dna.quality'

			quality {
				checkstyle false
				pmd false
				findbugs false
				codenarc false
			}
		}

		then: "plugins not registered"
		!project.plugins.findPlugin(CheckstylePlugin)
		!project.plugins.findPlugin(PmdPlugin)
		!project.plugins.findPlugin(FindBugsPlugin)
		!project.plugins.findPlugin(CodeNarcPlugin)
	}

	def "Check groovy sources check"() {

		when: "apply plugin"
		file('src/test/groovy').mkdirs()

		Project project = project {
			apply plugin: 'groovy'
			apply plugin: 'com.lmig.dna.quality'

			quality {
				sourceSets = [
					project.sourceSets.main,
				]
			}
		}

		then: "codenarc not registered"
		!project.plugins.findPlugin(CodeNarcPlugin)
	}

	def "Check groovy sources check2"() {

		when: "apply plugin"
		file('src/test/groovy').mkdirs()

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

		then: "codenarc registered"
		project.plugins.findPlugin(CodeNarcPlugin)
	}

	def "Check manual mode"() {

		when: "apply plugin"
		file('src/main/java').mkdirs()

		Project project = project {
			apply plugin: 'java'
			apply plugin: 'com.lmig.dna.quality'
			apply plugin: 'checkstyle'

			quality { autoRegistration = false }
		}

		then: "plugins registered"
		project.plugins.findPlugin(CheckstylePlugin)
		!project.plugins.findPlugin(PmdPlugin)
		!project.plugins.findPlugin(FindBugsPlugin)
		!project.plugins.findPlugin(CodeNarcPlugin)
	}
}