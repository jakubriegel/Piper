from pathlib import Path
import sys

from SampleClient import SampleClient

if __name__ == '__main__':
    client = SampleClient()
    if len(sys.argv) > 1 and sys.argv[1] == '--reset':
        print('reset')
        client.first_contact()
        exit()
    else:
        while True:
            if Path('EventGenerator/config/devIds_with_types.json').is_file() and Path(
                    'EventGenerator/config/typeIds_with_actions.json').is_file() and Path(
                    'EventGenerator/config/roomsIds_with_devices.json').is_file():
                client.send_data()
                client.request_routines()
            else:
                client.first_contact()
