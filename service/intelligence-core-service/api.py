from flask import Flask, request, Response
import json
# from uuid import uuid4
# from time import sleep

from app.modelService import ModelService

app = Flask(__name__)

modelServiceInstance = ModelService()


@app.route('/status', methods=['GET'])
def get_status():
    try:
        return Response(response=json.dumps('active'), status=200, mimetype='application/json')
    except:
        return Response(status=500)


@app.route('/get-sequence', methods=['GET'])
def get_predictions():
    modelId = request.args.get('modelId')
    event = request.args.get('event')
    limit = int(request.args.get('limit'))

    prediction = modelServiceInstance.predict(modelId, event, limit)

    response = {
        'modelId': modelId,
        'head': event,
        'sequence': prediction  # [f'{uuid4()}_{uuid4()}' for _ in range(limit)]
    }
    return Response(response=json.dumps(response), status=200, mimetype='application/json')


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8004, debug=True)
