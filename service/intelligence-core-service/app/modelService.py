from app.serveModel import ServeModel


class ModelService:
    def __init__(self):
        self.serveModel = ServeModel()

    def predict(self, modelId, event, limit):
        return self.serveModel.generate_sequences(model_id=modelId, initial_event_name=event, num_generate=limit)
