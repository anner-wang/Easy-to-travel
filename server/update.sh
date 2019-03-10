#！ /bin/bash
for index in {0..287}
do
    python mysql_process.py --csv2table n --index $index 
    python mysql_connect.py
    sleep 3000
done
