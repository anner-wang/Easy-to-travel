#！/bin/bash
for file in weights/*
do
    filename=${file##*weights/}
    echo 开始预测地点:$filename
    python predict.py --location $filename 
done
