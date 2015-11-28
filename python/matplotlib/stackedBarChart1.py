#!/usr/bin/env python
# a stacked bar plot
# ref: http://matplotlib.org/1.3.1/api/pyplot_api.html
import numpy as np
import matplotlib.pyplot as plt

N = 5
menMeans   = (20, 35, 30, 35, 27)
womenMeans = (25, 32, 34, 20, 25)
childrenMeans = (15, 12, 24, 10, 35)
ind = np.arange(N)    # the x locations for the groups
width = 0.35       # the width of the bars: can also be len(x) sequence

p1 = plt.bar(ind, menMeans,   width, color='r')
p2 = plt.bar(ind, childrenMeans,   width, color='b')
p3 = plt.bar(ind, womenMeans, width, color='y', bottom=menMeans)

plt.ylabel('Scores')
plt.title('Scores by group and gender')
plt.xticks(ind+width/2., ('G1', 'G2', 'G3', 'G4', 'G5') )
plt.yticks(np.arange(0,81,10))
plt.legend( (p1[0], p2[0], p3[0]), ('Men', 'children', 'Women') )

plt.show()