## 使用百度地图API获取城市地点的经纬度

### 文件介绍

* location.py 生成result.csv文件，并初始化固定地点
* suggestion.py 根据已有的地点，迭代添加推荐打车地点
* result.csv 执行结果全部保存为csv文件

建议执行方式:

* `windows: run.bat`
* `linux: sh run.sh`

执行结果:

![](output.png)

## 对获取的经纬度信息分地点分类  

### 文件介绍

* process2finaldata.py  生成神经网络训练需要的数据  

即为每一个地点在一年中每5分钟的等待打车人数，每一个地点存在 105120 条数据，共有 3566 个 地点，共有3.7 亿条数据

  ##  Use the Baidu map API to get the latitude and longitude of a city location

### Document introduction

* Location.py : generates a result.csv file and initializes the fixed location
* Suggestion.py Iteratively add recommended taxi locations based on existing locations
* Result.csv execution results are all saved as csv files

Suggested implementation:

* windows: run.bat
* linux: sh run.sh

## Sorting the acquired latitude and longitude information by location

### Document introduction

* Process2finaldata.py :  generates data needed for neural network training 

That is, the number of taxis waiting for every 5 minutes in each location, there are 105,120 data in each location, there are 3,566 locations, and there are 370 million data.