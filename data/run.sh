#! /bin/bash
file="result.csv"
if [ -x "$file" ];then
echo file not exist
python location.py
fi
python suggestion.py