# Configurable
### Decentrailised Config System

[![Modrinth](https://img.shields.io/modrinth/dt/configurable?color=00AF5C&label=downloads&logo=modrinth)](https://modrinth.com/mod/configurable)
[![CurseForge](https://cf.way2muchnoise.eu/full_0000000_downloads.svg)](https://curseforge.com/minecraft/mc-mods/configurable)

See the [wiki](https://github.com/Bawnorton/MixinSquared/wiki) for usage

## Gradle

```gradle
repositories {
    maven { url = "https://maven.bawnorton.com/releases" }
}
```

```gradle
dependencies {
    modImplementation(annotationProcessor("com.bawnorton.configurable:configurable-<loader>-<mappings>:<version>")))
}
```
Replace `<loader>` with your loader (fabric/neoforge)<br>
Replace `<mappings>` with your mappings (yarn/mojmap)<br>
Replace `<version>` with the latest version
