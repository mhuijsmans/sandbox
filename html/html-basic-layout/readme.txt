This project explores 
- setup of a basic HTML page
- with related CSS style sheets
- with integration of REST interface 

To start jetty
> mvn jetty:run

To start tomcat
> mvn tomcat7:run
ref: http://tomcat.apache.org/maven-plugin-2.0/tomcat7-maven-plugin/plugin-info.html
BUT that does not gave same result as jetty:run. No wegpage is shown.

Next use this link to access the basic page
http://localhost:8080/basic_page.html 

Browser
-------
I observed that Chrome sometimes doesn't display the updates subpage, 
even after restarting Chromse.