#!/usr/bin/python

import os
cmd = 'pdflatex hello.tex'
os.system(cmd)

# running pdflatex twice seems to be required to get a good report
cmd = 'pdflatex report.tex ; pdflatex report.tex'
os.system(cmd)