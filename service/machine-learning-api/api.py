import json
import sys

from flask import Flask, request, Response

app = Flask(__name__)


@app.route('/get-predictions?modelId=123&start=name', methods=['GET'])
def get():
    try:
        return Response(response=json.dumps("test"), status=200, mimetype='application/json')
    except:
        Response(status=404)


if __name__ == '__main__':
    print(sys.path)
    app.run(
        host='127.0.0.1',
        port=9875
    )
