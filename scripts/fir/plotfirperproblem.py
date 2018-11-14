#!/usr/bin/env python

import pandas
import matplotlib.pyplot as plt
import numpy as np


# for m in [5, 10, 15]:
	# for p in range(1, 16):

m=5
p=8

p="MaF{0:0=2d}".format(p)
data=pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/HHdEA2/'+p+'/fir', 
	header=None, delim_whitespace=True)

print(data)


# plt.plot(data.mean(axis=1).cumsum())
# plt.xlabel("iterations")
# plt.ylabel("accumulated fitness improvement rate of R2 of the executed MOEA")

# title=p+"("+str(m)+")"
# plt.title(title)
# plt.show()
# plt.savefig("img/firperproblem_"+title+".eps")
# plt.clf()
# print(title)

