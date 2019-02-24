@echo off
SET file=result.csv
if exist %file% (
    echo file alread existed,pass location.py!
) else (
    python location.py
)
python suggestion.py
echo 文件添加成功!
pause

