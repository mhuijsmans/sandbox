import matplotlib.pylab as plt
import numpy as np
#define the figure and get an axes instance
# ref: http://stackoverflow.com/questions/4700614/how-to-put-the-legend-out-of-the-plot
fig = plt.figure()
ax = fig.add_subplot(111)
#plot the data
x = np.arange(-5, 6)
ax.plot(x, x*x, label='y = x^2')
ax.plot(x, x*x*x, label='y = x^3')
ax.legend().draggable()
plt.show()