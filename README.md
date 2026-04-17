# Mystic Brews

Simulate a potion brewery on your Minecraft server!

## Built against Paper

[Paper](https://papermc.io/)

## Building & Installation

### Requrements

- Paper 1.21.8
- Java 21

### Compiling

- Clone this repository
- Run `mvn clean package`

### Required Dependencies

- [Citizens2](https://ci.citizensnpcs.co/job/citizens2/)
- [Vault](https://www.spigotmc.org/resources/vault.34315/)
- [DecentHolograms](https://www.spigotmc.org/resources/decentholograms-1-8-1-21-11-papi-support-no-dependencies.96927/)


### Setup

- Drop the compiled MysticBrews.jar + dependencies in the plugins folder
- Restart the server
- Check the project root for a simple brewery schematic!

## In Game Setup

- Main command `/mysticbrews <start/stop/set/add>`
- Add chairs (look at a stair block) `/mysticbrews add chair`
- Add brewing stands (look at a brewing stand) `/mysticbrews add brewingstand`
- Set cauldron (look at a cauldron) `/mysticbrews set cauldron`
- Set Brewtender spawn point (stand at location) `/mysticbrews set brewcespawn`
- Set customer NPC spawn point (stand at location) `/mysticbrews set npcspawn`
- Open the brewery with `/mysticbrews open`