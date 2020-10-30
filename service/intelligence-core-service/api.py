from flask import Flask, request, Response
import json

from modelService import ModelService

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
    try:
        modelId = int(request.args.get('modelId'))
        event = request.args.get('event')
        limit = int(request.args.get('limit'))
        prediction = modelServiceInstance.predict(modelId, event, limit)
        return Response(response=json.dumps({"sequence": prediction}), status=200, mimetype='application/json')
    except:
        return Response(status=422)


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000, debug=True)
