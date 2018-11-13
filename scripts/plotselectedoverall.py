#!/usr/bin/env python

import pandas
import matplotlib.pyplot as plt
import numpy as np


for m in [5, 10, 15]:

	avg=np.zeros([15, 9]) # 15 problems, 9 algorithms
	for prob in range(1, 16): # MaF01 to MaF15
		p="MaF{0:0=2d}".format(prob)
		data=pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/HHdEA2/'+p+'/selected', 
			header=None, delim_whitespace=True)

		for i in range(0,9):
			avg[prob-1,i]+=(data==i).sum().mean()/len(data)
			

avg=avg/3
label=("SPEA2","MOEAD","NSGAII","MOEADD","MOMBI2","NSGAIII","ThetaDEA","SPEA2SDE","HypE")

plt.plot(avg)
plt.legend(label, ncol=3)
plt.ylim([0, 0.25])
# plt.xlabel("problem instance")
plt.ylabel("count of application")
# title=str(m)+" objectives"
# plt.title(title)
plt.xticks(np.arange(15), ["MaF{0:0=2d}".format(prob) for prob in range(1, 16)], rotation=30)
plt.grid(True)
# plt.show()
plt.savefig("img/selectedoverall.eps")
plt.clf()
# print(title)

