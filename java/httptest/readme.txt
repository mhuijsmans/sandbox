This project explored a simple, HTTP client / embedded server implementation.
It includes both Tomcat & Jetty.

Leasson learned
---------------
- same test case: tomcat is fastest
- Jetty has shutdown hook. Tomcat not. For simple, hook is easy.
  For more complex, e.g. own threads, you need a solution application wide.
  Article in reference describes this.

Reference
---------
Based on this article with sample code
http://www.hascode.com/2013/07/embedding-jetty-or-tomcat-in-your-java-application/

Article that discusses graceful shutdown
http://java.dzone.com/articles/tomcats-graceful-shutdown 