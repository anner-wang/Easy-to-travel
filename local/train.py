import tensorflow as tf
import numpy as np
import csv
import argparse
import os
import sklearn.preprocessing as prep
from autoencoder import AddittiveGaussianNoiseAutoencoder

# 预处理数据
def standard_scale(X_train,X_test):
    preprocessor=prep.StandardScaler().fit(X_train)
    X_train=preprocessor.transform(X_train)
    X_test=preprocessor.transform(X_test)
    return X_train,X_test
# 不放回抽样
def get_random_block_from_data(data,batch_size):
    start_index=np.random.randint(0,len(data)-batch_size)
    return data[start_index:(start_index+batch_size)]

if __name__ == '__main__':
    # 加载训练地点
    parser=argparse.ArgumentParser()
    parser.add_argument('--location',required=True,help='longitude-latitude')
    parser.add_argument('--overwrite',required=False,default=False,help='True/False')
    args=parser.parse_args()
    filename=args.location
    overwrite=args.overwrite


    if not overwrite and os.path.exists('weights/'+filename):
        print('pass')
    else:
        # 加载自己的数据集到内存
        X_train = None
        with open('data/train_data/' + filename + '.csv', 'r') as file:
            reader = csv.reader(file)
            X_train = np.array(list(reader))
        # 初始化训练必须参数
        n_samples = int(X_train.shape[0])
        training_epoch = 50
        batch_size = 128
        display_step = 1
        save_weights_step = 5
        autoencoder = AddittiveGaussianNoiseAutoencoder(n_input=288, n_hidden=200, transfer_function=tf.nn.softplus,
                                                        optimizer=tf.train.AdamOptimizer(learning_rate=0.001)
                                                        , scale=0.01)
        saver = tf.train.Saver()

        # 开始训练
        for epoch in range(training_epoch):
            avg_cost = 0
            total_batch = int(n_samples / batch_size)
            for i in range(total_batch):
                batch_xs = get_random_block_from_data(X_train, batch_size)
                cost = autoencoder.partial_fit(batch_xs)
                avg_cost += cost / n_samples * batch_size
            if epoch % display_step == 0:
                print('Epoch:', '%04d' % (epoch + 1), 'cost=', '{:.9f}'.format(avg_cost))
            if epoch % save_weights_step == 0 and epoch > 0:
                autoencoder.save_weights(filename)

