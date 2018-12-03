#!/bin/bash

set -e

source ./scripts/seeds.sh
source ./scripts/wait_queue.sh

cd HHdEA
make
cd -

dir=$(pwd)
jar=$dir/HHdEA/target/HHdEA-1.0-SNAPSHOT-jar-with-dependencies.jar
main=br.ufpr.inf.cbio.hhdea.runner.CommandLineIndicatorRunner
ms=(5 10 15)
problems=(MaF01 MaF02 MaF03 MaF04 MaF05 MaF06 MaF07 MaF08 MaF09 MaF10 MaF11 MaF12 MaF13 MaF14 MaF15)
runs=20
replace=false
methodology=MaFMethodology
algorithms=(HHdEA2 HHdEA3)
indicator=(HV IGD)
experiment=hhdea3
method=remote

indicators=(HV)
run_indicators ms problems algorithms "$methodology" indicators $method $jar $runs TRUE $replace $experiment
 
indicators=(IGD)
run_indicators ms problems algorithms "$methodology" indicators $method $jar $runs FALSE $replace $experiment
 
echo "executing indicators"
wait_queue $experiment
