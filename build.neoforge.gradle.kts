import configurable.utils.*
import dev.kikugie.fletching_table.annotation.MixinEnvironment

plugins {
    kotlin("jvm")
    `maven-publish`
    id("net.neoforged.moddev")
    id("configurable.common")
    id("me.modmuss50.mod-publish-plugin")
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.neoforge") version "0.1.0-alpha.14"
    id("dev.isxander.secrets") version "0.1.0"
}

repositories {
    mavenCentral()
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://maven.parchmentmc.org")
}

val minecraft: String by project
val loader: String by project

sc.properties.tags(minecraft)

base.archivesName = "${mod("id")}-${mod("version")}+$minecraft-$loader"

dependencies {
    jarJar(api(annotationProcessor("com.google.auto.service:auto-service:1.0")!!)!!)
    jarJar(implementation("org.quiltmc.parsers:json:0.3.1")!!)
    jarJar(implementation("org.quiltmc.parsers:gson:0.3.1")!!)

    implementation("com.palantir.javapoet:javapoet:0.7.0")
    implementation("com.electronwill.night-config:toml:3.8.3")
    implementation("org.slf4j:slf4j-api:2.0.9")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.google.testing.compile:compile-testing:0.21.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    withSourcesJar()
    if (stonecutter.eval(minecraft, "<=1.21.11")){
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    } else {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }
}

neoForge {
    version = deps("neoforge")

    validateAccessTransformers = true
    accessTransformers.from(rootProject.file("src/main/resources/$minecraft-accesstransformer.cfg"))

    mods {
        register(mod("id")!!) {
            sourceSet(sourceSets["main"])
        }
    }

    deps("parchment") {
        if (stonecutter.eval(stonecutter.current.version, "<=1.21.11")) {
            parchment {
                val (mc, version) = it.split(':')
                mappingsVersion = version
                minecraftVersion = mc
            }
        }
    }

    runs {
        all {
            gameDirectory = rootProject.file("run")
        }

        register("client") {
            ideName = "NeoForge Client $minecraft"
            client()

            programArgument("--username=Bawnorton")
            programArgument("--uuid=17c06cab-bf05-4ade-a8d6-ed14aaf70545")
        }

        register("data") {
            disableIdeRun()
            if (stonecutter.eval(minecraft, ">1.21.1")) {
                serverData()
            } else {
                data()
            }
            programArguments.addAll(
                "--mod", "${mod("id")}",
                "--output", project.file("src/main/generated").toString()
            )
        }
    }

    afterEvaluate {
        runs.configureEach {
            applyMixinDebugSettings(::jvmArgument, ::systemProperty)
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

stonecutter {
  replacements.string(eval(current.version, ">=1.21.11")) {
    replace("net.minecraft.resources.ResourceLocation", "net.minecraft.resources.Identifier")
  }
  replacements.string(eval(current.version, ">=1.21.11")) {
    replace("ResourceLocation", "Identifier")
  }
}

tasks {
    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        from(jar.map { it.archiveFile })
        into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
        dependsOn("build")
    }

    build {
        dependsOn("runData")
    }

    processResources {
        exclude("fabric.mod.json", "configurable.fabric.mixins.json")
        exclude { it.name.endsWith(".accesswidener") }
    }

    test {
        useJUnitPlatform()
        outputs.upToDateWhen { false }
        jvmArgs("--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
        jvmArgs("--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED")
        jvmArgs("--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED")
    }
}

sourceSets {
    test {
        compileClasspath += sourceSets.main.get().compileClasspath
        runtimeClasspath += sourceSets.main.get().runtimeClasspath
    }
}

val isPublishing = gradle.startParameter.taskNames.any {
    it.contains("publish", ignoreCase = true)
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            name = "bawnorton"
            url = uri("https://maven.bawnorton.com/releases")

            if(isPublishing) {
                credentials {
                    username = onePassword["op://Private/Maven API Key/username"].get()
                    password = onePassword["op://Private/Maven API Key/credential"].get()
                }
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
    val mrTokenProvider = onePassword["op://Private/Modrinth API Key/credential"]
    val cfTokenProvider = onePassword["op://Private/Curseforge API Key/credential"]

    type = STABLE
    file = tasks.jar.map { it.archiveFile.get() }
    additionalFiles.from(tasks.named<org.gradle.jvm.tasks.Jar>("sourcesJar").map { it.archiveFile.get() })

    displayName = "${mod("name")} Neoforge ${mod("version")} for $minecraft"
    version = mod("version")
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    modLoaders.add(loader)

    val compatibleVersions = sc.properties.raw("mod", "compatible_versions").to<List<String>>()

    modrinth {
        projectId = property("publishing.modrinth") as String
        accessToken = mrTokenProvider
        minecraftVersions.addAll(compatibleVersions)
    }

    curseforge {
        projectId = property("publishing.curseforge") as String
        accessToken = cfTokenProvider
        minecraftVersions.addAll(compatibleVersions)
    }
}