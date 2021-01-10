from pathlib import Path
import sys

from SampleClient import SampleClient

if __name__ == '__main__':
    client = SampleClient()
    how_much_sent = 0
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
                how_much_sent += 1
                if how_much_sent > 700:
                    client.request_routines()
                    how_much_sent = 0
            else:
                client.first_contact()
