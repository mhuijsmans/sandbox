import matplotlib.pyplot as plt

import re
hourfinder = re.compile(r'\d\d/\w\w\w/\d\d\d\d:(\d\d):\d\d:\d\d')

counter = []

for day in range(4):
        counter.append([0] * 24)
        for hit in open("ac_2010100_" + str(day+1)+'.txt').xreadlines():
                #hour = int((hourfinder.findall(hit))[0])
                hour = int(hit)
                counter[day][hour] += 1
xscale = range(0,24)

plt.figure()

# Two plots - "Two plots vertical, one horizonal, first plot"
plt.subplot(211)

plt.plot(xscale,counter[0],"salmon",
                xscale,counter[3],"r*-")
plt.axis([0,24,0,10])
plt.ylabel('Accesses per hour')
plt.title('Weekday (top) and weekend (bottom)')

# Two plots - "Two plots vertical, one horizonal, second plot"

plt.subplot(212)
plt.plot(xscale,counter[1],"gp",
                xscale,counter[2],"black")
plt.axis([0,24,0,10])
plt.ylabel('Accesses per hour')
plt.xlabel('Hour of the day')

plt.show()