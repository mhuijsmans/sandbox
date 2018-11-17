This project explores if a resource (file or class) present in a jar J1 or J2 can be read maven project P that has a dependency on J1 and J2.
J1 and J2 have the same structure, but different files.

Observed: For P there is no difference if the resource in own or other jar.

This project can be run from eclipse, but is intended to run from commandline.
- install jarresource 1 and jarresource2 in local mvn repo using 'mvn install'
- next run jarresource-user using 'mvn test'

Printed output
file:/C:/Users/310160231/git/sandbox/java/jarresource/jarresource1/target/classes/file1-1.txt
file:/C:/Users/310160231/git/sandbox/java/jarresource/jarresource1/target/classes/data/file1-2.txt
file:/C:/Users/310160231/git/sandbox/java/jarresource/jarresource1/target/classes/org/mahu/proto/jarresource/JarResource1.class
file:/C:/Users/310160231/git/sandbox/java/jarresource/jarresource2/target/classes/file2-1.txt
file:/C:/Users/310160231/git/sandbox/java/jarresource/jarresource2/target/classes/data/file2-2.txt
file:/C:/Users/310160231/git/sandbox/java/jarresource/jarresource2/target/classes/org/mahu/proto/jarresource/JarResource2.class