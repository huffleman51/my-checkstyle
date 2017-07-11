package com.lmig.dna.gradle.plugin.quality

import org.gradle.testkit.runner.BuildResult

/**
 * @author Mark McCann
 */
class MultiModuleUseKitTest extends AbstractKitTest {

	def "Check java checks"() {
		setup:
		build("""
            plugins {
                    id 'com.lmig.dna.quality'
            }

            subprojects {
                apply plugin: 'java'
                apply plugin: 'com.lmig.dna.quality'

                quality {
                    strict false
                    findbugs = false
                    pmd = false
                }

                repositories {
                    maven { url 'https://pi-artifactory.lmig.com/artifactory/maven' } //required for testKit run
                }
            }
        """)

		file('settings.gradle') << "include 'mod1', 'mod2'"

		fileFromClasspath('mod1/src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')
		fileFromClasspath('mod2/src/main/java/sample/Sample.java', '/com/lmig/dna/gradle/plugin/quality/java/sample/Sample.java')

		when: "run check for both modules"
		BuildResult result = run('check')

		then: "violations detected in module only"
		result.output.replaceAll("Total time: .*", "").replaceAll("See the report at: file.*", "").replaceAll("\r", '').trim() == """:mod1:compileJava
:mod1:processResources UP-TO-DATE
:mod1:classes
:mod1:checkstyleMain
Checkstyle rule violations were found. 

9 Checkstyle rule violations were found in 1 files

[Misc | NewlineAtEndOfFile] sample.(Sample.java:0)
  File does not end with a newline.
  http://checkstyle.sourceforge.net/config_misc.html#NewlineAtEndOfFile

[Header | RegexpHeader] sample.(Sample.java:1)
  Line does not match expected header line of '^\\Q/*\\E\$'.
  http://checkstyle.sourceforge.net/config_header.html#RegexpHeader

[Javadoc | JavadocType] sample.(Sample.java:3)
  Missing a Javadoc comment.
  http://checkstyle.sourceforge.net/config_javadoc.html#JavadocType

[Regexp | RegexpSinglelineJava] sample.(Sample.java:5)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

[Regexp | RegexpSinglelineJava] sample.(Sample.java:7)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

[Regexp | RegexpSinglelineJava] sample.(Sample.java:8)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

[Regexp | RegexpSinglelineJava] sample.(Sample.java:9)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

[Regexp | RegexpSinglelineJava] sample.(Sample.java:11)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

[Regexp | RegexpSinglelineJava] sample.(Sample.java:13)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

:mod1:compileTestJava UP-TO-DATE
:mod1:processTestResources UP-TO-DATE
:mod1:testClasses UP-TO-DATE
:mod1:checkstyleTest UP-TO-DATE
:mod1:test UP-TO-DATE
:mod1:check
:mod2:compileJava
:mod2:processResources UP-TO-DATE
:mod2:classes
:mod2:checkstyleMain
Checkstyle rule violations were found. 

9 Checkstyle rule violations were found in 1 files

[Misc | NewlineAtEndOfFile] sample.(Sample.java:0)
  File does not end with a newline.
  http://checkstyle.sourceforge.net/config_misc.html#NewlineAtEndOfFile

[Header | RegexpHeader] sample.(Sample.java:1)
  Line does not match expected header line of '^\\Q/*\\E\$'.
  http://checkstyle.sourceforge.net/config_header.html#RegexpHeader

[Javadoc | JavadocType] sample.(Sample.java:3)
  Missing a Javadoc comment.
  http://checkstyle.sourceforge.net/config_javadoc.html#JavadocType

[Regexp | RegexpSinglelineJava] sample.(Sample.java:5)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

[Regexp | RegexpSinglelineJava] sample.(Sample.java:7)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

[Regexp | RegexpSinglelineJava] sample.(Sample.java:8)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

[Regexp | RegexpSinglelineJava] sample.(Sample.java:9)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

[Regexp | RegexpSinglelineJava] sample.(Sample.java:11)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

[Regexp | RegexpSinglelineJava] sample.(Sample.java:13)
  Line has leading space characters; indentation should be performed with tabs only.
  http://checkstyle.sourceforge.net/config_regexp.html#RegexpSinglelineJava

:mod2:compileTestJava UP-TO-DATE
:mod2:processTestResources UP-TO-DATE
:mod2:testClasses UP-TO-DATE
:mod2:checkstyleTest UP-TO-DATE
:mod2:test UP-TO-DATE
:mod2:check

BUILD SUCCESSFUL"""
	}
}