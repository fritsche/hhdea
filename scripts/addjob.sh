#!/bin/bash
#SBATCH --output=/mnt/NAS/gian/logs/slurm-%j.out

## Excluir máquina [hydra,gemini,loki,libra]
#SBATCH --exclude=

echo "$1"
eval $1
rm -f *.log*

