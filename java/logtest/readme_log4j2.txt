Home:
http://logging.apache.org/log4j/2.x/

Experiences
-----------
- runs out of the box
- log level for a Logger defined the applied log level.
  This is also valid when an additional the RootLogger is used.
  In that case the RootLogger logLevel is not used.
- without async, logging is done synchronous (which is sometimes preferred, like when I am testing).
- if log4j2.xml content is invalid, exception throw at startup.
- Appender.Console.name = Console seems to be a reserved name.