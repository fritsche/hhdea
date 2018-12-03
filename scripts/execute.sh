
function execute() {
  nms=$1[@]
  nproblems=$2[@]
  nalgorithms=$3[@]
  methodology=$4
  runs=$5
  replace=$6
  method=$7
  jar="$8"
  main=$9
  name=${10}
  dir=$(pwd)

  ms=("${!nms}")
  problems=("${!nproblems}")
  algorithms=("${!nalgorithms}")

  if [ "$method" = "remote" ]; then
    execute="sbatch -J $name.run $dir/scripts/addjob.sh" # for running on process queue
  else
    execute=eval # for running locally
  fi
  # execute=echo # for debugging

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
              javacommand="java -Duser.language=en -cp $jar -Xmx1g $main $dir/experiment $methodology $algorithm $problem $m $id $seed"
              cd /tmp
	      $execute "$javacommand > $output/$id.out" 1> /dev/null
              cd - > /dev/null
    	  fi
        done
        seed_index=$((seed_index+1))
      done
    done
  done

}

