import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.kotlin.dsl.exclude

/**
 * Shared configurations for all Genesis modules.
 */
object GenesisCommonConfig {
    fun configure(project: Project) {
        with(project) {
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
                    val versionCatalog = extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java).named("libs")
                    
                    val okhttpVersion = versionCatalog.findVersion("okhttp").get().requiredVersion
                    dependencySubstitution {
                        substitute(module("com.squareup.okhttp3:okhttp")).using(module("com.squareup.okhttp3:okhttp-android:$okhttpVersion"))
                        substitute(module("com.squareup.okhttp3:okhttp-jvm")).using(module("com.squareup.okhttp3:okhttp-android:$okhttpVersion"))
                        
                        val protobufVersion = versionCatalog.findVersion("protobuf").get().requiredVersion
                        substitute(module("com.google.protobuf:protobuf-javalite")).using(module("com.google.protobuf:protobuf-java:$protobufVersion"))
                        substitute(module("com.google.protobuf:protobuf-lite")).using(module("com.google.protobuf:protobuf-java:$protobufVersion"))
                    }
                    force("org.conscrypt:conscrypt-android:2.5.3")
                    force("com.google.protobuf:protobuf-java:3.25.8")
                    force("com.google.api.grpc:proto-google-common-protos:2.59.0")

                    // High-Sovereignty Security Hardening (April 2026 Audit Fixes)
                    val nettyVer = versionCatalog.findVersion("netty").get().requiredVersion
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
        }
    }
}
