import MySQLdb
import os
import csv
from functools import reduce
import Process
import argparse

def filename2location(filename,csv_name='result.csv'):
    # 读取文件内容到内存
    data = {}
    with open(csv_name, 'r') as file:
        reader = csv.reader(file)
        for line in reader:
            id = line[0]
            location = line[1] + '-' + line[2]
            data[location] = id
    ret=data[filename].replace('(','').replace(')','').replace('-','').replace('/','')
    return ret

class Database(object):
    def __init__(self,databast='wuhan'):
        print('开始连接数据库')
        self.db = MySQLdb.connect("localhost", "root", "anner",databast)
        self.db.set_character_set('utf8')
        self.dbc = self.db.cursor()
        self.dbc.execute('SET NAMES utf8;')
        self.dbc.execute('SET CHARACTER SET utf8;')
        self.dbc.execute('SET character_set_connection=utf8;')
        print('数据库连接成功，使用数据库--wuhan')



    def _show_process(self,max_step):
        process = Process.ShowProcess(max_step)
        return process
    # 创建地点的表
    def _create_location_table(self,table_name):
        print('开始创建表 '+table_name)
        process = self._show_process(max_step=287)
        self.dbc.execute("DROP TABLE IF EXISTS " + table_name)
        sql='create table '+table_name+' (id int) default charset=utf8;'
        self.dbc.execute(sql)
        # 扩展列
        for name in range(288):
            sql = 'alter table ' + table_name + ' add column col' + str(name) + ' int;'
            self.dbc.execute(sql)
            process.show_process(name)
        print('表格 %s 创建完成'%(table_name))
        process.close()

    # 创建app交互的表
    def _create_map_table(self,table_name='map'):
        print('开始清空表 '+table_name)
        self.dbc.execute("DROP TABLE IF EXISTS " + table_name)
        sql='create table '+table_name+'(longitude varchar(20),latitude varchar(20),number int) default charset=utf8;'
        self.dbc.execute(sql)
        print('表'+table_name+"清空完成")

    # 插入288数据
    def _insert_location(self,table_name,index,data):
        # 形成sql 语句
        sql='insert into '+table_name+' values( '+str(index)+' , '
        def add(s,i):
            return str(s)+' , '+str(i)
        sql=(sql+reduce(add,data))+');'
        self.dbc.execute(sql)
        self.db.commit()
    # 插入map数据
    def _insert_map(self,longitude,latitude,number,start_flag=False):
        if start_flag:
            # 插入数据
            sql = 'insert into map values (%s,%s,%s)' % (longitude, latitude, number)
            self.dbc.execute(sql)
            self.db.commit()
        else:
            self.dbc.execute('select number from map where longitude=%s and latitude=%s'%(longitude,latitude))
            origin_data=self.dbc.fetchall()[0][0]
            # 如果数据存在，先删去数据
            sql = 'delete from map where longitude= %s and latitude=%s' % (longitude, latitude)
            self.dbc.execute(sql)
            # 插入数据
            sql = 'insert into map values (%s,%s,%s)' % (longitude, latitude, number)
            self.dbc.execute(sql)
            self.db.commit()
            print('(%s,%s,%s)-->(%s,%s,%s)'%(longitude,latitude,origin_data,longitude,latitude,number))



    # 批量化将csv文件转换为table
    def csv2table(self,filename,csv_dir):
        if not os.path.exists(filename) or not os.path.exists(csv_dir):
            print('csv文件不存在')
        else:
            # 读取文件内容到内存
            data={}
            with open(filename,'r') as file:
                reader=csv.reader(file)
                for line in reader:
                    id=line[0]
                    location=line[1]+'-'+line[2]
                    data[id]=location
            # 开始创建表格
            for name in data:
                table_name=name.replace('(','').replace(')','').replace('-','').replace('/','')
                self._create_location_table(table_name)
                print('开始写入数据')
                process = self._show_process(max_step=10000)
                with open(csv_dir+'/'+data[name]+'.csv','r') as file:
                    reader=csv.reader(file)
                    for i,line in enumerate(reader):
                        process.show_process(i + 1)
                        if i + 1 > 10000:
                            break
                        self._insert_location(table_name,i,line)
                    print('数据写入完成')
                    process.close()
    # 读取一个地点全部数据，返回列表
    def get_location_all_data(self,filename):
        table_name=filename2location(filename)
        # 查询
        sql='select * from '+table_name
        self.dbc.execute(sql)
        result=list(self.dbc.fetchall())
        result=[d[1:] for d in result]
        return result

    # 返回表最后一行数据做预测
    def get_location_last_data(self,filename,map_insert=False):
        table_name = filename2location(filename)
        # 查询
        sql = 'select * from '+table_name+' order by id desc limit 1;'
        self.dbc.execute(sql)
        result = list(self.dbc.fetchall())[0]
        if not map_insert:
            print('输入数据: ', result[1:])
        return result
    # 保存预测数据
    def save_predice_data(self,filename,index,data):
        table_name = filename2location(filename)
        # 保存
        self._insert_location(table_name=table_name,index=index,data=data)


    # 定期更新map表接口
    def update_map(self,index):
        # 读取文件内容到内存
        data = {}
        with open('result.csv', 'r') as file:
            reader = csv.reader(file)
            for line in reader:
                id = line[0]
                location = line[1] + '-' + line[2]
                data[id] = location
        # 读取所有的数据库数据的最后一行
        for i,name in enumerate(data):
            longitude=data[name].split('-')[0]
            latitude=data[name].split('-')[1]
            number=self.get_location_last_data(data[name],map_insert=True)[1:][index]
            self._insert_map(longitude,latitude,number)
        print('数据更新完成')



if __name__ == '__main__':

    parser=argparse.ArgumentParser()
    parser.add_argument('--csv2table',required=True,type=str)
    parser.add_argument('--index', required=False, default=0,type=int)
    args=parser.parse_args()
    index=args.index
    csv2table=args.csv2table
    print(csv2table)
    db = Database()
    if csv2table=='y':
        db.csv2table('result.csv', 'data/train_data')
    else:
        db.update_map(index)


    #
    #print(filename2location('114.3094-30.603889'))
