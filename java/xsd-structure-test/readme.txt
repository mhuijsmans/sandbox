Introduction
------------
This project explores include and namespaces in XSD/XML

Experiences
-----------
- jabx2-maven-plugin with multiple execution
  <schemaFiles>....</schemaFiles> worked
- "@XmlRootElement annotation missing" explained here:  
  https://weblogs.java.net/blog/2006/03/03/why-does-jaxb-put-xmlrootelement-sometimes-not-always
  
Differemce
jabx2-maven-plugin uses serces plugin
maven-jaxb2-plugin uses jdk built in JAXB   

References
----------
Include / import examples:
http://www.datypic.com/books/defxmlschema/chapter04.html

NamesSpaces & global.xjb to specify java binding rules.
http://www.journaldev.com/1312/how-to-generate-java-classes-from-xsd-using-xjc-maven-plugin

Also xjb
http://seanshou.blogspot.nl/2013/04/practice-of-customize-jaxb-schema-2.html

maven-jabx2-plugin
http://confluence.highsource.org/display/MJIIP/Maven+JAXB2+Plugin