#!/usr/bin/env python

import pandas
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns

# for each objective number
m=5
data=np.empty([9, 15])
# MaF01 to MaF15, but MaF08, MaF06 (they cause a bug in FIR computing _probably a division by zero_)
for prob in (1, 2, 3, 4, 5, 7, 9, 10, 11, 12, 13, 14, 15): 
# for prob in range(1, 16): # MaF01 to MaF15
# prob=1
	p="MaF{0:0=2d}".format(prob)
	di=pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/HHdEA2/'+p+'/moeasfir.0',
		header=None, delim_whitespace=True).mean(axis=1)
	improvement=np.empty([di.shape[0], 20])
	improvement[:,0] = di
	for i in range(1,20):
		improvement[:,i] = pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/HHdEA2/'+p+'/moeasfir.'+str(i),
			header=None, delim_whitespace=True).mean(axis=1)

	selected=pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/HHdEA2/'+p+'/selected',
		header=None, delim_whitespace=True)

	# for each MOEA
	for i in range(0,9):
		# data[i, prob-1]=np.corrcoef((selected==i).mean(axis=1), improvement.mean(axis=1))[0,1]
		data[i, prob-1]=((selected==i).mean(axis=1)*improvement.mean(axis=1)).mean()

print(data)
sns.heatmap(data)
#plt.plot(data.transpose(), 'o')
plt.show()
