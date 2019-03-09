import MySQLdb
import os
import csv
from functools import reduce
import Process
import argparse

def filename2location(filename,csv_name='result.csv'):

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
        print('Start connecting to the database')
        self.db = MySQLdb.connect("localhost", "admin", "1195593460",databast)
        self.db.set_character_set('utf8')
        self.dbc = self.db.cursor()
        self.dbc.execute('SET NAMES utf8;')
        self.dbc.execute('SET CHARACTER SET utf8;')
        self.dbc.execute('SET character_set_connection=utf8;')
        print('Database connection is successful, use database --wuhan')



    def _show_process(self,max_step):
        process = Process.ShowProcess(max_step)
        return process

    def _create_location_table(self,table_name):
        print('Start creating a table  '+table_name)
        sql = 'SHOW TABLES LIKE \'' + table_name + '\''
        self.dbc.execute(sql)
        result = self.dbc.fetchall()
        if len(result) == 0:
            print('Table does not exist, start create table ' + table_name)
            process = self._show_process(max_step=287)
            sql = 'create table ' + table_name + ' (id int) default charset=utf8;'
            self.dbc.execute(sql)

            for name in range(288):
                sql = 'alter table ' + table_name + ' add column col' + str(name) + ' int;'
                self.dbc.execute(sql)
                process.show_process(name)
            print('Form %s created ' % (table_name))
            process.close()
            return  True
        else:
            print('table '+table_name+' is existed ,pass')
            return  False


    def _create_map_table(self,table_name='map'):
        print('Start emptying the table  '+table_name)
        self.dbc.execute("DROP TABLE IF EXISTS " + table_name)
        sql='create table '+table_name+'(longitude varchar(20),latitude varchar(20),number int) default charset=utf8;'
        self.dbc.execute(sql)
        print('table '+table_name+" emptying completed")


    def _insert_location(self,table_name,index,data):

        sql='insert into '+table_name+' values( '+str(index)+' , '
        def add(s,i):
            return str(s)+' , '+str(i)
        sql=(sql+reduce(add,data))+');'
        self.dbc.execute(sql)
        self.db.commit()

    def _insert_map(self,longitude,latitude,number,start_flag):
        if start_flag==0:
            sql = 'insert into map values (%s,%s,%s)' % (longitude, latitude, number)
            self.dbc.execute(sql)
            self.db.commit()
            print('%s,%s,%s'%(longitude,latitude,number))
        else:
            self.dbc.execute('select number from map where longitude=%s and latitude=%s'%(longitude,latitude))
            origin_data=self.dbc.fetchall()[0]

            sql = 'delete from map where longitude= %s and latitude=%s' % (longitude, latitude)
            self.dbc.execute(sql)

            sql = 'insert into map values (%s,%s,%s)' % (longitude, latitude, number)
            self.dbc.execute(sql)
            self.db.commit()
            print('(%s,%s,%s)-->(%s,%s,%s)'%(longitude,latitude,origin_data,longitude,latitude,number))




    def csv2table(self,filename,csv_dir):
        if not os.path.exists(filename) or not os.path.exists(csv_dir) :
            print('Csv file does not exist')
        else:

            data={}
            with open(filename,'r') as file:
                reader=csv.reader(file)
                for line in reader:
                    id=line[0]
                    location=line[1]+'-'+line[2]
                    data[id]=location

            for name in data:
                if not os.path.exists(csv_dir+'/'+data[name]+'.csv'):
                    print (csv_dir+'/'+data[name]+'.csv not find ,pass')
                else:
                    table_name = name.replace('(', '').replace(')', '').replace('-', '').replace('/', '').replace('.','')
                    flag=self._create_location_table(table_name)
                    if flag:
                        print('Start writing data')
                        process = self._show_process(max_step=10000)
                        with open(csv_dir + '/' + data[name] + '.csv', 'r') as file:
                            reader = csv.reader(file)
                            for i, line in enumerate(reader):
                                process.show_process(i + 1)
                                if i + 1 > 10000:
                                    break
                                self._insert_location(table_name, i, line)
                            print('Data writing completed')
                            process.close()

    def get_location_all_data(self,filename):
        table_name=filename2location(filename)
        sql = 'SHOW TABLES LIKE \'' + table_name + '\''
        self.dbc.execute(sql)
        r = self.dbc.fetchall()
        if len(r)==0:
            return None
        sql='select * from '+table_name
        self.dbc.execute(sql)
        result=list(self.dbc.fetchall())
        result=[d[1:] for d in result]
        return result


    def get_location_last_data(self,filename,map_insert=False):
        table_name = filename2location(filename)

        sql = 'select * from '+table_name+' order by id desc limit 1;'
        self.dbc.execute(sql)
        result = list(self.dbc.fetchall())[0]
        if not map_insert:
            print('Input data:', result[1:])
        return result

    def save_predice_data(self,filename,index,data):
        table_name = filename2location(filename)
        self._insert_location(table_name=table_name,index=index,data=data)


    def update_map(self,index,start_flag):

        data = {}
        with open('result.csv', 'r') as file:
            reader = csv.reader(file)
            for line in reader:
                id = line[0]
                location = line[1] + '-' + line[2]
                data[id] = location
        for i,name in enumerate(data):
            if os.path.exists('weights/'+data[name]):
                longitude = data[name].split('-')[0]
                latitude = data[name].split('-')[1]
                number = self.get_location_last_data(data[name], map_insert=True)[1:][index]
                self._insert_map(longitude, latitude, number,start_flag=start_flag)
            else:
                print('weights/'+data[name]+' is not existed ,pass')
        print('Data update completed')



if __name__ == '__main__':

    parser=argparse.ArgumentParser()
    parser.add_argument('--csv2table',required=True,type=str)
    parser.add_argument('--index', required=False, default=0,type=int)
    parser.add_argument('--start_flag', required=False, default=1, type=int)
    args=parser.parse_args()
    start_flag=args.start_flag
    index=args.index
    csv2table=args.csv2table
    db = Database()
    if csv2table=='y':
        db.csv2table('result.csv', 'data/train_data')
    else:
        db.update_map(index,start_flag=start_flag)



