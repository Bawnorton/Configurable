plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.20.1-fabric-yarn" /* [SC] DO NOT EDIT */

stonecutter registerChiseled tasks.register("chiseledBuildAndCollect", stonecutter.chiseled) {
    group = "project"
    ofTask("buildAndCollect")
}

stonecutter registerChiseled tasks.register("chiseledPublishMods", stonecutter.chiseled) {
    group = "project"
    ofTask("publishMods")
}

stonecutter registerChiseled tasks.register("chiseledPublishMavenLocal", stonecutter.chiseled) {
    group = "publishing"
    ofTask("publishMavenPublicationToMavenLocal")
}

stonecutter registerChiseled tasks.register("chiseledPublishMavenRemote", stonecutter.chiseled) {
    group = "publishing"
    ofTask("publishMavenPublicationToBawnortonRepository")
}

stonecutter configureEach {
    val current = project.property("loom.platform")
    val platforms = listOf("fabric", "neoforge").map { it to (it == current) }
    consts(platforms)

    val currentMappings = project.property("mappings")
    val mappings = listOf("yarn", "mojmap").map { it to (it == currentMappings) }
    consts(mappings)
}
