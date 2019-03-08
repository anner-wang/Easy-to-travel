#！ /bin/bash
for index in {0..287}
do
    python mysql_process.py --csv2table n --index $index 
    sleep 3000
done