from serveModel import ServeModel


class Controller:
    def __init__(self):
        self.serveModel = ServeModel()

    def predict(self, start_sequence_event_id, num_generate):
        return self.serveModel.generate_sequences(start_sequence_event_id, num_generate)
