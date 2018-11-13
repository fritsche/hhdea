#!/bin/bash

filename="selected"
begin=0
end=19
interval="$begin..$end"
path=experiment/MaFMethodology/

for m in 5 10 15; do
	for p in `seq -w 1 15`; do
		output=$path/$m/output/HHdEA2/MaF$p/
		eval "paste $output/$filename.{$interval} | column -s $'\t' -t > $output/$filename"
	done
done
