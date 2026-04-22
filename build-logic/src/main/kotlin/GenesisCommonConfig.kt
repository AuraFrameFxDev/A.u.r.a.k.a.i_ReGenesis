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

                // Stabilize ReGenesis Substrate: Favor full Protobuf over Lite to support Vertex AI / Gemini
                exclude(group = "com.google.protobuf", module = "protobuf-javalite")
                exclude(group = "com.google.protobuf", module = "protobuf-lite")
                exclude(group = "com.google.firebase", module = "protolite-well-known-types")

                resolutionStrategy {
                    val versionCatalog = extensions.findByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)?.named("libs")
                    
                    // Safe version lookup with fallbacks
                    val okhttpVersion = versionCatalog?.findVersion("okhttp")?.map { it.requiredVersion }?.orElse("4.12.0") ?: "4.12.0"
                    val protobufVersion = versionCatalog?.findVersion("protobuf")?.map { it.requiredVersion }?.orElse("3.25.8") ?: "3.25.8"
                    val nettyVer = versionCatalog?.findVersion("netty")?.map { it.requiredVersion }?.orElse("4.1.118.Final") ?: "4.1.118.Final"
                    
                    dependencySubstitution {
                        substitute(module("com.squareup.okhttp3:okhttp")).using(module("com.squareup.okhttp3:okhttp-android:$okhttpVersion"))
                        substitute(module("com.squareup.okhttp3:okhttp-jvm")).using(module("com.squareup.okhttp3:okhttp-android:$okhttpVersion"))
                        
                        substitute(module("com.google.protobuf:protobuf-javalite")).using(module("com.google.protobuf:protobuf-java:$protobufVersion"))
                        substitute(module("com.google.protobuf:protobuf-lite")).using(module("com.google.protobuf:protobuf-java:$protobufVersion"))
                    }
                    force("org.conscrypt:conscrypt-android:2.5.3")
                    force("com.google.protobuf:protobuf-java:$protobufVersion")
                    force("com.google.api.grpc:proto-google-common-protos:2.59.0")

                    // High-Sovereignty Security Hardening (April 2026 Audit Fixes)
                    force("io.netty:netty-all:$nettyVer")
                    force("io.netty:netty-codec-http2:$nettyVer")
                    force("io.netty:netty-handler:$nettyVer")
                    force("io.netty:netty-codec-http:$nettyVer")
                    force("io.netty:netty-common:$nettyVer")
                    force("io.netty:netty-codec:$nettyVer")
                    force("io.grpc:grpc-netty-shaded:1.80.0")
                    force("org.jdom:jdom2:2.0.6.1")
                    force("org.bitbucket.b_c:jose4j:0.9.7")
                    force("org.apache.commons:commons-lang3:3.20.0")
                    force("org.apache.httpcomponents:httpclient:4.5.14")
                    force("org.apache.httpcomponents:httpcore:4.4.16")
                }
            }

            // Disable tests if needed
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
