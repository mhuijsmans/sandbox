#!/usr/bin/python

import os
cmd = 'wkhtmltopdf helloworld.html helloworld.pdf'
os.system(cmd)

# Next command doesn't work.
# generated error: Error: This version of wkhtmltopdf is build against an unpatched version of QT, and does not support more then one input document.
# Solution exist in the internet, requring patching qt
# cmd = 'wkhtmltopdf cover cover.html toc helloworld.html helloworld_toc.pdf'
# os.system(cmd)
# cmd = 'wkhtmltopdf toc helloworld.html helloworld_toc.pdf'
# os.system(cmd)