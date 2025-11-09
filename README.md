<div align="center">
  <h1>IceRunner — Minecraft Minigame</h1>

![Made with Kotlin](https://img.shields.io/badge/Made%20with-Kotlin-purple)
![Built with Gradle](https://img.shields.io/badge/Built_with-Gradle-cyan)
![Minecraft versions](https://img.shields.io/badge/Minecraft%20versions-1.21.8-green)

</div>

## Introduction

IceRunner is a game mode inspired by KOTH (King of the Hill), in which two teams of seven players battle for control of the central island. When a team dominates an island, it accumulates points over time. Once a team reaches a set number of points, that team wins the game.

## Installation

### Requirements : 

- Java 21+
- Gradle 8+

### Build from Source code

This minecraft plugin has to be built from the source code as of now, you can build the plugin JAR file by following these commands : 

```sh
git clone https://github.com/Altaks/IceRunner # or `git clone git@github.com:Altaks/IceRunner.git` to clone through SSH
cd ./IceRunner
./gradlew shadowJar
```

Once you've executed these commands, you should find the plugin JAR in the `/artifacts` folder.

If you want to change the output directory of the `./gradlew shadowJar` command, you can set the `PLUGINS_DIRECTORY` environment variable in a `.env` file at the root of the repository.

## License

This project is licensed under the [MIT License (MIT)](./LICENSE)

## Contribution

This project is open to contributions for external developers/maintainers

![Alt](https://repobeats.axiom.co/api/embed/519dc8bf1ced48fd8b77be5ad321a8c811b75ccd.svg "Repobeats analytics image")