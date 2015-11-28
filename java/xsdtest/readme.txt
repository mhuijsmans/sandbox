This project 
- generates Java code from XSD
- contains XSD related information
- explored validation (un)marshalling and comparing java xml objects

XSD: restrictions on SimpleType
------------------------------- 
http://www.w3schools.com/schema/schema_facets.asp
Provides overview and examples. 

JAXB generated code does not include restrictions or key-checks.
Validation for marshal and unmarshal can be emnforced in code:
http://blog.bdoughan.com/2010/11/validate-jaxb-object-model-with-xml.html   
http://blog.bdoughan.com/2010/12/jaxb-and-marshalunmarshal-schema.html

XML catalog to enable use of common datatypes across XSD's
---------------------------------------------------------
http://blog.bdoughan.com/2011/10/jaxb-xjc-imported-schemas-and-xml.html

JAXB and Root Elements /  @XmlElementDecl
-----------------------------------------
As I referenced 2 other blog, I also decided to include this one (although I do not 
   get the problem that is solved.). 
http://blog.bdoughan.com/2012/07/jaxb-and-root-elements.html

JAXB version
------------
(copied from: http://mojo.codehaus.org/jaxb2-maven-plugin/usage.html)
As JDK 1.6 includes JAXB 2.1 (starting with JDK 1.6u4), we set the target JAXB runtime platform 
accordingly through the target configuration parameter. JDK 1.6+ may be used to build the project.

Homepage selected maven plugin
------------------------------
http://mojo.codehaus.org/jaxb2-maven-plugin/ 

Latest version
--------------
http://mvnrepository.com/artifact/org.codehaus.mojo/jaxb2-maven-plugin/
 
Which maven-plugin
------------------
I have found different maven-jaxb2-plugin 
		<plugin>
			<groupId>org.jvnet.jaxb2.maven2</groupId>
			<artifactId>maven-jaxb2-plugin</artifactId>
			<version>0.9.0</version>
		</plugin>
		<plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>jaxb2-maven-plugin</artifactId>
                <version>1.6</version>
        </plugin>
Choose: http://mojo.codehaus.org/jaxb2-maven-plugin/usage.html