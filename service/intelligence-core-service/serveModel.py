import json

import tensorflow as tf


class ServeModel:
    def __init__(self):
        self.models = {
            0: tf.keras.models.load_model('model', compile=True)
        }
        self.categories_dict = self.load_categories_dict('model/category_dict.json')

    def load_categories_dict(self, path):
        with open(path) as f:
            return json.load(f)

    def get_category_by_id(self, category_id):
        return self.categories_dict[str(category_id)]

    def get_category_by_name(self, category_name):
        key_list = list(self.categories_dict.keys())
        val_list = list(self.categories_dict.values())
        return int(key_list[val_list.index(category_name)])

    def generate_sequences(self, model_id, initial_event_name, num_generate=10):
        model = self.models[model_id]
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
        model.reset_states()
        for i in range(num_generate):
            predictions = model(input_eval)
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
