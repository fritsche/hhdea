#!/bin/bash
## supress sbatch log
#SBATCH --output=/dev/null

## excluide machine from process queue (values: [hydra,gemini,loki,libra])
#SBATCH --exclude=gemini

eval $1
