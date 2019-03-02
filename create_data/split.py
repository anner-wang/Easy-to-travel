import csv
import random
import os
import time

if __name__ == '__main__':
    # 按照秒存储，5分钟存储一次
    # 读取文件
    folder=os.path.exists('data')
    if not folder:
        os.makedirs('data')
        print('创建文件夹成功')
    flag=True
    filename='result.csv'
    with open(filename,'r') as f:
        reader=csv.reader(f)
        # 主循环
        for place in reader:
            print(place)
            if place[3]=='uid':
                continue
            # 创建地点的第一天csv文件
            index=1
            lines=[]
            filename='data/'+place[1]+'-'+place[2]
            # 创建文件夹
            if not os.path.exists(filename):
                os.makedirs(filename)
                name=filename+'/'+str(index)+'.csv'
                file=open(name,'w',newline='')
                writer=csv.writer(file)
                for i in range(288):
                    line=[i,random.randint(1,100)]
                    lines.append(line)
                    writer.writerow(line)
                file.close()
            # 按照第一个文件的值，存储后面的csv文件
            index+=1
            for i in range(364):
                file=open(filename+'/'+str(index)+'.csv','w',newline='')
                index+=1
                writer=csv.writer(file)
                for line in lines:
                    writer.writerow([line[0],line[1]+random.randint(1,10)])
                file.close()


        
