Introduction
------------
This project explores conversions of pixel data, e.g. like present in PNG.

Experiences
-----------
- with test data, 
  compressionLevel=1 given about 40-45% reduction in size.
  compressionLevel=1 given about 52-55% reduction in size.
  
- ToDo: create uncompressed, next zip.  

Test-run results: from pixel data in memory, create a PNG in memory.
Run is done with different compression levels.
--------------------------------------------------------
Compression level 0 provide information about the PNG size. 
Xrun; image=weird.png; compressionLevel=0; PngOutSize=7685041; Elapsed=1814 ms; max=10; avg=182 ms
Xrun; image=weird.png; compressionLevel=1; PngOutSize=4563791; Elapsed=4376 ms; max=10; avg=438 ms
Xrun; image=weird.png; compressionLevel=2; PngOutSize=4511595; Elapsed=4910 ms; max=10; avg=491 ms
Xrun; image=weird.png; compressionLevel=3; PngOutSize=4447171; Elapsed=7408 ms; max=10; avg=741 ms
Xrun; image=weird.png; compressionLevel=6; PngOutSize=4282150; Elapsed=27593 ms; max=10; avg=2760 ms
Xrun; image=weird.png; compressionLevel=9; PngOutSize=4230174; Elapsed=52962 ms; max=10; avg=5297 ms

XXrun; image=big-weird-compressionLevel9.png; compressionLevel=0; PngOutSize=23639076; Elapsed=5323 ms; max=10; avg=533 ms
XXrun; image=big-weird-compressionLevel9.png; compressionLevel=1; PngOutSize=13451970; Elapsed=12694 ms; max=10; avg=1270 ms
XXrun; image=big-weird-compressionLevel9.png; compressionLevel=2; PngOutSize=13266199; Elapsed=14822 ms; max=10; avg=1483 ms
XXrun; image=big-weird-compressionLevel9.png; compressionLevel=3; PngOutSize=13050463; Elapsed=21981 ms; max=10; avg=2199 ms
XXrun; image=big-weird-compressionLevel9.png; compressionLevel=6; PngOutSize=12570287; Elapsed=79204 ms; max=10; avg=7921 ms
XXrun; image=big-weird-compressionLevel9.png; compressionLevel=9; PngOutSize=12372271; Elapsed=193975 ms; max=10; avg=19398 ms

XXrun; image=big-weird.png; compressionLevel=0; PngOutSize=23639076; Elapsed=5253 ms; max=10; avg=526 ms
XXrun; image=big-weird.png; compressionLevel=1; PngOutSize=13451970; Elapsed=14164 ms; max=10; avg=1417 ms
XXrun; image=big-weird.png; compressionLevel=2; PngOutSize=13266199; Elapsed=14629 ms; max=10; avg=1463 ms
XXrun; image=big-weird.png; compressionLevel=3; PngOutSize=13050463; Elapsed=21876 ms; max=10; avg=2188 ms
XXrun; image=big-weird.png; compressionLevel=6; PngOutSize=12570287; Elapsed=78462 ms; max=10; avg=7847 ms
XXrun; image=big-weird.png; compressionLevel=9; PngOutSize=12372271; Elapsed=191365 ms; max=10; avg=19137 ms

Test-run results
----------------
Warm-up
XXrun; image=big-weird.png; compressionLevel=0; PngOutSize=23639076; Elapsed=6016 ms; max=10; avg=602 ms
XXrun; image=big-weird.png; compressionLevel=1; PngOutSize=13451970; Elapsed=12799 ms; max=10; avg=1280 ms
XXrun; image=big-weird.png; compressionLevel=2; PngOutSize=13266199; Elapsed=14857 ms; max=10; avg=1486 ms
XXrun; image=big-weird.png; compressionLevel=3; PngOutSize=13050463; Elapsed=22088 ms; max=10; avg=2209 ms
XXrun; image=big-weird.png; compressionLevel=6; PngOutSize=12570287; Elapsed=78618 ms; max=10; avg=7862 ms
After warm-up
XXrun; image=big-weird.png; compressionLevel=0; PngOutSize=23639076; Elapsed=5648 ms; max=10; avg=565 ms
XXrun; image=big-weird.png; compressionLevel=1; PngOutSize=13451970; Elapsed=12939 ms; max=10; avg=1294 ms
XXrun; image=big-weird.png; compressionLevel=2; PngOutSize=13266199; Elapsed=14827 ms; max=10; avg=1483 ms
XXrun; image=big-weird.png; compressionLevel=3; PngOutSize=13050463; Elapsed=22112 ms; max=10; avg=2212 ms
XXrun; image=big-weird.png; compressionLevel=6; PngOutSize=12570287; Elapsed=78539 ms; max=10; avg=7854 ms

References
----------
Java PNG library. Apache License 2.0
https://code.google.com/p/pngj/