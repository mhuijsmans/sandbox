This project explored different logging implementations:
- java.util.Logging
- log4j (todo)
- sl4j (http://www.slf4j.org/)
- apache common logging (compareble to sl4j)

PROBLEM
My problem with java.util.logging is if you do it the wrong way, the impl
silently ignores what you tell, leaving you without a clue.

Configuration file:
-Djava.util.logging.config.file="logging.properties" 
In that file, include a line like this:
.level= INFO
This sets the global level, which can be overridden for specific handlers and loggers. 
For example, a specific logger's level can be overridden like this:
com.xyz.foo.level = SEVERE
You can get a template for a logging properties file from jre6\lib\logging.properties.
That is included in the resources directory
 
Todo:
- logfile management, roll-over, disk management
- different logfiles
- session based 
- mdc: https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/MDC.html

Links:
- Java util logging: http://docs.oracle.com/javase/6/docs/technotes/guides/logging/overview.html 
- http://www.java-logging.com/
- Log4j and Syslog: logging in a distributed system, using syslog
  https://blog.trifork.com/2010/01/14/logging-to-the-syslog-from-a-java-application/
- A logging solution from redhat
  https://access.redhat.com/site/documentation/en-US/Red_Hat_Certificate_System/8.1/html/Admin_Guide/java-logs.html
- syslog implementation in Java: http://syslog4j.org/  
- Toolset: Storage: Logstash: http://logstash.net/ 
           Analyse: Elasticsearch: http://www.elasticsearch.org/
		   Dashboard: http://www.elasticsearch.org/overview/kibana/ 
		   All 3 together : http://www.trifork.nl/en/home/solutions/Logstash---Kibana.html 