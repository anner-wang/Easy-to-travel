@echo off 
SET file=result.csv
if exist %file% (
    echo file alread existed,pass location.py!
) else (
    python location.py --ak=%0
)
python suggestion.py --ak=%0
echo 文件添加成功!
pause

