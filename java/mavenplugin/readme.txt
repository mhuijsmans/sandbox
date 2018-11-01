Introduction
------------
This project explores the creation of a maven plug to generate code.
It includes
- a library that is used by the plugin
- the plugin with test case of generated code
  The plugin is configured to execute during the maven generate-sources phase.
- the plugin user that invokes the plugin to generate a jar with generated code.

Used sources
------------

https://books.sonatype.com/mvnref-book/reference/writing-plugins-sect-custom-plugin.html
Mentions required <packaging> value for a maven plugin: mavenplugin.

http://jenesis4java.sourceforge.net/mojo.html
Provides an example for code generation, which includes adding generated sources to maven project source path  

https://github.com/apache/maven-plugin-tools/blob/maven-plugin-tools-3.5.2/maven-plugin-plugin/pom.xml
The POM of the maven-plugin-plugin uses a previous version of maven-plugin-plugin to generate a new plugin

Observations
------------

Important: The maven-plugin-plugin defines WHEN the maven-plugin-plugin shall be invokes. That is AFTER
the compile step. So (I guess) the maven-plugin-plugin uses the class files to find the annotations.

Important: for testing you need to run as a minimum
> mvn clean test
to ensure that the maven plugin is exected.

Running 
> mvn clean compile 
gives
[ERROR] Failed to parse plugin descriptor for org.mahu.proto.mavenplugin:mavenplugin:1.0-SNAPSHOT (C:\Users\310160231\git\sandbox\java\mavenplugin\plugin\target\classes): 
No plugin descriptor found at META-INF/maven/plugin.xml -> [Help 1]
That makes sense given that the maven-plugin-plugin runs after compile.

So if you want 
> mvn clean compile
to work you need to make the plugin (+ support libraries) a seperate project that can be imported from (local/remote) maven repo

<eod>