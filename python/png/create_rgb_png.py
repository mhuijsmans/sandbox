#!/usr/bin/python

# ref: https://pythonhosted.org/pypng/ex.html

import png

# use following command to show image properties
# file rgb_1c_1r_16bd_alpha.png

p1 = [(1,256,65534, 2,512,65534, 3,1024,65533),
     (4,2048,65532, 5,4096,65531, 6,8192,65530)]
with open('rgb_3c_2r_16bd.png', 'wb') as f:
    nrOfColums = 3
    nrOfRows = 2
    w = png.Writer(nrOfColums, nrOfRows, bitdepth=16)
    w.write(f, p1)

# next line doesn't seem to work. It the image has bitdepth=8
p2 = [(1,1,1)]
with open('rgb_1c_1r_4bd.png', 'wb') as f:
    nrOfColums = 1
    nrOfRows = 1
    w = png.Writer(nrOfColums, nrOfRows, bitdepth=4)
    w.write(f, p2)

p3 = [(1024,1,1,0)]
with open('rgb_1c_1r_16bd_alpha.png', 'wb') as f:
    nrOfColums = 1
    nrOfRows = 1
    w = png.Writer(nrOfColums, nrOfRows, alpha=True, bitdepth=16)
    w.write(f, p3)

p4 = [(1,1,1),(2,2,2),(3,3,3),(4,4,4)]
with open('rgb_1c_4r_16bd_interlace.png', 'wb') as f:
    nrOfColums = 1
    nrOfRows = 4
    w = png.Writer(nrOfColums, nrOfRows, interlace=True, bitdepth=16)
    w.write(f, p4)

p1 = [(1,1,1, 1023,1023,1023, 65535,65535,65535),
     (0,1,2, 1021,1022,1023, 253,254,255)]
with open('rgb_3c_2r_16bd_pgr.png', 'wb') as f:
    nrOfColums = 3
    nrOfRows = 2
    w = png.Writer(nrOfColums, nrOfRows, bitdepth=16)
    w.write(f, p1)