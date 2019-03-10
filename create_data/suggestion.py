
import csv
import requests
import json
import argparse
import time

def run(query,region):
    params={'query':query,'region':region,'city_limit':'true','output':'json','ak':ak}

    r=requests.get(url,params=params)
    time.sleep(5)

    data=json.loads(r.text)['result']
    result=[{'name':d['name'],'longitude':d['location']['lng'],'latitude':d['location']['lat'],'uid':d['uid']} for d in data ]

    with open(filename,'a',newline='') as f:
        writer=csv.writer(f)
        for d in result:
            temp=[d['name'],d['longitude'],d['latitude'],d['uid']]
            writer.writerow(temp)
            print(temp)
    
if __name__ == '__main__':
    parser=argparse.ArgumentParser()

    parser.add_argument('--ak',required=False,default='AXclZFCYBqfM8nBDloQ3uGQFr54MV9Q4',help=' 百度地图AK')
    parser.add_argument('--region',required=False,default='武汉',help='目标城市')
    args=parser.parse_args()

    ak=args.ak
    region=args.region
    url='http://api.map.baidu.com/place/v2/suggestion'
    filename='result.csv'
    temp=set()

    while True:
        f=open(filename,'r')
        reader=csv.reader(f)
        result=set([ d[0][-1] for d in reader ][1:])
        result=result-temp
        temp=temp|result
        print(result)
        if (len(result)==0):
            break
        f.close()
        for i in result:
            try:
                run(i,region)
            except Exception as e:
                print('reason',e)
    