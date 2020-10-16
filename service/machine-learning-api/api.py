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


@app.route('/get-predictions', methods=['GET'])
def getPredictions():
    try:
        start_sequence_event_id = request.args.get('start_sequence_event_id')
        num_generate = request.args.get('num_generate')
        prediction = controller.predict(start_sequence_event_id, num_generate)
        print(prediction)
        return Response(response=json.dumps(prediction), status=200, mimetype='application/json')
    except:
        return Response(status=422)


if __name__ == '__main__':
    app.run(host='127.0.0.1', port=9875)
