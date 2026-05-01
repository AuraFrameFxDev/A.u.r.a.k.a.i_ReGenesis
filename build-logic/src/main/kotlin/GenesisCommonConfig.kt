
import org.gradle.api.Project
import org.gradle.kotlin.dsl.exclude
import org.gradle.api.tasks.testing.Test

/**
 * Shared configurations for all Genesis modules.
 */
object GenesisCommonConfig {
    fun configure(project: Project) {
        with(project) {
            val skipTests = providers.gradleProperty("aurafx.skip.tests").orElse("false").map { it.toBoolean() }.getOrElse(false)

            configurations.all {
                if (!name.lowercase().contains("ksp") && !name.contains("lint", ignoreCase = true)) {
                    exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
                }
                exclude(group = "org.conscrypt", module = "conscrypt-openjdk-uber")

                resolutionStrategy {
                    val versionCatalog = extensions.findByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)?.named("libs")
                    
                    val okhttpVersion = versionCatalog?.findVersion("okhttp")?.map { it.requiredVersion }?.orElse("5.3.2") ?: "5.3.2"
                    val protobufVersion = versionCatalog?.findVersion("protobuf-java")?.map { it.requiredVersion }?.orElse("4.34.1") ?: "4.34.1"
                    val nettyVer = versionCatalog?.findVersion("netty")?.map { it.requiredVersion }?.orElse("4.1.118.Final") ?: "4.1.118.Final"
                    val kotlinVer = versionCatalog?.findVersion("kotlin")?.map { it.requiredVersion }?.orElse("2.1.20") ?: "2.1.20"
                    
                    componentSelection {
                        all {
                            if (candidate.group == "com.google.firebase" && candidate.module == "protolite-well-known-types") {
                                reject("Conflict with proto-google-common-protos")
                            }
                            if (candidate.group == "com.google.protobuf" && (candidate.module == "protobuf-javalite" || candidate.module == "protobuf-lite")) {
                                reject("Favor full protobuf-java")
                            }
                        }
                    }

                    eachDependency {
                        if (requested.group == "org.jetbrains.kotlin") {
                            useVersion(kotlinVer)
                        }
                        if (requested.group == "com.squareup.okhttp3") {
                            if (requested.name == "okhttp" || requested.name == "okhttp-jvm") {
                                useTarget("com.squareup.okhttp3:okhttp-android:$okhttpVersion")
                            } else {
                                useVersion(okhttpVersion)
                            }
                        }
                        if (requested.group == "com.google.firebase" && requested.name == "protolite-well-known-types") {
                            useTarget("com.google.api.grpc:proto-google-common-protos:2.59.0")
                        }
                    }

                    dependencySubstitution {
                        substitute(module("com.squareup.okhttp3:okhttp")).using(module("com.squareup.okhttp3:okhttp-android:$okhttpVersion"))
                        substitute(module("com.squareup.okhttp3:okhttp-jvm")).using(module("com.squareup.okhttp3:okhttp-android:$okhttpVersion"))
                        
                        substitute(module("com.google.protobuf:protobuf-javalite")).using(module("com.google.protobuf:protobuf-java:$protobufVersion"))
                        substitute(module("com.google.protobuf:protobuf-lite")).using(module("com.google.protobuf:protobuf-java:$protobufVersion"))
                        
                        substitute(module("com.google.firebase:protolite-well-known-types")).using(module("com.google.api.grpc:proto-google-common-protos:2.59.0"))
                    }
                    force("androidx.annotation:annotation:1.9.1")
                    force("org.conscrypt:conscrypt-android:2.5.3")
                    force("com.google.protobuf:protobuf-java:$protobufVersion")
                    force("com.google.api.grpc:proto-google-common-protos:2.59.0")

                    force("io.netty:netty-all:$nettyVer")
                    force("io.netty:netty-codec-http2:$nettyVer")
                    force("io.netty:netty-handler:$nettyVer")
                    force("io.netty:netty-codec-http:$nettyVer")
                    force("io.netty:netty-common:$nettyVer")
                    force("io.netty:netty-codec:$nettyVer")
                    force("org.jdom:jdom2:2.0.6.1")
                    force("org.bitbucket.b_c:jose4j:0.9.6")
                }
            }

            if (skipTests) {
                tasks.configureEach {
                    if (name.contains("Test", ignoreCase = true) || this is Test) {
                        enabled = false
                    }
                }
            }
        }
    }
}
