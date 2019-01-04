
function analysis() {

  nms=$1[@]
  nproblems=$2[@]
  nalgorithms=$3[@]
  experiment=$4
  methodology=$5
  nindicator=$6[@]
  confidence=$7

  ms=("${!nms}")
  problems=("${!nproblems}")
  algorithms=("${!nalgorithms}")
  indicator=("${!nindicator}")

  if [ "${#algorithms[@]}" -lt "2" ] ; then
	(>&2 echo "ERROR: the number of algorithms should be higher than 1")
	exit 1
  fi

  for ind in "${indicator[@]}"; do

    echo $ind ${#algorithms[@]} ${algorithms[@]} ${#problems[@]} ${problems[@]} ${#ms[@]} ${ms[@]} experiment/$methodology $experiment $confidence

    java -Duser.language=en -jar experiment/lib/Statistics-1.0-SNAPSHOT-jar-with-dependencies.jar $ind ${#algorithms[@]} ${algorithms[@]} ${#problems[@]} ${problems[@]} ${#ms[@]} ${ms[@]} experiment/$methodology $experiment $confidence

    output=experiment/$methodology/R/$experiment/KruskalTest$ind.tex

    echo "%$output" > $output
    echo "\documentclass[]{article}" >> $output
    echo "\usepackage{colortbl}" >> $output
    echo "\usepackage[table*]{xcolor}" >> $output
    echo "\usepackage{multirow}" >> $output
    echo "\usepackage{fixltx2e}" >> $output
    echo "\usepackage{stfloats}" >> $output
    echo "\usepackage{psfrag}" >> $output
    echo "\usepackage[]{threeparttable}" >> $output
    echo "\usepackage{multicol}" >> $output
    echo "\usepackage{lscape}" >> $output
    echo "\xdefinecolor{gray95}{gray}{0.75}" >> $output
    echo "\begin{document}" >> $output

    if (( ${#algorithms[@]} > 4 )); then
      echo "\begin{landscape}" >> $output
    fi

    echo "\begin{table}" >> $output
    echo "\caption{$ind. Mean and standard deviation}" >> $output
    echo "\label{table:mean.$ind}" >> $output
    echo "\centering" >> $output
    echo "\begin{footnotesize}" >> $output
    aux=""
    head=""
    for (( i = 0; i < ${#algorithms[@]}; i++ )); do
      aux=$aux"l|"
      head=$head" & "${algorithms[i]}
    done
    echo "\begin{tabular}{|l|l|$aux}" >> $output
    echo "\hline" >> $output
    echo "Obj. & problem $head \\\\ \hline" >> $output
    for (( i = 0; i < ${#ms[@]}; i++ )); do
      cat "experiment/$methodology/R/$experiment/${ms[i]}/$ind.tex" >> $output
      echo "\hline" >> $output
    done
    echo "\end{tabular}" >> $output
    echo "\end{footnotesize}" >> $output
    echo "\end{table}" >> $output

    if (( ${#algorithms[@]} > 4 )); then
      echo "\end{landscape}" >> $output
    fi

    echo "\end{document}" >> $output

  done

}
