@file:Suppress("UnstableApiUsage")

import dev.kikugie.fletching_table.annotation.MixinEnvironment
import configurable.utils.*

plugins {
    kotlin("jvm")
    `maven-publish`
    id("configurable.common")
    id("fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.14"
}

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release/")
}

val minecraft: String by project
val loader: String by project
base.archivesName = "${mod("id")}-${mod("version")}+$minecraft-$loader"

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings(loom.layered {
        officialMojangMappings()
        deps("parchment") {
            parchment("org.parchmentmc.data:parchment-$it@zip")
        }
    })

    modImplementation("net.fabricmc:fabric-loader:0.16.14")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${deps("fabric_api")}")

    implementation("com.google.auto.service:auto-service-annotations:1.0")
    implementation("com.palantir.javapoet:javapoet:0.7.0")
    implementation("org.quiltmc.parsers:json:0.3.1")
    implementation("org.quiltmc.parsers:gson:0.3.1")
    implementation("com.electronwill.night-config:toml:3.8.2")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.google.testing.compile:compile-testing:0.21.0")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$minecraft.accesswidener"))

    runConfigs.all {
        ideConfigGenerated(false)
    }

    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        programArgs("--username=Bawnorton", "--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")
        appendProjectPathToConfigName = false
        name = "Fabric Client $minecraft"
    }

    afterEvaluate {
        runConfigs.configureEach {
            applyMixinDebugSettings(::vmArg, ::property)
        }
    }
}

fletchingTable {
    mixins.register("main") {
        mixin("default", "configurable.mixins.json")
        mixin("client", "configurable.client.mixins.json") {
            environment = MixinEnvironment.Env.CLIENT
        }
    }
}

tasks {
    register<Copy>("buildAndCollect") {
        group = "build"
        from(remapJar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${mod("version")}"))
        dependsOn("build")
    }

    processResources {
        exclude("META-INF/neoforge.mods.toml")
        exclude { it.name.endsWith("-accesstransformer.cfg") }
    }

    test {
        useJUnitPlatform()
        outputs.upToDateWhen { false }
        jvmArgs("--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
        jvmArgs("--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
        jvmArgs("--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
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
            groupId = "${mod("group")}.${mod("id")}"
            artifactId = "${mod("id")}-$loader"
            version = "${mod("version")}+$minecraft"

            from(components["java"])
        }
    }
}

publishMods {
    val mrToken = providers.gradleProperty("MODRINTH_TOKEN")
    val cfToken = providers.gradleProperty("CURSEFORGE_TOKEN")

    type = BETA
    file = tasks.remapJar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.remapSourcesJar.map { it.archiveFile.get() })

    displayName = "${mod("name")} Fabric ${mod("version")} for $minecraft"
    version = mod("version")
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add(loader)

    modrinth {
        projectId = property("publishing.modrinth") as String
        accessToken = mrToken
        minecraftVersions.add(minecraft)
        requires("fabric-api")
    }

    curseforge {
        projectId = property("publishing.curseforge") as String
        accessToken = cfToken
        minecraftVersions.add(minecraft)
        requires("fabric-api")
    }
}