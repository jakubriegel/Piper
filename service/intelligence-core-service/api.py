from flask import Flask, request, Response
import json

from controller import Controller

app = Flask(__name__)

controller = Controller()


@app.route('/status', methods=['GET'])
def getStatus():
    try:
        return Response(response=json.dumps('active'), status=200, mimetype='application/json')
    except:
        return Response(status=404)


@app.route('/get-sequence', methods=['GET'])
def getPredictions():
    try:
        modelId = int(request.args.get('modelId'))
        event = request.args.get('event')
        limit = int(request.args.get('limit'))
        prediction = controller.predict(modelId, event, limit)
        return Response(response=json.dumps({"sequence": prediction}), status=200, mimetype='application/json')
    except:
        return Response(status=422)


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)
