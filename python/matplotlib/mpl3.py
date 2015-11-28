# http://www.wellho.net/resources/ex.php?item=y118/mpl3.py

import matplotlib.pyplot as plt

# Graphing multiple data sets against the same X axis

# Collect the data - 7 days of web access logs, accesses in each hour

import re
hourfinder = re.compile(r'\d\d/\w\w\w/\d\d\d\d:(\d\d):\d\d:\d\d')
counter = []
for day in range(3):
        counter.append([0] * 24)
        for hit in open("ac_2010100_" + str(day+1)+'.txt').xreadlines():
                # hour = int((hourfinder.findall(hit))[0])
                hour = int(hit)
                counter[day][hour] += 1

# colours - see http://matplotlib.sourceforge.net/api/colors_api.html
# c,m,y,k,w r,g,b, also takes HTML names ...
# markers  - 0 . v ^ s p * + etc
# line styles are - -. and -- applied to single letter colours

# Multiple plots against an X range

xscale = range(0,24)
plt.plot(xscale,counter[0],"brown",
                xscale,counter[1],"r.-",
                xscale,counter[2],"gv-.",
                # number of colors must match number of samples
                # xscale,counter[3],"salmon",
                # xscale,counter[4],"magenta",
                # xscale,counter[5],"navy",
                # xscale,counter[6],"black"
                )

# Specific axis choice (get rid of default compression on Y!)

plt.axis([0,24,0,10])

plt.ylabel('Accesses per hour')
plt.xlabel('Hour of the day')
plt.title('www.wellho.net daily accesses')
plt.show()