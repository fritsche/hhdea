#!/usr/bin/env python

import pandas
import matplotlib.pyplot as plt
import numpy as np
import os

for alg in ["HHdEA3", "HHdEA2"]:
	for m in [5, 10, 15]:
		avg=np.empty([15, 9]) # 15 problems, 9 algorithms
		for prob in range(1, 16): # MaF01 to MaF15
			p="MaF{0:0=2d}".format(prob)
			data=pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/'+alg+'/'+p+'/selected', 
				header=None, delim_whitespace=True)
			for i in range(0,9):
				avg[prob-1,i]=(data==i).sum().mean()/len(data)
		label=("SPEA2","MOEAD","NSGAII","MOEADD","MOMBI2","NSGAIII","ThetaDEA","SPEA2SDE","HypE")
		plt.plot(avg)
		plt.legend(label, ncol=3)
		# plt.xlabel("problem instance")
		plt.ylabel("count of application")
		title=str(m)+" objectives"
		plt.title(title)
		plt.ylim([0, 0.7])
		plt.xticks(np.arange(15), ["MaF{0:0=2d}".format(prob) for prob in range(1, 16)], rotation=30)
		plt.grid(True)
		# plt.show()
		directory="img/"+alg+"/"
		if not os.path.exists(directory):
		    os.makedirs(directory)
		plt.savefig(directory+"/"+alg+"selectedoverall_"+title+".eps")
		plt.clf()
		print(title)
