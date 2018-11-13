#!/usr/bin/env python

import pandas
import matplotlib.pyplot as plt
import numpy as np


for m in [5, 10, 15]:
	for p in range(1, 16):
		p="MaF{0:0=2d}".format(p)
		data=pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/HHdEA2/'+p+'/selected', 
			header=None, delim_whitespace=True)

		result=[]
		for i in range(0,9):
			result.append([x for x in (data==i).cumsum().mean(axis=1)])

		result=np.array(result).transpose()

		label=("SPEA2","MOEAD","NSGAII","MOEADD","MOMBI2","NSGAIII","ThetaDEA","SPEA2SDE","HypE")

		plt.plot(result)
		plt.legend(label)
		plt.xlabel("iterations")
		plt.ylabel("accumulated count of application")
		title=p+"("+str(m)+")"
		plt.title(title)
		# plt.show()
		plt.savefig("img/selected_"+title+".eps")
		plt.clf()
		print(title)

