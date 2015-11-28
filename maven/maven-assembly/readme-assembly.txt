Multi-module project blog (basic version)
http://blog.soebes.de/blog/2013/09/28/build-smells-maven-assembly-plugin/
+ example project
https://github.com/khmarbaise/assembly-examples/tree/master/assemblies-with-files-ten-mods

I used following example and it "works".
OR not, it doesn't generate what I expect
http://www.petrikainulainen.net/programming/tips-and-tricks/creating-a-runnable-binary-distribution-with-maven-assembly-plugin/

This link describes a number of interesting advanced cases (but not full examples)
https://maven.apache.org/plugins/maven-assembly-plugin/advanced-module-set-topics.html

I used this for user-*.jar, but it doesn't work (I can't find any file):
http://stackoverflow.com/questions/3493381/custom-maven-assembly

-----------------------------------------------------------------------
Deploying an assembly to the maven repo.
2 methods are described.
http://stackoverflow.com/questions/2244344/deploying-assembly-package-with-maven-release-plugin 

from this source: http://maven.40175.n5.nabble.com/Packaging-type-quot-zip-quot-td95333.html
Additional suggestion
>   <execution> 
>     <id>make-assembly</id> 
>     <phase>package</phase> 
>     <goals> 
>       <goal>attached</goal> 
>     </goals> 
>   </execution> 
So whenever i do a mvn install, deploy or even a full 
release I get the attached zip in my repository. 

-----------------------------------------------------------------------

Copying artifacts from repo:
http://maven.apache.org/plugins/maven-dependency-plugin/ 
