@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `maven-publish`
    java
    kotlin("jvm") version "1.9.22"
    id("dev.architectury.loom") version "1.7-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val mod = ModData(project)
val loader = LoaderData(project, loom.platform.get().name.lowercase())
val minecraftVersion = MinecraftVersionData(stonecutter)
val awName = "${mod.id}.accesswidener"

version = "${mod.version}-$loader-${mod.mappings}+$minecraftVersion"
group = mod.group
base.archivesName.set(mod.name)

stonecutter {
    debug = true
}

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.bawnorton.com/releases/")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.isxander.dev/releases/")
    maven("https://maven.shedaniel.me")
    maven("https://jitpack.io")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    modImplementation("dev.isxander:yet-another-config-lib:${property("yacl_version")}-$loader")
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$awName"))

    runConfigs.all {
        ideConfigGenerated(false)
    }
}

tasks {
    withType<JavaCompile> {
        options.release = 21
    }

    withType<ShadowJar> {
        from(sourceSets.main.get().output)
        relocate("com.google.gson", "com.bawnorton.configurable.libs.gson")
        relocate("com.electronwill.nightconfig", "com.bawnorton.configurable.libs.nightconfig")

        dependencies {
            include(dependency("com.google.code.gson:gson:2.10.1"))
            include(dependency("com.electronwill.night-config:toml:3.8.0"))
            include(dependency("com.electronwill.night-config:core:3.8.0"))
        }

        exclude(
            "assets/minecraft/**",
            "data/minecraft/**",
            "mappings/**",
            "**/.mcassetsroot",
            "*.jfc",
            "pack.png",
            "version.json"
        )

        mergeServiceFiles()

        archiveClassifier = null
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.shadowJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("remapJar", "shadowJar")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }
}

if(loader.isFabric) {
    dependencies {
        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
        modImplementation("net.fabricmc:fabric-loader:${loader.getVersion()}")
        modCompileOnly("com.terraformersmc:modmenu:${property("modmenu")}")
    }

    tasks {
        processResources {
            val modMetadata = mapOf(
                "mod_id" to mod.id,
                "mod_name" to mod.name,
                "description" to mod.description,
                "version" to mod.version,
                "minecraft_dependency" to mod.minecraftDependency
            )

            inputs.properties(modMetadata)
            filesMatching("fabric.mod.json") { expand(modMetadata) }
        }
    }
}

if (loader.isNeoForge) {
    dependencies {
        mappings(loom.layered {
            mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("yarn_build")}:v2")
            mappings("dev.architectury:yarn-mappings-patch-neoforge:1.21+build.4")
        })
        neoForge("net.neoforged:neoforge:${loader.getVersion()}")
    }

    tasks {
        processResources {
            val modMetadata = mapOf(
                "mod_id" to mod.id,
                "mod_name" to mod.name,
                "description" to mod.description,
                "version" to mod.version,
                "minecraft_dependency" to mod.minecraftDependency,
                "loader_version" to loader.getVersion()
            )

            inputs.properties(modMetadata)
            filesMatching("META-INF/neoforge.mods.toml") { expand(modMetadata) }
        }

        remapJar {
            atAccessWideners.add(awName)
        }
    }
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "bawnorton"
            url = uri("https://maven.bawnorton.com/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "${mod.group}.${mod.id}"
            artifactId = "${mod.id}-$loader-${mod.mappings}"
            version = "${mod.version}+$minecraftVersion"

            from(components["java"])
        }
    }
}

publishMods {
    file = tasks.shadowJar.get().archiveFile
    val tag = "$loader-${mod.mappings}-${mod.version}+$minecraftVersion"
    val branch = "main"
    changelog = "[Changelog](https://github.com/Bawnorton/${mod.name}/blob/$branch/CHANGELOG.md)"
    displayName = "${mod.name} ${loader.toString().replaceFirstChar { it.uppercase() }} (${mod.mappings}) ${mod.version} for $minecraftVersion"
    type = STABLE
    modLoaders.add(loader.toString())

    github {
        accessToken = providers.gradleProperty("GITHUB_TOKEN")
        repository = "Bawnorton/${mod.name}"
        commitish = branch
        tagName = tag
    }

    modrinth {
        accessToken = providers.gradleProperty("MODRINTH_TOKEN")
        projectId = mod.modrinthProjId
        minecraftVersions.addAll(mod.supportedVersions)
    }

    curseforge {
        accessToken = providers.gradleProperty("CURSEFORGE_TOKEN")
        projectId = mod.curseforgeProjId
        minecraftVersions.addAll(mod.supportedVersions)
    }
}
