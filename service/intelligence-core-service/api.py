from flask import Flask, request, Response
from werkzeug.exceptions import HTTPException, abort
import json

from app.modelService import ModelService

app = Flask(__name__)

modelServiceInstance = ModelService()


@app.errorhandler(Exception)
def handle_error(e):
    code = 404
    if isinstance(e, HTTPException):
        code = e.code
        if e.code == 422:
            return Response(response=json.dumps('UNABLE_TO_PROCESS'), status=code, mimetype='application/json')
        elif e.code == 500:
            return Response(response=json.dumps('INTERNAL_SERVER_ERROR'), status=code, mimetype='application/json')
        elif e.code == 204:
            return Response(response=json.dumps('EMPTY'), status=code, mimetype='application/json')
        else:
            return Response(response=json.dumps('PAGE_NOT_FOUND'), status=code, mimetype='application/json')


@app.route('/', methods=['GET'])
def index():
    abort(500)


@app.route('/status', methods=['GET'])
def get_status():
    try:
        return Response(response=json.dumps('active'), status=200, mimetype='application/json')
    except:
        abort(500)


@app.route('/get-sequence', methods=['GET'])
def get_predictions():
    modelId = request.args.get('modelId')
    event = request.args.get('event')
    limit = int(request.args.get('limit'))

    try:
        prediction = modelServiceInstance.predict(modelId, event, limit)
    except ValueError as value_error:
        return Response(response=json.dumps({'message': str(value_error)}), status=400)

    response = {
        'modelId': modelId,
        'head': event,
        'sequence': prediction  # [f'{uuid4()}_{uuid4()}' for _ in range(limit)]
    }
    return Response(response=json.dumps(response), status=200, mimetype='application/json')


@app.route('/load-model', methods=['PUT'])
def load_model():
    modelId = request.args.get('modelId')

    try:
        modelServiceInstance.load_model(modelId)
    except ValueError as value_error:
        return Response(response=json.dumps({'message': str(value_error)}), status=404)

    return Response(response=json.dumps({'modelId': modelId}), status=200, mimetype='application/json')


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8004, debug=True)
