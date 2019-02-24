# 通过调用百度地图API逆向获取经纬度数据
import requests
import csv
import json
import argparse

def run(location):
    param={'query':location,'region':city,'city_limit':'true','output':'json','ak':ak}
    # 网络请求
    r=requests.get(url,params=param)
    # json字符串处理
    data=json.loads(r.text)['results']
    result=[{'name':d['name'],'longitude':d['location']['lng'],'latitude':d['location']['lat'],'uid':d['uid']} for d in data ]
    # 存储至磁盘
    with open(filename,'a',newline='') as f:
        writer=csv.writer(f)
        for d in result:
            temp=[d['name'],d['longitude'],d['latitude'],d['uid']]
            writer.writerow(temp)
            print(temp)

if __name__ == '__main__':

    parser=argparse.ArgumentParser()
    # 添加参数
    parser.add_argument('--ak',required=True,help=' 百度地图AK')
    parser.add_argument('--city',required=False,default='武汉',help='目标城市')
    args=parser.parse_args()
    # 基础的url和参数配置
    ak=args.ak
    city=args.city
    url='http://api.map.baidu.com/place/v2/search'
    location=['大学','医院','购物中心','政府','景点','步行街','书店','广场','学校','公园']
    # headers={'content-type':'application/json'}
    # proxies = { "http": "http://10.10.1.10:3128", "https": "http://10.10.1.10:1080", } 
    filename='result.csv'
    f=open(filename,'w',newline='')
    writer=csv.writer(f)
    writer.writerow(['name','longitude','latitude','uid'])
    f.close()
    for i in range(len(location)):
        try:
            run(location[i])
        except Exception:
            pass