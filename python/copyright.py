import os
from glob import glob

copyright = [
"/**",
" * Copyright The last Company 2015",
" *",
" * All rights are reserved. Reproduction or transmission in whole or in part, in",
" * any form or by any means, electronic, mechanical or otherwise, is prohibited",
" * without the prior written consent of the copyright owner.",
" *",
" * Filename: ###.java",
" */",
""
]

# The fingerprint line is used to determine if file has copyright notice
fingerprintline = " * Copyright The last Company "

def updateCopyRight(file, lines):
    print 'Updating file=%s' % file
    head, tail = os.path.split(file)
    classname = tail.split('.java')[0]
    # print 'check file=%s classname=%s' % (file, classname)
    newLines = list(copyright)
    newLines[7] = newLines[7].replace('###',classname)
    # print newLines
    newLines.extend(lines)
    data = "\n".join( (item for item in newLines) )
    #print data
    with open(file, "w") as text_file:
        text_file.write(data)

def checkCopyright(file):
    lines = []
    with open(file) as f:
        lines = f.read().splitlines()
    if len(lines) > 1:
        if not lines[1].startswith(fingerprintline):
            updateCopyRight(file, lines)

##########
# MAIN
##########

files = []
start_dir = os.getcwd()
pattern   = "*.java"

for dir,_,_ in os.walk(start_dir):
    files.extend(glob(os.path.join(dir,pattern)))

# print files

for file in files:
    checkCopyright(file)