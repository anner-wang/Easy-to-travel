#! /bin/bash
for file in data/train_data/*
do
	if test -f $file
	then
        	filename=${file##*/}
        	location=${filename%.*}
        	echo 开始训练地点:$location
        	#python train.py --location $location 
    	fi
done
