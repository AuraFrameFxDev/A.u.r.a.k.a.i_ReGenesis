//package dev.genesis.android.test
//
//import com.android.build.api.dsl.AaptOptions
//import com.android.build.api.dsl.AdbOptions
//import com.android.build.api.dsl.AndroidSourceSet
//import com.android.build.api.dsl.ApkSigningConfig
//import com.android.build.api.dsl.BuildFeatures
//import com.android.build.api.dsl.CompileOptions
//import com.android.build.api.dsl.ComposeOptions
//import com.android.build.api.dsl.DataBinding
//import com.android.build.api.dsl.ExternalNativeBuild
//import com.android.build.api.dsl.JacocoOptions
//import com.android.build.api.dsl.LibraryAndroidResources
//import com.android.build.api.dsl.LibraryBuildFeatures
//import com.android.build.api.dsl.LibraryBuildType
//import com.android.build.api.dsl.LibraryDefaultConfig
//import com.android.build.api.dsl.LibraryExtension
//import com.android.build.api.dsl.LibraryInstallation
//import com.android.build.api.dsl.LibraryProductFlavor
//import com.android.build.api.dsl.LibraryPublishing
//import com.android.build.api.dsl.Lint
//import com.android.build.api.dsl.LintOptions
//import com.android.build.api.dsl.Packaging
//import com.android.build.api.dsl.PrivacySandbox
//import com.android.build.api.dsl.Splits
//import com.android.build.api.dsl.TestCoverage
//import com.android.build.api.dsl.TestFixtures
//import com.android.build.api.dsl.TestOptions
//import com.android.build.api.dsl.ViewBinding
//import org.gradle.api.Action
//import org.gradle.api.NamedDomainObjectContainer
//
//class FakeLibraryExtension : LibraryExtension {
//
//    private val features = object : LibraryBuildFeatures {
//        override var compose: Boolean? = false
//        override var aidl: Boolean? = null
//        override var buildConfig: Boolean? = null
//        override var prefab: Boolean? = null
//        override var renderScript: Boolean? = null
//        override var resValues: Boolean? = null
//        override var shaders: Boolean? = null
//        override var viewBinding: Boolean? = null
//        override fun getExtensions(): org.gradle.api.plugins.ExtensionContainer = TODO()
//    }
//
//    override val buildFeatures: LibraryBuildFeatures
//        get() = features
//
//    override val aaptOptions: AaptOptions = TODO()
//    override val adbOptions: AdbOptions = TODO()
//    override val compileOptions: CompileOptions = TODO()
//    override val dataBinding: DataBinding = TODO()
//    override val viewBinding: ViewBinding = TODO()
//    override val jacoco: JacocoOptions = TODO()
//    override val testCoverage: TestCoverage = TODO()
//    override val lint: Lint = TODO()
//    override val lintOptions: LintOptions = TODO()
//    override val packagingOptions: Packaging = TODO()
//    override val packaging: Packaging = TODO()
//    override val signingConfigs: NamedDomainObjectContainer<out ApkSigningConfig> = TODO()
//    override val externalNativeBuild: ExternalNativeBuild = TODO()
//    override val testOptions: TestOptions = TODO()
//    override val splits: Splits = TODO()
//    override val composeOptions: ComposeOptions = TODO()
//    override val sourceSets: NamedDomainObjectContainer<out AndroidSourceSet> = TODO()
//    override var buildToolsVersion: String = TODO()
//    override var compileSdk: Int? = TODO()
//    override val androidResources: LibraryAndroidResources = TODO()
//    override val buildTypes: NamedDomainObjectContainer<out LibraryBuildType> = TODO()
//    override val installation: LibraryInstallation = TODO()
//    override val productFlavors: NamedDomainObjectContainer<out LibraryProductFlavor> = TODO()
//    override val defaultConfig: LibraryDefaultConfig = TODO()
//    override val publishing: LibraryPublishing = TODO()
//    override val privacySandbox: PrivacySandbox = TODO()
//    override val testFixtures: TestFixtures = TODO()
//    override val prefab: NamedDomainObjectContainer<Prefab> = TODO()
//    override var compileSdkExtension: Int? = TODO()
//    override var compileSdkMinor: Int? = TODO()
//    override var compileSdkPreview: String? = TODO()
//    override val experimentalProperties: MutableMap<String, Any> = TODO()
//    override val flavorDimensions: MutableList<String> = TODO()
//    override var namespace: String? = TODO()
//    override var ndkPath: String? = TODO()
//    override var ndkVersion: String = TODO()
//    override var resourcePrefix: String? = TODO()
//    override val sdkComponents: SdkComponents = TODO()
//    override var testBuildType: String = TODO()
//    override var testNamespace: String? = TODO()
//
//    /**
//     * Applies [action] to this fake extension's internal [BuildFeatures] instance.
//     *
//     * @param action Action to execute with the internal [BuildFeatures].
//
//     */
//    override fun buildFeatures(action: Action<BuildFeatures>) {
//        action.execute(features)
//    }
//
//    override fun testFixtures(action: TestFixtures.() -> Unit) {
//        TODO("Not yet implemented")
//    }
//}
