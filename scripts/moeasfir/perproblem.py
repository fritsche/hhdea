#!/usr/bin/env python

import pandas
import matplotlib.pyplot as plt
import numpy as np

for m in [5, 10, 15]:
	# MaF01 to MaF15, but MaF08, MaF06 (they cause a bug in FIR computing _probably a division by zero_)
	for prob in (1, 2, 3, 4, 5, 7, 9, 10, 11, 12, 13, 14, 15): 
	# for prob in range(1, 16): # MaF01 to MaF15

		p="MaF{0:0=2d}".format(prob)

		data=pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/HHdEA2/'+p+'/moeasfir.0', header=None, delim_whitespace=True)

		for i in range(1,20):
			data+=pandas.read_csv('experiment/MaFMethodology/'+str(m)+'/output/HHdEA2/'+p+'/moeasfir.'+str(i), header=None, delim_whitespace=True)

		data/=20

		plt.plot(data.cumsum().mean(axis=1))
		plt.xlabel("iterations")
		plt.ylabel("R2 accumulated average improvement")
		title=p+"("+str(m)+")"
		plt.title(title)
		# plt.show()
		plt.savefig("img/moeasfir/perproblem_"+title+".eps")
		plt.clf()
		print(title)
