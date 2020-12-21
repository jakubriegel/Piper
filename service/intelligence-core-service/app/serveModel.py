from typing import List
from app.logger import log
import tensorflow as tf
import json
import os

MODELS_DIR = '/models'
# change it to '/models' on Docker and 'models' for local development (make copy model to models folder and
# change it's name f.eg. to 123_model where 123 is modelId)


class ServeModel:
    def __init__(self):
        self.loaded_models = {}
        self.categories_dicts = {}
        self.model = None

        self.__scan_models_directory(MODELS_DIR)

    def __scan_models_directory(self, path: str) -> None:
        models_list = os.scandir(path=path)
        for model in models_list:
            if model.is_dir():
                self.load_model(model_id=model.name, postfix=False)

    def load_model(self, model_id: str, postfix=True) -> None:
        log(f'Loading model with id: {model_id}')
        if model_id + '_model' in self.loaded_models:
            log(f'Model {model_id} already loaded.')
            return None

        filesystem_models_list = [dir_obj.name for dir_obj in os.scandir(MODELS_DIR)]
        if not (model_id + '_model' in filesystem_models_list or model_id in filesystem_models_list):
            raise ValueError(
                "Model with given id doesn't exist!"
            )
        elif postfix:
            self.categories_dicts[model_id] = load_dict(f'{MODELS_DIR}/{model_id}_model/category_dict.json')
            self.loaded_models[model_id] = tf.keras.models.load_model(f'{MODELS_DIR}/{model_id}_model', compile=True)
        else:
            self.categories_dicts[model_id] = load_dict(f'{MODELS_DIR}/{model_id}/category_dict.json')
            self.loaded_models[model_id] = tf.keras.models.load_model(f'{MODELS_DIR}/{model_id}', compile=True)

        log(f'Model with id: {model_id} loaded into memory.')

    def get_category_by_id(self, category_id: int):
        return self.categories_dicts[self.model_id][str(category_id)]

    def get_category_by_name(self, category_name: str) -> int:
        key_list = list(self.categories_dicts[self.model_id].keys())
        val_list = list(self.categories_dicts[self.model_id].values())
        return int(key_list[val_list.index(category_name)])

    def generate_sequences(self, model_id: str, initial_event_name: str, num_generate: int = 10) -> List[str]:
        self.model_id = model_id + '_model'
        self.load_model(model_id)
        tmp_model_ref = self.loaded_models[self.model_id]
        print(f'Making prediction using model: {model_id}, start event: {initial_event_name} and n={num_generate}')

        start_sequence_event_id = self.get_category_by_name(initial_event_name)

        # Converting our start frame to vector of numbers (vectorization)
        input_eval = [start_sequence_event_id]
        input_eval = tf.expand_dims(input_eval, 0)

        # Empty string to store our results
        generated_sequences = []

        # Low temperatures results in more predictable events.
        # Higher temperatures results in more surprising events.
        # Experiment to find the best setting.
        temperature = 1.0

        # Here batch size == 1
        tmp_model_ref.reset_states()
        for i in range(num_generate):
            predictions = tmp_model_ref(input_eval)
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
