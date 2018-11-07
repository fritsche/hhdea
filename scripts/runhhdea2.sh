#!/bin/bash

set -e

source ./scripts/seeds.sh
source ./scripts/wait_queue.sh

cd HHdEA
make
cd -

dir=$(pwd)
jar=$dir/HHdEA/target/HHdEA-1.0-SNAPSHOT-jar-with-dependencies.jar
main=br.ufpr.inf.cbio.hhdea.runner.HHdEA2Runner
ms=(5 10 15)
problems=(MaF01 MaF02 MaF03 MaF04 MaF05 MaF06 MaF07 MaF08 MaF09 MaF10 MaF11 MaF12 MaF13 MaF14 MaF15)
runs=20
replace=false
methodology=MaFMethodology
algorithms=(HHdEA2)
experiment=hhdea2

set_seeds

execute="sbatch -J $experiment.run $dir/scripts/addjob.sh" # for running on process queue
# execute=eval # for running locally

seed_index=0

for m in "${ms[@]}"; do
	for problem in "${problems[@]}"; do
		for (( id = 0; id < $runs; id++ )); do
			# each objective, problem and independent run (id) uses a different seed
			seed=${seeds[$seed_index]}
			# different algorithms on the same problem instance uses the same seed
			for algorithm in "${algorithms[@]}"; do
				file="$dir/experiment/$methodology/$m/data/$algorithm/$problem/FUN$id.tsv"
				output="$dir/experiment/$methodology/$m/output/$algorithm/$problem/"
				mkdir -p $output
				if [ ! -s $file ] || [ "$replace" = true ]; then
					javacommand="java -Duser.language=en -cp $jar -Xmx1g $main"
					params="-P $dir/experiment/ -m $m -p $problem -s $seed -id $id"
					cd /tmp
					$execute "$javacommand $params > $output/$id.out" 1> /dev/null
					cd - > /dev/null
				fi
			done
			seed_index=$((seed_index+1))
		done
	done
done

echo "executing algorithms"
wait_queue $experiment

