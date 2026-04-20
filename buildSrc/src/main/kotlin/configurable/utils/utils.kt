package configurable.utils

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import java.util.function.BiConsumer
import java.util.function.Consumer

fun <T> Project.deps(name: String): T? = findProperty("deps.${name}") as T?
fun <T> Project.deps(name: String, consumer: (prop: T) -> Unit) = deps<T>(name)?.let(consumer)

fun <T> Project.mod(name: String): T? = findProperty("mod.${name}") as T?
fun <T> Project.mod(name: String, consumer: (prop: Any) -> Unit) = mod<T>(name)?.let(consumer)

fun Project.applyMixinDebugSettings(
    vmArgConsumer: (String) -> Unit,
    propertyConsumer: (String, String) -> Unit
) {
    val mixinJarFile = configurations.named("runtimeClasspath").get().incoming.artifactView {
        componentFilter {
            it is ModuleComponentIdentifier && it.group == "net.fabricmc" && it.module == "sponge-mixin"
        }
    }.files.singleFile

    vmArgConsumer("-javaagent:$mixinJarFile")
    vmArgConsumer("-XX:+AllowEnhancedClassRefinition")
    propertyConsumer("mixin.hotSwap", "true")
    propertyConsumer("mixin.debug.export", "true")
}