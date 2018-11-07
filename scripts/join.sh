#!/bin/bash

path=experiment/MaFMethodology/5/output/HHdEA2/
filename="selected"
begin=0
end=19
interval="$begin..$end"

for p in `seq -w 1 15`; do
	eval "paste $path/MaF$p/$filename.{$interval} | column -s $'\t' -t > $path/MaF$p/$filename"
done