import sys
import json
from datetime import datetime
sys.path.append('channels')
from channels import send_sms, send_voice, send_email

def main():
    channel = sys.argv[1]
    to = sys.argv[2]
    body = sys.argv[3]
    attempt = int(sys.argv[4])

    if channel == 'sms':
        res = send_sms(to, body, datetime.now(), attempt)
    elif channel.startswith('voice'):
        res = send_voice(to, body, datetime.now(), attempt)
    else:
        res = send_email(to, body, datetime.now(), attempt)

    print(json.dumps(res))

if __name__ == '__main__':
    main()