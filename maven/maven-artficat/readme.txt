The objective of this project is explore main and attached artifacts.

The maven-install-plugin allows to install new artifacts with a pom. 
Looking at the .m2 repo I conclude that the new artifact is link to old artifact.
Futher detail are unclear.

The maven-javadoc-plugin generates a true attached artifact, that is installed 
with the main artifact in a m2 repo.

Class org.apache.maven.plugin.javadoc.JavadocJar
Includes following code which attaches the javaDoc jar to the current project
     projectHelper.attachArtifact( project, "javadoc", getClassifier(), outputFile );

