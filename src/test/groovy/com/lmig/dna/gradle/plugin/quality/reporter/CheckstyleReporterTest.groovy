package com.lmig.dna.gradle.plugin.quality.reporter

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import com.lmig.dna.gradle.plugin.quality.AbstractKitTest

/**
 * @author Mark McCann 
 */
class CheckstyleReporterTest extends AbstractKitTest {

	def "Check checkstyle report"() {

		setup: "prepare project"
		build("""
            plugins {
                id 'groovy'
                id 'com.lmig.dna.quality'
            }

            task testReport() << {
                new com.lmig.dna.gradle.plugin.quality.report.CheckstyleReporter(
                    new com.lmig.dna.gradle.plugin.quality.ConfigLoader(project)
                ).report(project, 'main')
            }
        """)
		file('src/main/java').mkdirs()
		String report = getClass().getResourceAsStream('/com/lmig/dna/gradle/plugin/quality/report/checkstyle/main.xml').text
				.replaceAll('\\$\\{srcRoot\\}', file('src/main/java').canonicalPath.replaceAll('\\\\', '\\\\\\\\'))
		File target = file('build/reports/checkstyle/main.xml')
		target.parentFile.mkdirs()
		target << report

		when: "call reporter"
		BuildResult result = run('testReport')
		def error = result.output

		then: "output valid"
		result.task(':testReport').outcome == TaskOutcome.SUCCESS
		error.replaceAll("\r", '').contains """
6 Checkstyle rule violations were found in 2 files

[Misc | NewlineAtEndOfFile] sample.(Sample.java:0)
  File does not end with a newline.
  http://checkstyle.sourceforge.net/config_misc.html#NewlineAtEndOfFile

[Javadoc | JavadocType] sample.(Sample.java:3)
  Missing a Javadoc comment.
  http://checkstyle.sourceforge.net/config_javadoc.html#JavadocType

[Misc | UncommentedMain] sample.(Sample.java:11)
  Uncommented main method found.
  http://checkstyle.sourceforge.net/config_misc.html#UncommentedMain

[Misc | NewlineAtEndOfFile] sample.(Sample2.java:0)
  File does not end with a newline.
  http://checkstyle.sourceforge.net/config_misc.html#NewlineAtEndOfFile

[Javadoc | JavadocType] sample.(Sample2.java:3)
  Missing a Javadoc comment.
  http://checkstyle.sourceforge.net/config_javadoc.html#JavadocType

[Misc | UncommentedMain] sample.(Sample2.java:11)
  Uncommented main method found.
  http://checkstyle.sourceforge.net/config_misc.html#UncommentedMain
""" as String
	}
}