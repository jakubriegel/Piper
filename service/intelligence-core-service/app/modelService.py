from app.serveModel import ServeModel


class ModelService:
    def __init__(self):
        self.serveModel = ServeModel()

    def predict(self, model_id, event, limit):
        return self.serveModel.generate_sequences(model_id=model_id, initial_event_name=event, num_generate=limit)
