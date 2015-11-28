Introduction
------------
Project toger impression of the throughput of the Java Implementation of TCP.

System
------

Experiences
-----------
In Windows 8 gb, int 5, 2410m
1 process with client and server

clientAndServerInOne           				, received (mb) 16384 in (ms/sc) 30464/30 is mb/sec 546
blockingNioClientAndServerInOne				, received (mb) 16384 in (ms/sc) 29225/29 is mb/sec 564
blockingNioClientAndBlockingNioServerInOne	, received (mb) 16384 in (ms/sc) 27765/27 is mb/sec 606
clientAndServerInOne           				, received (mb) 16384 in (ms/sc) 30758/30 is mb/sec 546
blockingNioClientAndServerInOne				, received (mb) 16384 in (ms/sc) 28491/28 is mb/sec 585
blockingNioClientAndBlockingNioServerInOne	, received (mb) 16384 in (ms/sc) 27664/27 is mb/sec 606
clientAndServerInOne           				, received (mb) 16384 in (ms/sc) 30697/30 is mb/sec 546
blockingNioClientAndServerInOne				, received (mb) 16384 in (ms/sc) 28548/28 is mb/sec 585
blockingNioClientAndBlockingNioServerInOne	, received (mb) 16384 in (ms/sc) 27574/27 is mb/sec 606
clientAndServerInOne           				, received (mb) 16384 in (ms/sc) 31047/31 is mb/sec 528
blockingNioClientAndServerInOne				, received (mb) 16384 in (ms/sc) 28421/28 is mb/sec 585
blockingNioClientAndBlockingNioServerInOne	, received (mb) 16384 in (ms/sc) 27585/27 is mb/sec 606
clientAndServerInOne           				, received (mb) 16384 in (ms/sc) 30820/30 is mb/sec 546
blockingNioClientAndServerInOne				, received (mb) 16384 in (ms/sc) 28912/28 is mb/sec 585
blockingNioClientAndBlockingNioServerInOne	, received (mb) 16384 in (ms/sc) 28014/28 is mb/sec 585
Data Read: 17.179.869.184

2 Seperate proceses
Non-nio
- Received (mb) 8192 in (ms/sc) 14780/14 is mb/sec 585
NIO (NioThroughputTest)
testBlockingNioServer, received (mb) 32768 in (ms/sc) 40576/40 is mb/sec 819

References
----------
