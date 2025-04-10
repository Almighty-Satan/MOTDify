![CI](https://img.shields.io/github/actions/workflow/status/Almighty-Satan/MOTDify/build.yml?branch=master&style=flat-square)
![License](https://img.shields.io/github/license/Almighty-Satan/SLAMS?style=flat-square)

# MOTDify

MOTDify is a simple server that responds with an MOTD when pinged via the Minecraft server list.
Players can't join this server and are instantly disconnected with a configurable error message.

Supported Minecraft versions: 1.7.x+

![image0](./images/image0.png)
![image1](./images/image1.png)

## Configuration
MOTDify can be configured using the following environment variables

|Environment Variable|Description|Default|
|:-------:|:----------------:|:--------:|
|`motdify_port`|The port where this server is listening|`25565`|
|`motdify_protocol_version`|The Minecraft protocol version|Same as the client|
|`motdify_software`|The name of the server software/version|`MOTDify`|
|`motdify_players_online`|The number of player currently online|`0`|
|`motdify_players_max`|The maximum number of concurrent players|`69`|
|`motdify_favicon`|Path to a png image that is displayed as the servers favicon. The size of the image has to be exactly 64x64 pixels|N/A|
|`motdify_motd`|The MOTD|`§cServer not available!`|
|`motdify_disconnect`|A message that is displayed to the player when trying to join this server|`§cServer not available!`|

## Running
It is recommended to use the [Docker image](https://hub.docker.com/r/almightysatan/motdify) but you can also compile the project
using `./gradlew build` and run `java -jar MOTDify-1.x.x.jar`
