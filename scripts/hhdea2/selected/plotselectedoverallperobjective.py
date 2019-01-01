#!/usr/bin/env python

import pandas
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns


probs=(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
for m in [5, 10, 15]:

	avg=np.empty([15, 9]) # 15 problems, 9 algorithms
	for prob in probs: # MaF01 to MaF15
		p="MaF{0:0=2d}".format(prob)
		data=pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/HHdEA2/'+p+'/selected', 
			header=None, delim_whitespace=True)

		for i in range(0,9):
			avg[prob-1,i]=(data==i).sum().mean()/len(data)
			

	label=("SPEA2","MOEAD","NSGAII","MOEADD","MOMBI2","NSGAIII","ThetaDEA","SPEA2SDE","HypE")

	cmap = sns.cubehelix_palette(50, hue=0.05, rot=0, light=0.9, dark=0, as_cmap=True)
	sns.heatmap(avg.transpose(), cmap=cmap)
	plt.xticks(np.arange(len(probs)), ["MaF{0:0=2d}".format(prob) for prob in probs], rotation=30)
	plt.yticks(np.arange(9)+.5, ("SPEA2","MOEAD","NSGAII","MOEADD","MOMBI2","NSGAIII","ThetaDEA","SPEA2SDE","HypE"), rotation=0)
	plt.title("Overall count of application (m="+str(m)+")")
	# plt.show()
	plt.savefig("img/selected/overall"+str(m)+".eps")
	plt.clf()

	# plt.plot(avg)
	# plt.legend(label, ncol=3)
	# # plt.xlabel("problem instance")
	# plt.ylabel("count of application")
	# title=str(m)+" objectives"
	# plt.title(title)
	# plt.xticks(np.arange(15), ["MaF{0:0=2d}".format(prob) for prob in range(1, 16)], rotation=30)
	# plt.grid(True)
	# # plt.show()
	# plt.savefig("img/selectedoverall_"+title+".eps")
	# plt.clf()
	# print(title)

