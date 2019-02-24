# 根据经纬度获取乘车地点
import csv
import requests
import json
import argparse
import time

def run(query,region):
    params={'query':query,'region':region,'city_limit':'true','output':'json','ak':ak}
    # 网络请求
    r=requests.get(url,params=params)
    time.sleep(5)
    # 解析json字符串
    data=json.loads(r.text)['result']
    result=[{'name':d['name'],'longitude':d['location']['lng'],'latitude':d['location']['lat'],'uid':d['uid']} for d in data ]
    # 存储到磁盘
    with open(filename,'a',newline='') as f:
        writer=csv.writer(f)
        for d in result:
            temp=[d['name'],d['longitude'],d['latitude'],d['uid']]
            writer.writerow(temp)
            print(temp)
    
if __name__ == '__main__':
    parser=argparse.ArgumentParser()
    # 添加参数
    parser.add_argument('--ak',required=False,default='AXclZFCYBqfM8nBDloQ3uGQFr54MV9Q4',help=' 百度地图AK')
    parser.add_argument('--region',required=False,default='武汉',help='目标城市')
    args=parser.parse_args()
    # 基础的url和参数配置
    ak=args.ak
    region=args.region
    url='http://api.map.baidu.com/place/v2/suggestion'
    filename='result.csv'
    f=open(filename,'r')
    reader=csv.reader(f)
    result=set([ d[0][:2] for d in reader ][1:])
    print(result)
    f.close()
    for i in result:
        try:
            run(i,region)
        except Exception:
            print(Exception)
    