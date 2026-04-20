@file:Suppress("UnstableApiUsage")

import dev.kikugie.fletching_table.annotation.MixinEnvironment
import configurable.utils.*

plugins {
    kotlin("jvm")
    `maven-publish`
    id("configurable.common")
    id("net.fabricmc.fabric-loom")
    id("me.modmuss50.mod-publish-plugin")
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.14"
}

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://maven.parchmentmc.org")
}

val minecraft: String by project
val loader: String by project

sc.properties.tags(minecraft, loader)

base.archivesName = "${mod<String>("id")}-${mod<String>("version")}+$minecraft-$loader"

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")

    implementation("net.fabricmc:fabric-loader:0.19.2")
    implementation("net.fabricmc.fabric-api:fabric-api:${deps<String>("fabric_api")}")

    include(api(annotationProcessor("com.google.auto.service:auto-service:1.0")!!)!!)
    include(implementation("org.quiltmc.parsers:json:0.3.1")!!)
    include(implementation("org.quiltmc.parsers:gson:0.3.1")!!)
    include(implementation("com.electronwill.night-config:toml:3.8.3")!!)
    include(implementation("com.electronwill.night-config:core:3.8.3")!!)

    implementation("com.palantir.javapoet:javapoet:0.7.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.google.testing.compile:compile-testing:0.21.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

loom {
    accessWidenerPath.set(rootProject.file("src/main/resources/$minecraft.accesswidener"))

    fabricApi {
        configureDataGeneration {
            createRunConfiguration = true
            client = true
            modId = mod<String>("id")
        }
    }

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

    runConfigs["datagen"].apply {
        name = "Fabric Data Generation $minecraft"
    }

    afterEvaluate {
        runConfigs.configureEach {
            applyMixinDebugSettings(::vmArg, ::property)
        }
    }
}

fletchingTable {
    fabric {
        entrypointMappings.put("fabric-datagen", "net.fabricmc.fabric.api.datagen.v1.FabricDataGeneratorEntrypoint")
    }

    mixins.register("main") {
        mixin("default", "configurable.mixins.json")
        mixin("client", "configurable.client.mixins.json") {
            environment = MixinEnvironment.Env.CLIENT
        }
    }
}

stonecutter {
  replacements.string(eval(current.version, ">=1.21.11")) {
    replace("net.minecraft.resources.ResourceLocation", "net.minecraft.resources.Identifier")
  }
  replacements.string(eval(current.version, ">=1.21.11")) {
    replace("ResourceLocation", "Identifier")
  }
}

tasks {
    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${mod<String>("version")}"))
        dependsOn("build")
    }

    processResources {
        exclude("META-INF/neoforge.mods.toml")
        exclude { it.name.endsWith("-accesstransformer.cfg") }
    }

    jar {
        dependsOn("runDatagen")
    }

    named<Jar>("sourcesJar") {
        dependsOn("runDatagen")
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
            groupId = "${mod<String>("group")}.${mod<String>("id")}"
            artifactId = "${mod<String>("id")}-$loader"
            version = "${mod<String>("version")}+$minecraft"

            from(components["java"])
        }
    }
}

publishMods {
    val mrToken = providers.gradleProperty("MODRINTH_TOKEN")
    val cfToken = providers.gradleProperty("CURSEFORGE_TOKEN")

    type = STABLE
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.named<Jar>("sourcesJar").map { it.archiveFile.get() })

    displayName = "${mod<String>("name")} Fabric ${mod<String>("version")} for $minecraft"
    version = mod<String>("version")
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add(loader)

    val compatibleVersions = sc.properties.raw("mod", "compatible_versions").to<List<String>>()

    modrinth {
        projectId = property("publishing.modrinth") as String
        accessToken = mrToken
        minecraftVersions.addAll(compatibleVersions)
        requires("fabric-api")
    }

    curseforge {
        projectId = property("publishing.curseforge") as String
        accessToken = cfToken
        minecraftVersions.addAll(compatibleVersions)
        requires("fabric-api")
    }
}