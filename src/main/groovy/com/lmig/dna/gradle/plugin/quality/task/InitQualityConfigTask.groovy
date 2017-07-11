package com.lmig.dna.gradle.plugin.quality.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import com.lmig.dna.gradle.plugin.quality.ConfigLoader

import groovy.transform.CompileStatic

/**
 * Task copies default configs to user directory (quality.configDir) for customization.
 * By default, does not override existing files.
 * Registered as 'initQualityConfig'.
 *
 * @author Mark McCann
 * @see com.lmig.dna.gradle.plugin.quality.QualityPlugin for registration
 */
@CompileStatic
class InitQualityConfigTask extends DefaultTask {

	@Input
	boolean override

	InitQualityConfigTask() {
		group  = 'build setup'
		description = 'Copies default quality plugin configuration files for customization'
	}

	@TaskAction
	void run() {
		new ConfigLoader(project).initUserConfigs(override)
	}
}
