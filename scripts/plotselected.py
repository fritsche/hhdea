#!/usr/bin/env python

import pandas
import matplotlib.pyplot as plt
import numpy as np

data=pandas.read_csv('experiment/MaFMethodology/5/output/HHdEA2/MaF05/selected', header=None, delim_whitespace=True)

result=[]
for i in range(0,9):
	result.append([x for x in (data==i).cumsum().mean(axis=1)])

result = np.array(result)

result=result.transpose()

plt.plot(result)
plt.show()
