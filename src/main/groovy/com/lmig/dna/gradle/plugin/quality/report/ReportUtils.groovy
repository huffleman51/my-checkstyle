package com.lmig.dna.gradle.plugin.quality.report

import org.gradle.api.Project

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

/**
 * Reporting utils.
 *
 * @author Mark McCann
 */
@SuppressWarnings('DuplicateStringLiteral')
@CompileStatic
class ReportUtils {

	/**
	 * Resolve java package from provided absolute source path.
	 * Use configured java source roots to properly detect class package.
	 *
	 * @param project project instance
	 * @param type execution type (main or test)
	 * @param file absolute path to source file
	 * @return package for provided java class path
	 */
	@CompileStatic(TypeCheckingMode.SKIP)
	static String extractJavaPackage(Project project, String type, String file) {
		String name = new File(file).canonicalPath
		Closure search = { Iterable<File> files ->
			files*.canonicalPath.find { String s -> name.startsWith(s) }
		}
		// try looking in java and then in groovy sources (mixed mode)
		String root = search(project.sourceSets[type].java.srcDirs) ?: search(project.sourceSets[type].groovy.srcDirs)
		if (root) {
			name = name[root.length() + 1..-1] // remove sources dir prefix
		}
		name = name[0..name.lastIndexOf('.') - 1] // remove extension
		name = name.replaceAll('\\\\|/', '.')
		name[0..name.lastIndexOf('.') - 1] // remove class name

	}

	/**
	 * @param path absolute path to source file
	 * @return file name without path
	 */
	static String extractFile(String path) {
		int idx = path.replaceAll('\\\\', '/').lastIndexOf('/')
		path[idx + 1..-1]
	}

	/**
	 * Unescapes html string.
	 * Uses XmlSlurper as the simplest way.
	 *
	 * @param html html
	 * @return raw string without html specific words
	 */
	static String unescapeHtml(String html) {
		new XmlSlurper().parseText("<t>${html.trim().replaceAll('\\&nbsp;', '')}</t>")
	}

	/**
	 * @param file file to resolve path
	 * @return canonical file path with '/' as separator and without leading slash for linux
	 */
	static String noRootFilePath(File file) {
		String path = file.canonicalPath.replaceAll('\\\\', '/')
		path.startsWith('/') ? path[1..-1] : path
	}

	/**
	 * @param file file
	 * @return file link to use in console output
	 */
	static String toConsoleLink(File file) {
		return "file:///${noRootFilePath(file)}"
	}
}
