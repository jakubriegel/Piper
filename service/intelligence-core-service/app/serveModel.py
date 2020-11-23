import json
import os
from typing import List
import tensorflow as tf

MODELS_DIR = '/models'
# change it to /models on Docker and models locally (make copy model to models folder and
# change it's name f.eg. to 123_model where 123 is modelId)


class ServeModel:
    def __init__(self):
        self.modelId = ''
        self.models = []
        self.categories_dict = {}
        self.model = None
        self.__scan_models_directory(MODELS_DIR)

    def __scan_models_directory(self, path: str):
        models_list = os.scandir(path=path)
        tmp_models_list = []

        print('Available models:')

        for model in models_list:
            if model.is_dir():
                tmp_models_list.append(model.name)
                print(model.name)

        self.models = tmp_models_list

    def load_model(self, model_id: str) -> None:
        self.modelId = model_id
        self.categories_dict = load_dict(f'{MODELS_DIR}/{model_id}_model/category_dict.json')
        self.model = tf.keras.models.load_model(f'{MODELS_DIR}/{model_id}_model', compile=True)

    def get_category_by_id(self, category_id: int):
        return self.categories_dict[str(category_id)]

    def get_category_by_name(self, category_name: str) -> int:
        key_list = list(self.categories_dict.keys())
        val_list = list(self.categories_dict.values())
        return int(key_list[val_list.index(category_name)])

    def generate_sequences(self, model_id: str, initial_event_name: str, num_generate: int = 10) -> List[str]:
        self.load_model(model_id)
        print(f'Making prediction using model: {model_id}, start event: {initial_event_name} and n={num_generate}')

        start_sequence_event_id = self.get_category_by_name(initial_event_name)

        # Converting our start frame to vector of numbers (vectorizing)
        input_eval = [start_sequence_event_id]
        input_eval = tf.expand_dims(input_eval, 0)

        # Empty string to store our results
        generated_sequences = []

        # Low temperatures results in more predictable events.
        # Higher temperatures results in more surprising events.
        # Experiment to find the best setting.
        temperature = 1.0

        # Here batch size == 1
        self.model.reset_states()
        for i in range(num_generate):
            predictions = self.model(input_eval)
            # remove the batch dimension
            predictions = tf.squeeze(predictions, 0)

            # using a categorical distribution to predict the character returned by the model
            predictions = predictions / temperature
            predicted_id = tf.random.categorical(predictions, num_samples=1)[-1, 0].numpy()

            # We pass the predicted character as the next input to the model
            # along with the previous hidden state
            input_eval = tf.expand_dims([predicted_id], 0)

            generated_sequences.append(self.get_category_by_id(predicted_id))

        generated_sequences.insert(0, self.get_category_by_id(start_sequence_event_id))
        return generated_sequences


def load_dict(path: str):
    with open(path) as f:
        return json.load(f)


# if __name__ == '__main__':
#     serve_model = ServeModel()
#     print(serve_model.generate_sequences('31231231231-3123-1231323-1321231','bathroom_light_1_switch_light_off', 5))
