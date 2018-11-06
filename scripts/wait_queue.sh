function wait_queue() {
	name=$1
	# Waiting queue
	date
	total=$( squeue -u gian | grep $name | wc -l )
	while [ ! $(squeue -u gian | grep $name | wc -l) -eq 0 ]; do
		x=$(squeue -u gian | grep $name | wc -l)
		value=$( echo  $x / $total | bc -l )
		x=$( echo  $total - $x | bc -l )
		value=$( echo "$value * 100" | bc)
		value=$( echo "100 - $value" | bc)
		value=${value%.*}
		echo -ne "Waiting queue: "$value"% ["$x"/"$total"]"\\r
		sleep 1
	done

	# wait for files being copied
	sleep 2

}
