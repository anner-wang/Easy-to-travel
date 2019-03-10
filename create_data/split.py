import os
import csv
import random
import time
if not os.path.exists('data'):
    os.mkdir('data')
    os.mkdir('data/train_data')
    os.mkdir('data/test_data')
with open('result.csv','r') as file:
    reader=csv.reader(file)
    index=1	
    for line in reader:
        longitude=line[1]
        latitude=line[2]
        data=[random.randint(5,20) for r in range(288) ]
        if os.path.exists('data/train_data/'+longitude+'-'+latitude+'.csv'):
            print (index)
            index+=1
            time.sleep(0.1)
        else:
            with open('data/train_data/' + longitude + '-' + latitude + '.csv', 'w', newline='') as f:
                writer = csv.writer(f)

                new_data = [d + random.randint(1, 3) for d in data]
                with open('data/test_data/' + longitude + '-' + latitude + '.csv', 'a', newline='') as test_f:
                    test_writer = csv.writer(test_f)
                    test_writer.writerow(new_data)
                for i in range(10000):
                    new_data = [d + random.randint(1, 3) for d in data]
                    print(new_data)
                    writer.writerow(new_data)


