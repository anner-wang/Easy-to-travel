import os
import csv
import random
if not os.path.exists('data'):
    os.mkdir('data')
with open('result.csv','r') as file:
    reader=csv.reader(file)
    start_flag=True
    for line in reader:
        if start_flag:
            start_flag=False
            continue
        longitude=line[1]
        latitude=line[2]
        data=[random.randint(5,20) for r in range(288) ]
        with open('data/'+longitude+'-'+latitude+'.csv','w',newline='') as f:
            writer=csv.writer(f)
            for i in range(100000):
                new_data=[d+random.randint(1,3) for d in data]
                print(new_data)
                writer.writerow(new_data)

