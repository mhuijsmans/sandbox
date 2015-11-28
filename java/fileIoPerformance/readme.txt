Introduction
------------

System
------

Experiences
-----------

Conclusion: memoryMappedFile & testRandomAccessWithDirectNio are the fastest.
Note: test includes a read-loop to touch all bytes in the buffer

Created file: Filesize 30.300.000
testWithoutNioReadLines,     elapsed: 266
testWithoutNioReadAllAtOnce, elapsed: 88
testRandomAccessWithDirectNio, isDirect   true, isReadonly false
testRandomAccessWithDirectNio, elapsed: 20
testReadFileWithFixedSizeNioBuffer, elapsed: 87
testReadFileWithNioMappedByteBuffer, isDirect   true, isLoaded   false, isReadonly true
testReadFileWithNioMappedByteBuffer, elapsed: 59

testWithoutNioReadLines, elapsed: 151
testWithoutNioReadAllAtOnce, elapsed: 63
testRandomAccessWithDirectNio, isDirect   true, isReadonly false
testRandomAccessWithDirectNio, elapsed: 20
testReadFileWithFixedSizeNioBuffer, elapsed: 79
testReadFileWithNioMappedByteBuffer, isDirect   true, isLoaded   false, isReadonly true
testReadFileWithNioMappedByteBuffer, elapsed: 38

testWithoutNioReadLines, elapsed: 147
testWithoutNioReadAllAtOnce, elapsed: 90
testRandomAccessWithDirectNio, isDirect   true, isReadonly false
testRandomAccessWithDirectNio, elapsed: 25
testReadFileWithFixedSizeNioBuffer, elapsed: 49
testReadFileWithNioMappedByteBuffer, isDirect   true, isLoaded   false, isReadonly true
testReadFileWithNioMappedByteBuffer, elapsed: 31

testWithoutNioReadLines, elapsed: 117
testWithoutNioReadAllAtOnce, elapsed: 80
testRandomAccessWithDirectNio, isDirect   true, isReadonly false
testRandomAccessWithDirectNio, elapsed: 29
testReadFileWithFixedSizeNioBuffer, elapsed: 46
testReadFileWithNioMappedByteBuffer, isDirect   true, isLoaded   false, isReadonly true
testReadFileWithNioMappedByteBuffer, elapsed: 30

testWithoutNioReadLines,             elapsed: 113
testWithoutNioReadAllAtOnce,         elapsed: 58
testRandomAccessWithDirectNio, isDirect   true, isReadonly false
testRandomAccessWithDirectNio,       elapsed: 24
testReadFileWithFixedSizeNioBuffer,  elapsed: 47
testReadFileWithNioMappedByteBuffer, isDirect   true, isLoaded   false, isReadonly true
testReadFileWithNioMappedByteBuffer, elapsed: 30

References
----------