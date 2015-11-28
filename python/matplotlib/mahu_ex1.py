# ref: http://stackoverflow.com/questions/2397791/how-can-i-show-figures-separately-in-matplotlib
# when run, this will show 2 windows.
import matplotlib.pyplot as plt
f1 = plt.figure()
f2 = plt.figure()
ax1 = f1.add_subplot(111)
ax1.plot(range(0,10))
ax2 = f2.add_subplot(111)
ax2.plot(range(10,20))
plt.show()