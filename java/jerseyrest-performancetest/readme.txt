Introduction
------------
This project explores characteristics of jersey rest

System
------

Experiences
-----------

Measurements JAX-RS
-------------------
Image=start.png; size=23344; Elapsed=470 ms; max=30; avg=16 ms
Image=weird.png; size=7695089; Elapsed=2500 ms; max=30; avg=84 ms
Image=big-weird.png; size=23666731; Elapsed=5743 ms; max=30; avg=192 ms
Image=big-weird-compressionLevel9.png; size=12372271; Elapsed=3169 ms; max=30; avg=106 ms

Image=start.png; size=23344; Elapsed=226 ms; max=1; avg=226 ms
Image=weird.png; size=7695089; Elapsed=139 ms; max=1; avg=139 ms
Image=big-weird.png; size=23666731; Elapsed=448 ms; max=1; avg=448 ms
Image=big-weird-compressionLevel9.png; size=12372271; Elapsed=247 ms; max=1; avg=247 ms

Reading 23.000.00 bytes + wrap byte[] into a NioBuffer
Image=start.png; size=23000000; Elapsed=2585 ms; max=10; avg=259 ms kb/sec: 86889

Reading 23.000.00 bytes + copy to NioBuffer (through small buffer)
Image=start.png; size=23000000; Elapsed=2323 ms; max=10; avg=233 ms kb/sec: 96689

Measurements HttpUrlConnection: 1 thread
----------------------------------------
With NIO wrap, size=23000000; Elapsed=9620 ms; max=50; avg=193 ms kb/sec: 116740
With NIO copy, size=23000000; Elapsed=16485 ms; max=50; avg=330 ms kb/sec: 68125

Measurements HttpUrlConnection: 4 thread
----------------------------------------
With NIO wrap, size=23000000; Elapsed=143514 ms; max=800; avg=180 ms kb/sec: 125205

Measurement ApacheHttpClient (default configuration)
-----------------------------------------------------
With NIO wrap, size=23000000; Elapsed=6124 ms; max=30; avg=205 ms kb/sec: 110030
With NIO copy, size=23000000; Elapsed=10051 ms; max=30; avg=336 ms kb/sec: 67040

Measurement ApacheAsyncHttpClient: with ByteBuffer (but NOT direct)
-------------------------------------------------------------------
Size=23000000; Elapsed=9746 ms; max=50; avg=195 ms kb/sec: 115231
Done

References
----------
http://www.ibm.com/developerworks/library/j-zerocopy/index.html