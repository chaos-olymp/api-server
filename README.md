# API-Server

This is a BungeeCord which was especially developed for ChaosOlymp.DE to provide the website frontend with player and server information. It uses the Sun/Oracle http server library to start an HTTP server. The port is hardcoded to port `3010`, but I still recommend putting it behind a reverse proxy like [nginx](https://www.nginx.com/) or [haproxy](https://www.haproxy.com/) to ensure more security and professionalism.

## `/servers/`
Returns a json response with server information.

### Example
```json
[
  {
    "internal-name": "survival",
    "name": "survival",
    "motd": "§3Example MOTD",
    "online": true
  }
]
```

## `/players/`
Returns a json response with server information.

### Example
```json
[
  {
    "name": "DeepRobin",
    "uuid": "375e2a8d-ab90-4601-adb1-23acafbd0c55",
    "locale": "de-DE",
    "server": "survival",
    "ping": 12,
    "group": "developer"
  }
]
```