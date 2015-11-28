#!/usr/bin/python

# ref: http://www.artima.com/weblogs/viewpost.jsp?thread=236637
from collections import namedtuple

Article = namedtuple("Article", 'title author')
article1 = Article("Records in Python", "M. Simionato")
print article1