#！ /bin/bash
# 逻辑脚本，设置为0:00 定时执行，保证系统的运行
# 开始预测和更新数据库
sh test_batch.sh
# 预测完成后开始5分钟更新app数据库
sh update.sh