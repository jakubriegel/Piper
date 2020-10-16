import tensorflow as tf


class ServeModel:
    def __init__(self):
        self.model = tf.keras.models.load_model('model', compile=True)
        self.categories_dict = {0: 'bathroom_light_1_switch_light_off',
                                1: 'bathroom_light_1_switch_light_on',
                                2: 'bathroom_light_2_switch_light_off',
                                3: 'bathroom_light_2_switch_light_on',
                                4: 'bedroom_1_blind_1_switch_blind_down',
                                5: 'bedroom_1_blind_1_switch_blind_up',
                                6: 'bedroom_1_light_1_switch_light_off',
                                7: 'bedroom_1_light_1_switch_light_on',
                                8: 'bedroom_1_light_2_switch_light_off',
                                9: 'bedroom_1_light_2_switch_light_on',
                                10: 'bedroom_1_light_3_switch_light_off',
                                11: 'bedroom_1_light_3_switch_light_on',
                                12: 'bedroom_2_blind_1_switch_blind_down',
                                13: 'bedroom_2_blind_1_switch_blind_up',
                                14: 'bedroom_2_light_1_switch_light_off',
                                15: 'bedroom_2_light_1_switch_light_on',
                                16: 'bedroom_2_light_2_switch_light_off',
                                17: 'bedroom_2_light_2_switch_light_on',
                                18: 'bedroom_3_blind_1_switch_blind_down',
                                19: 'bedroom_3_blind_1_switch_blind_up',
                                20: 'bedroom_3_light_1_switch_light_off',
                                21: 'bedroom_3_light_1_switch_light_on',
                                22: 'bedroom_3_light_2_switch_light_off',
                                23: 'bedroom_3_light_2_switch_light_on',
                                24: 'corridor_light_1_sensor_light_off',
                                25: 'corridor_light_1_sensor_light_on',
                                26: 'general_ac_temp_up',
                                27: 'kitchen_blind_1_switch_blind_down',
                                28: 'kitchen_blind_1_switch_blind_up',
                                29: 'kitchen_blind_2_switch_blind_down',
                                30: 'kitchen_blind_2_switch_blind_up',
                                31: 'kitchen_light_1_switch_light_off',
                                32: 'kitchen_light_1_switch_light_on',
                                33: 'kitchen_light_2_switch_light_off',
                                34: 'kitchen_light_2_switch_light_on',
                                35: 'kitchen_light_3_switch_light_off',
                                36: 'kitchen_light_3_switch_light_on',
                                37: 'living_room_blind_1_switch_blind_down',
                                38: 'living_room_blind_1_switch_blind_up',
                                39: 'living_room_blind_2_switch_blind_down',
                                40: 'living_room_blind_2_switch_blind_up',
                                41: 'living_room_blind_3_switch_blind_down',
                                42: 'living_room_blind_3_switch_blind_up',
                                43: 'living_room_light_1_switch_light_off',
                                44: 'living_room_light_1_switch_light_on',
                                45: 'living_room_light_2_switch_light_off',
                                46: 'living_room_light_2_switch_light_on',
                                47: 'living_room_light_3_switch_light_off',
                                48: 'living_room_light_3_switch_light_on',
                                49: 'living_room_tv_off',
                                50: 'living_room_tv_on',
                                51: 'outdoor_gate_1_switch_gate_close',
                                52: 'outdoor_gate_1_switch_gate_open',
                                53: 'outdoor_gate_2_switch_gate_close',
                                54: 'outdoor_gate_2_switch_gate_open',
                                55: 'outdoor_light_1_sensor_light_off',
                                56: 'outdoor_light_1_sensor_light_on',
                                57: 'outdoor_light_2_sensor_light_off',
                                58: 'outdoor_light_2_sensor_light_on'}

    def getCategory(self, category_id):
        return self.categories_dict[category_id]

    def generate_sequences(self, start_sequence_event_id, num_generate=10):

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

            generated_sequences.append(self.getCategory(predicted_id))

        generated_sequences.insert(0, self.getCategory(start_sequence_event_id))
        return generated_sequences


if __name__ == '__main__':
    serveModel = ServeModel()
    print(serveModel.getCategory(1))
    print(serveModel.generate_sequences(start_sequence_event_id=1,num_generate=10))