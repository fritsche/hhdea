#!/usr/bin/env python

import os
import pandas
import matplotlib.pyplot as plt
import numpy as np

for alg in ["HHdEA3", "HHdEA2"]:
	for m in [5, 10, 15]:
		for p in range(1, 16):
			p="MaF{0:0=2d}".format(p)
			data=pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/'+alg+'/'+p+'/selected', 
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
			directory="img/"+alg+"/"
			if not os.path.exists(directory):
			    os.makedirs(directory)
			plt.savefig(directory+"/"+alg+"selectedperproblem_"+title+".eps")
			plt.clf()
			print(title)

