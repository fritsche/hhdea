#!/bin/bash

basename=$1
begin=0
end=19
interval="$begin..$end"
path=experiment/MaFMethodology/

for m in 5 10 15; do
	for p in `seq -w 1 15`; do
		output=$path/$m/output/HHdEA3/MaF$p/
		echo $output
		eval "paste $output/$basename.{$interval} | column -s $'\t' -t > $output/$basename"
	done
done
