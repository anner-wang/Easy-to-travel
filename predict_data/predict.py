import tensorflow as tf
import argparse
import csv
import numpy as np
from autoencoder import AddittiveGaussianNoiseAutoencoder

if __name__ == '__main__':
    # 加载预测地点
    parser = argparse.ArgumentParser()
    parser.add_argument('--location', required=True, help='longitude-latitude')
    args = parser.parse_args()
    filename = args.location

    if not os.path.exists('weights/'+filename):
        print('文件 weights/'+filename+"不存在")
    else:
        # # 加载计算图
        # saver=tf.train.import_meta_graph('weights/'+filename+'/anner.2019-3-5')
        # sess=tf.Session()
        # saver.restore(sess,tf.train.latest_checkpoint('weights/'+filename+'/'))
        # 加载同一地点上一时刻数据
        data = None
        with open('data/test_data/' + filename + '.csv', 'r') as f:
            data = list(csv.reader(f))
        data_numpy = np.array(data)

        # 初始化预测必须参数
        n_samples = 1
        training_epoch = 20
        batch_size = 128
        display_step = 1
        save_weights_step = 5
        autoencoder = AddittiveGaussianNoiseAutoencoder(n_input=288, n_hidden=200, transfer_function=tf.nn.softplus,
                                                        optimizer=tf.train.AdamOptimizer(learning_rate=0.001)
                                                        , scale=0.01)
        # 预测数据
        predict_data = autoencoder.load_weights_predict_X(data_numpy, filename).tolist()[0]
        predict_data = [int(d) for d in predict_data]
        print(predict_data)
        # 保存预测数据
        with open('data/test_data/' + filename + '.csv', 'a', newline='') as f:
            csv.writer(f).writerow(predict_data)
        print('数据预测输出并保存完成')