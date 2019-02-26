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
        data=list(reader)
        for t in range(288):
             # 创建文件
            file=open('data/%d.csv'%t,'w',newline='')
            writer=csv.writer(file)
            for line in data:
                if flag:
                    new_line=line+['time','number']
                    flag=False
                else:
                    new_line=line+[t*60*5,random.randint(0,100)]
                writer.writerow(new_line)
                print(new_line)
            flag=True
            file.close()
            print('文件%d.csv创建结束'%t)