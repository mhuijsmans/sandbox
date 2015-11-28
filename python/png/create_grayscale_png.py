#!/usr/bin/python

# ref: https://pythonhosted.org/pypng/ex.html

import png

p1 = [(1, 2, 3), (256, 1024, 256)]
with open('grayscale_3c_2r_16bd.png', 'wb') as f:
    nrOfColums = 3
    nrOfRows = 2
    w = png.Writer(nrOfColums, nrOfRows, greyscale=True, bitdepth=16)
    w.write(f, p1)

p2 = [(1, 2, 3), (4, 5, 6)]
with open('grayscale_3c_2r_4bd.png', 'wb') as f:
    nrOfColums = 3
    nrOfRows = 2
    w = png.Writer(nrOfColums, nrOfRows, greyscale=True, bitdepth=4)
    w.write(f, p2)
