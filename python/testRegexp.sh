#!/usr/bin/python

import re
countExpression = '([0-9]+$)'
countRegExpr = re.compile(countExpression)

line = '[2014-12-14 13:00:11.837681] [0x000007f36172a788] [debug]   abc::Buffer::Put() NrOfEntries: 21'

count = countRegExpr.search(line).group(1)

print 'count: %s' % count