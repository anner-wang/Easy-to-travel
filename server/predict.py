import tensorflow as tf
import argparse
import csv
import numpy as np
import os
from autoencoder import AddittiveGaussianNoiseAutoencoder
from mysql_process import Database

if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('--location', required=True, help='longitude-latitude')
    args = parser.parse_args()
    filename = args.location
    if not os.path.exists('weights/'+filename):
        print('file weights/'+filename+"is not existed")
    else:
        db=Database()
        temp_data=db.get_location_last_data(filename)
        index,data=temp_data[0],temp_data[1:]
        data_numpy = np.array(data).reshape((1,288))

        n_samples = 1
        training_epoch = 20
        batch_size = 128
        display_step = 1
        save_weights_step = 5
        autoencoder = AddittiveGaussianNoiseAutoencoder(n_input=288, n_hidden=200, transfer_function=tf.nn.softplus,
                                                        optimizer=tf.train.AdamOptimizer(learning_rate=0.001)
                                                        , scale=0.01)

        predict_data = autoencoder.load_weights_predict_X(data_numpy, filename).tolist()[0]
        predict_data = [int(d) for d in predict_data]
        print('predict data:',predict_data)
        db.save_predice_data(filename,index+1,predict_data)
        print('data prediction output and save completed')