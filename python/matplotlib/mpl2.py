# ref: http://www.wellho.net/resources/ex.php?item=y118/mpl2.py

import matplotlib.pyplot as plt

# Plotting a user data set.

# Gather a count of the accesses to our web server each
# hour of the day to make up a 24 point even spaced set

import re
hourfinder = re.compile(r'\d\d/\w\w\w/\d\d\d\d:(\d\d):\d\d:\d\d')
counter = [0] * 24
# use fo xreadline not recommended:
# ref: http://stackoverflow.com/questions/8555722/difference-between-xreadlines-and-for-looping-a-file
for hit in open("ac_20101007.txt").xreadlines():
        #hour = int((hourfinder.findall(hit))[0])
        hour = int(hit)
        counter[hour] += 1

# Basic plot of that data using all defaults - but
# with a minimum of labels and titles to make it useful

plt.plot(counter)
plt.ylabel('Accesses per hour')
plt.xlabel('Hour of the day')
plt.title('www.wellho.net daily accesses')
plt.show()