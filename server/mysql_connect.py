import MySQLdb
if __name__ == '__main__':
    print('Start connecting to the database')
    conn1 = MySQLdb.connect(host='www.anner.wang', user='root', passwd='permit', db='wuhan', port=3306)
    conn2=MySQLdb.connect(host='127.0.0.1', user='admin', passwd='1195593460', db='wuhan', port=3306)

    sql = "select * from map;"
    cur1=conn1.cursor()
    cur2 = conn2.cursor()
    cur2.execute(sql)
    update_rows = cur2.fetchall()

    for row in update_rows:
        longitude=row[0]
        latitude=row[1]
        number=str(row[2])
        sql='delete from map where longitude ='+longitude+' and latitude='+latitude+';'
        cur1.execute(sql)
        sql='insert into map values ( '+longitude+','+latitude+','+number+');'
        cur1.execute(sql)
        conn1.commit()
        print(row[0],row[1],row[2])
    print('update database on www.anner.wang:3306/map done')
