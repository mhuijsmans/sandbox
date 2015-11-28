#!/usr/bin/env python
# Working with multiple figure windows and subplots
# ref: http://matplotlib.org/examples/pylab_examples/multiple_figs_demo.html
from pylab import *

t = arange(0.0, 2.0, 0.01)
s1 = sin(2*pi*t)

s2 = sin(4*pi*t)

# first figure has 2*2 grind with plots. Last nr is the plot nr
# explained here: http://stackoverflow.com/questions/3584805/in-matplotlib-what-does-111-means-in-fig-add-subplot111
figure(1)
subplot(221)
plot(t,s1)
subplot(222)
plot(t,2*s1)
subplot(223)
plot(t,3*s1)
subplot(224)
plot(t,4*s1)

# 2nd figure has 1 plot
figure(2)
plot(t,s2)

# switch back to figure 1 and make some changes
figure(1)
subplot(221)
plot(t,s2, 'gs')
setp(gca(), 'xticklabels', [])

figure(1)
savefig('fig1')

figure(2)
savefig('fig2')

show()
