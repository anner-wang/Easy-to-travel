#！/bin/bash
for file in data/train_data/*
do
    if test -f $file
    then
        filename=${file##*/}
        location=${filename%.*}
        echo 开始预测地点:$location
        python predict.py --location $location 
    fi
done