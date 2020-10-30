import json
from pathlib import Path

import tensorflow as tf
import pandas as pd


class ModelBuilder:
    def __init__(self):
        print("Initalize ModelBuilder \n")

    @staticmethod
    def build_model(csv_file):
        header_list = ["timestamp", "sensor", "action"]
        sensors_df = pd.read_csv('model/data.csv', names=header_list)

        sensors_df['sensors_with_action'] = sensors_df['sensor'] + '_' + sensors_df['action']

        sensors_df['sensors_with_action_code'] = pd.Categorical(sensors_df['sensors_with_action'])
        categories = pd.Categorical(sensors_df['sensors_with_action_code'])
        CATEGORIES_AMOUNT = len(categories.categories.values)
        print('There is', CATEGORIES_AMOUNT, 'unique categories')

        categories_dict = dict(enumerate(sensors_df['sensors_with_action_code'].cat.categories))

        category_dict_file = Path('model/category_dict.json')
        category_dict_file.write_text(json.dumps(categories_dict, indent=4) + '\n')


if __name__ == '__main__':
    mb = ModelBuilder()
    mb.build_model('test.csv')