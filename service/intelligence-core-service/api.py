from flask import Flask, request, Response
from werkzeug.exceptions import HTTPException, abort
import json
from uuid import uuid4
from time import sleep

# from app.modelService import ModelService

app = Flask(__name__)

# modelServiceInstance = ModelService()


@app.errorhandler(Exception)
def handle_error(e):
    code = 404
    if isinstance(e, HTTPException):
        code = e.code
        if e.code == 422:
            return Response(response=json.dumps('UNABLE_TO_PROCESS'), status=code, mimetype='application/json')
        elif e.code == 500:
            return Response(response=json.dumps('INTERNAL_SERVER_ERROR'), status=code, mimetype='application/json')
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

    try:
        modelId = int(request.args.get('modelId'))
        event = request.args.get('event')
        limit = int(request.args.get('limit'))
        prediction = modelServiceInstance.predict(modelId, event, limit)
        if len(prediction) <= 0:
            return Response(response=json.dumps('no content'), status=204, mimetype='application/json')
        return Response(response=json.dumps({"sequence": prediction}), status=200, mimetype='application/json')
    except:
        abort(422)



if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8004, debug=True)

