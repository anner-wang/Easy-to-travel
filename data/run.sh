#! /bin/bash
echo -n "ak="
read ak
file="result.csv"
if [ -x "$file" ];then
echo file not exist
python location.py --ak=$ak
fi
python suggestion.py --ak=$ak