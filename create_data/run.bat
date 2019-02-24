@echo off 
SET /p ak=ak=
SET file=result.csv
if exist %file% (
    echo file alread existed,pass location.py!
) else (
    python location.py --ak=%ak%
)
python suggestion.py --ak=%ak%
echo 文件添加成功!
pause

