
function run_indicators() {
  nms=$1[@]
  nproblems=$2[@]
  nalgorithms=$3[@]
  methodology=$4
  nindicator=$5[@]
  method=$6
  jar=$7
  runs=$8
  norm=$9
  replace=${10}
  name=${11}
  dir=$(pwd)

  ms=("${!nms}")
  problems=("${!nproblems}")
  algorithms=("${!nalgorithms}")
  indicator=("${!nindicator}")

  if [ "$method" = "remote" ]; then
	   execute="sbatch -J $name.ind $dir/scripts/addjob.sh" # for running on process queue
  else
    execute=eval # for running locally
  fi
  # execute=echo # for debugging

  seed_index=0

  for ind in "${indicator[@]}"; do
    for problem in "${problems[@]}"; do
      for m in "${ms[@]}"; do
        for (( id = 0; id < $runs; id++ )); do
          # each objective, problem and independent run (id) uses a different seed
          seed=${seeds[$seed_index]}
          # different algorithms on the same problem instance uses the same seed
          for alg in "${algorithms[@]}"; do
            base="$dir/experiment/$methodology/$m/data/$alg/$problem/FUN$id.tsv"
            file="$base.$ind"
            if [ ! -s $file ] || [ "$replace" = true ]; then
              rm -f $file
              javacommand="java -Duser.language=en -cp $jar -Xmx1g br.ufpr.inf.cbio.hhdea.runner.CommandLineIndicatorRunner $ind $dir/experiment/referenceFronts/"$problem"_"$m".ref $base $norm $seed > $file"
              cd /tmp
              $execute "$javacommand" 1> /dev/null
              cd - > /dev/null
            fi
          done
        done
      done
    done
  done
}

function join() {

  # get arguments
  nms=$1[@]
  nproblems=$2[@]
  nalgorithms=$3[@]
  output="experiment/$4"
  runs="$5"
  nindicator=$6[@]

  # parse arguments to array
  ms=("${!nms}")
  problems=("${!nproblems}")
  algorithms=("${!nalgorithms}")
  indicator=("${!nindicator}")

  error=0

  for ind in "${indicator[@]}"; do
    for m in "${ms[@]}"; do
      for problem in "${problems[@]}"; do
        for algname in "${algorithms[@]}"; do
          # remove old $ind file before computing
          rm -f $output/$m/data/$algname/$problem/$ind
          for (( id = 0; id < $runs; id++ )); do
            file=$output/$m/data/$algname/$problem/FUN$id.tsv.$ind
            if [ ! -s $file ]; then
              (>&2 echo "File '$file' does not exist or is empty")
              error=1
            else
              cat $file >> $output/$m/data/$algname/$problem/$ind
            fi
          done
        done
      done
    done
  done

  if [ $error -ne 0 ]; then
    (>&2 echo "Something went wrong")
    exit 1
  fi
}
