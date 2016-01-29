#This script allow to create all taskfile associeted to set of experiment
#should not be used "like that" as it is really setup dependend

for i in /gpfs/projects/bsc21/WORK-SIMON/hetca/DensityExpe/*/; do 
	filename=`basename "$i"`.task ; 
	printf "cd $i && java -Xmx1024M -Xms1024M -jar HETCASave2015_NORTHREAD.jar >> res\n%.0s" {1..160} > $filename  ; 
done 

