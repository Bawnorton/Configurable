@file:Suppress("UnstableApiUsage")

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

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

repositories {
    mavenCentral()
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.bawnorton.com/releases/")
    maven("https://maven.terraformersmc.com/")
    maven("https://maven.isxander.dev/releases/")
    maven("https://maven.shedaniel.me")
    maven("https://jitpack.io")
    maven("https://maven.su5ed.dev/releases")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    modImplementation("dev.isxander:yet-another-config-lib:${property("yacl_version")}-$loader")
    implementation("org.quiltmc.parsers:gson:0.3.0")
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$awName"))

    runConfigs.all {
        ideConfigGenerated(false)
    }
}

tasks {
    withType<JavaCompile> {
        options.release = minecraftVersion.javaVersion()
    }

    withType<RemapJarTask> {
        dependsOn("shadowJar")
        inputFile.set(shadowJar.get().archiveFile.get())
        addNestedDependencies = true
    }

    withType<ShadowJar> {
        from(sourceSets.main.get().output)
        relocate("com.google.gson", "com.bawnorton.configurable.libs.gson")
        relocate("org.quiltmc.parsers", "com.bawnorton.configurable.libs.parsers")
        relocate("com.electronwill.nightconfig", "com.bawnorton.configurable.libs.nightconfig")

        dependencies {
            include(dependency("com.google.code.gson:gson:2.10.1"))
            include(dependency("com.electronwill.night-config:toml:3.8.0"))
            include(dependency("com.electronwill.night-config:core:3.8.0"))
            include(dependency("org.quiltmc.parsers:gson:0.3.0"))
            include(dependency("org.quiltmc.parsers:json:0.3.0"))
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
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion())
    targetCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion())
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("remapJar")
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

        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api")}+$minecraftVersion")
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

        modImplementation("org.sinytra.forgified-fabric-api:fabric-networking-api-v1:${property("ffapi_networking")}")
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

if(mod.mappings == "yarn") {
    publishMods {
        file = tasks.remapJar.get().archiveFile
        val tag = "$loader-${mod.version}+$minecraftVersion"
        val branch = "main"
        changelog = "[Changelog](https://github.com/Bawnorton/${mod.name}/blob/$branch/CHANGELOG.md)"
        displayName = "${mod.name} ${loader.toString().replaceFirstChar { it.uppercase() }} ${mod.version} for $minecraftVersion"
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
            minecraftVersions.addAll(mod.supportedVersions.split(", "))
        }

        curseforge {
            accessToken = providers.gradleProperty("CURSEFORGE_TOKEN")
            projectId = mod.curseforgeProjId
            minecraftVersions.addAll(mod.supportedVersions.split(", "))
        }
    }
}
