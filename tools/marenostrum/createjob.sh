#bin/bash
#File used with the createTask on. This one create the job files

 for i in /gpfs/projects/bsc21/WORK-SIMON/hetca/DensityExpe/*/; do 
     filename=`basename "$i"` 
     cp hetcaGene "$filename".job
     sed -i "s/hetca/$filename/g" "$filename".job 
 done
