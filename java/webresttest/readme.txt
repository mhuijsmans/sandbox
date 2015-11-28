Introduction
------------
This project explores rest + web (tomcat) + spring

System
------
any

Experiences
-----------
- with servlet 3.0 annotation for servlets are supported. Implication is that when spring is on classpath
  that the spring servlet is found. This is case when when nothing is specified in web.xml fo spring.   

Run
---
To start tomcat
> mvn tomcat7:run

Access Webpage (via MainServlet)
http://localhost:8080/webresttest/main
Format: http://localhost:8080/<artifactId>/<MainServlet urlPatterns>

Access RestService 
http://localhost:8080/webresttest/rest/webservice/hello
where
- webresttest is <artifactiId>
- rest is @ApplicationPath defined in RestMainServlet
- webservice is @Path defined in WebController
- hello is (sub-)@Path defined in WebController

Todo
----
log4j.xml is in resources folder. Not used yet.

References
----------
tomcat-maven-plugin
http://www.avajava.com/tutorials/lessons/how-do-i-deploy-a-maven-web-application-to-tomcat.html

Starting with Tomcat7, tomcat can be embedded in war/jar.
http://tomcat.apache.org/maven-plugin-2.2/executable-war-jar.html

Rest 2.0, Tomcat 7 example (from which I copied)
http://www.dineshonjava.com/2013/06/restful-web-services-with-jersey-jax-rs.html#.U8bv3vmSwyI  

Example (with e.g. Batis, transaction handling) from whch I borrowed parts
http://www.codingpedia.org/ama/restful-web-services-example-in-java-with-jersey-spring-and-mybatis/ 

Spring auto wiring details
http://docs.spring.io/spring/docs/3.0.0.M3/reference/html/ch04s12.html
