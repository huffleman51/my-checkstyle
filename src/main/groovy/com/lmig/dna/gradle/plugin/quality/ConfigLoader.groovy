package com.lmig.dna.gradle.plugin.quality

import org.gradle.api.Project

import groovy.transform.CompileStatic

/**
 * Loads configuration files either from custom configs directory (quality.configDir) or from classpath.
 * When loading from classpath all files are copied (and cached) in build/quality-configs/.
 * <p>
 * To avoid problems with clean task, default configs are copied only before actual task run. All config file
 * getters contains optional parameter copyDefaultConfig to prevent actual file copying on configuration phase.
 *
 * @author Mark McCann
 */
@CompileStatic
class ConfigLoader {
	private final String checkstyle = 'checkstyle/DNA-checkstyle.xml'
	private final String checkstyleSuppressions = 'checkstyle/DNA-checkstyle-suppressions.xml'
	private final String checkstyleHeader = 'checkstyle/DNA-checkstyle-header.txt'
	private final String pmd = 'pmd/pmd.xml'
	private final String findbugsExclude = 'findbugs/exclude.xml'
	private final String findbugsXsl = 'findbugs/html-report-style.xsl'
	private final String codenarc = 'codenarc/codenarc.xml'

	Project project
	File configDir
	File tmpConfigDir

	ConfigLoader(Project project) {
		this.project = project
	}

	File resolveCheckstyleConfig(boolean copyDefaultFile = true) {
		resolve(checkstyle, copyDefaultFile)
	}

	File resolveCheckstyleSuppressions(boolean copyDefaultFile = true) {
		resolve(checkstyleSuppressions, copyDefaultFile)
	}

	File resolveCheckstyleHeader(boolean copyDefaultFile = true) {
		resolve(checkstyleHeader, copyDefaultFile)
	}

	File resolvePmdConfig(boolean copyDefaultFile = true) {
		resolve(pmd, copyDefaultFile)
	}

	File resolveFindbugsExclude(boolean copyDefaultFile = true) {
		resolve(findbugsExclude, copyDefaultFile)
	}

	File resolveFindbugsXsl(boolean copyDefaultFile = true) {
		resolve(findbugsXsl, copyDefaultFile)
	}

	File resolveCodenarcConfig(boolean copyDefaultFile = true) {
		resolve(codenarc, copyDefaultFile)
	}

	/**
	 * Copies default configs into configured user directory.
	 *
	 * @param override override filed
	 */
	void initUserConfigs(boolean override) {
		init()
		[
			checkstyle,
			checkstyleSuppressions,
			checkstyleHeader,
			pmd,
			findbugsExclude,
			findbugsXsl,
			codenarc
		].each {
			copyConfig(configDir, it, override)
		}
	}

	private File resolve(String path, boolean copyDefaultFile) {
		init()
		// look custom user file first
		File target = new File(configDir, path)
		return target.exists() ?
				target
				: (copyDefaultFile ? copyConfig(tmpConfigDir, path, false) : new File(tmpConfigDir, path)) as File
	}

	private void init() {
		if (configDir == null) {
			// lazy resolution to make sure user configuration applied
			this.configDir = project.rootProject.file(project.extensions.findByType(QualityExtension).configDir)
			this.tmpConfigDir = project.file("$project.buildDir/quality-configs/")
		}
	}

	private copyConfig(File parent, String path, boolean override) {
		File target = new File(parent, path)
		if (target.exists() && !override) {
			return target
		}
		if (!target.parentFile.exists() && !target.parentFile.mkdirs()) {
			throw new IllegalStateException("Failed to create directories: $target.parentFile.absolutePath")
		}
		InputStream stream = getClass().getResourceAsStream("/com/lmig/dna/quality/config/$path")
		if (stream == null) {
			throw new IllegalStateException("Default config file not found in classpath: $path")
		}
		target << stream.text
		return target
	}
}
