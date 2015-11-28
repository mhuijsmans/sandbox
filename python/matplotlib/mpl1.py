# ref: http://www.wellho.net/resources/ex.php4?item=y118/mpl1.py

""" A first plot - get some numbers, plot them against
a uniform X axis starting at 1 using all the defaults."""

import matplotlib.pyplot as plt

plt.plot([1,23,2,4])
plt.ylabel('some numbers')

# ref savefig: http://stackoverflow.com/questions/9622163/save-plot-to-image-file-instead-of-displaying-it-using-matplotlib-so-it-can-be
plt.savefig('mpl1.png')
plt.savefig('mpl1.pdf')
plt.savefig('mpl1.ps')

plt.show()