#!/bin/bash

set -e

source ./experiment/scripts/seeds.sh
source ./experiment/scripts/execute.sh
source ./experiment/scripts/wait_queue.sh
source ./experiment/scripts/indicators.sh
source ./experiment/scripts/hypervolume.sh
source ./experiment/scripts/analysis.sh
source ./experiment/scripts/set_nadir.sh

methodology=MaFMethodology
experiment=moeas # WARNING: max 8 chars (to grep in wait_queue)

algorithms=(SPEA2SDE HypE MOMBI2 MOEADD ThetaDEA NSGAIII MOEAD SPEA2 NSGAII)

# WARNING: changing replace to 'true' will override previous results with the current algorihtm version
replace=false # true: re-execute even if file exists

if [ "$replace" = true ]; then
	read -p "WARNING: replace opition is set as true. Do you confirm you want to override the data from ${algorithms[@]}? [N/y]" -n 1 -r
	echo    # move to a new line
	if [[ ! $REPLY =~ ^[Yy]$ ]]
	then
	    [[ "$0" = "$BASH_SOURCE" ]] && exit 1 || return 1 # handle exits from shell or function but don't exit interactive shell
	fi
fi

method=remote # remote or local
jar=$(pwd)"/HHdEA/target/HHdEA-1.0-SNAPSHOT-jar-with-dependencies.jar"
main=br.ufpr.inf.cbio.hhdea.runner.MainRunner

ms=(5 10 15)
problems=(MaF01 MaF02 MaF03 MaF04 MaF05 MaF06 MaF07 MaF08 MaF09 MaF10 MaF11 MaF12 MaF13 MaF14 MaF15)
runs=20
set_seeds
execute ms problems algorithms "$methodology" "$runs" $replace $method $jar $main $experiment
echo "executing algorithms"
wait_queue $experiment

indicators=(HV)
run_indicators ms problems algorithms "$methodology" indicators $method $jar $runs TRUE $replace $experiment
 
indicators=(IGD)
run_indicators ms problems algorithms "$methodology" indicators $method $jar $runs FALSE $replace $experiment
 
echo "executing indicators"
wait_queue $experiment

echo "join results"

indicators=(HV IGD)
join ms problems algorithms "$methodology" "$runs" indicators

confidences=(0.95 0.99)
for confidence in "${confidences[@]}"; do
	experiment=$experiment$confidence
	echo "executing analysis"
	analysis ms problems algorithms "$experiment" "$methodology" indicators $confidence
done
