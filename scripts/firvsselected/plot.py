#!/usr/bin/env python

import pandas
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns

probs=(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
m=10
data = np.empty([9, len(probs)])
# MaF01 to MaF15, but MaF08, MaF06 (they cause a bug in FIR computing _probably a division by zero_)
j=-1
for prob in probs: 
# for prob in range(1, 16): # MaF01 to MaF15
# prob=1
	j+=1
	p="MaF{0:0=2d}".format(prob)
	print(p)
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
		data[i, j] = ((selected==i) & (improvement>0)).mean().mean()

cmap = sns.cubehelix_palette(50, hue=0.05, rot=0, light=0.9, dark=0, as_cmap=True)
sns.heatmap(data, cmap=cmap)
plt.xticks(np.arange(len(probs)), ["MaF{0:0=2d}".format(prob) for prob in probs], rotation=30)
plt.yticks(np.arange(9)+.5, ("SPEA2","MOEAD","NSGAII","MOEADD","MOMBI2","NSGAIII","ThetaDEA","SPEA2SDE","HypE"), rotation=0)
plt.title("Overall improvement when MOEA is applied (m="+str(m)+")")
# plt.show()
plt.savefig("img/firvsselected/overall"+str(m)+".eps")
plt.clf()
