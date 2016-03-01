#!/usr/bin/python

import re

countExpression = '([0-9]+$)'
countRegExpr = re.compile(countExpression)
line = '[2014-12-14 13:00:11.837681] [0x000007f36172a788] [debug]   abc::Buffer::Put() NrOfEntries: 21'
count = countRegExpr.search(line).group(1)
print 'count: %s' % count



item = '(\[([A-Za-z0-9_ -:\.]+)\])'
countExpression = '%s%s%s(.*)$' % (item, item, item)
countRegExpr = re.compile(countExpression)
line = 'ignore this text[2014-12-14 13:00:11.837681][0x000007f36172a788][debug]   abc::Buffer::Put() NrOfEntries: 21'
match = countRegExpr.search(line)
if match:
    print 'match'
    print match.groups()[1]
    print match.groups()[3]
    print match.groups()[5]
    print match.groups()[6]
else:
    print 'no match'