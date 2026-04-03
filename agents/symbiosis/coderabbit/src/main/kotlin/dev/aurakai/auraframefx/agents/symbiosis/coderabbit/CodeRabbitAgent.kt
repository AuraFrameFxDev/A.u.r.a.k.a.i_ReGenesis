package dev.aurakai.auraframefx.agents.symbiosis.coderabbit

import dev.aurakai.auraframefx.agents.symbiosis.coderabbit.models.*
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.core.security.ProvenanceValidator
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CodeRabbitAgent @Inject constructor(
    private val pandora: PandoraBoxService,
    private val provenance: ProvenanceValidator
) {

    suspend fun checkRepoHotspots(repoPath: String): HotspotReport {
        // [LDO-DBG] Gating: Requires CREATIVE unlock for code analysis
        check(pandora.isCapabilityUnlocked(AgentCapabilityCategory.CREATIVE)) {
            "Pandora Tier Access Denied: CREATIVE capability required for hotspot scan."
        }

        // [LDO-DBG] Hotspot scan initiated for: $repoPath
        provenance.createProvenance("coderabbit", "hotspot_scan")
        
        return HotspotReport(
            repoPath = repoPath,
            hotspots = listOf("di/ConsciousnessModule.kt", "navigation/ReGenesisNavHost.kt"),
            complexityScore = 0.85f
        )
    }

    suspend fun proposePatches(hotspots: HotspotReport): List<PatchProposal> {
        // [LDO-DBG] Gating: Requires SYSTEM unlock for proposing structural changes
        // Using ROOT as a proxy for SYSTEM capability category
        check(pandora.isCapabilityUnlocked(AgentCapabilityCategory.ROOT)) {
            "Pandora Tier Access Denied: ROOT capability required for patch proposal."
        }

        Timber.d("CodeRabbit Symbiosis: Proposing patches for ${hotspots.hotspots.size} hotspots")
        
        return listOf(
            PatchProposal(
                id = "PATCH-WIRING-001",
                targetFile = "ReGenesisNavHost.kt",
                diff = "+ import dev.aurakai.auraframefx.navigation.goToGateImagePicker",
                rationale = "Normalize string-literal routes to typed shortcuts."
            )
        )
    }
}
