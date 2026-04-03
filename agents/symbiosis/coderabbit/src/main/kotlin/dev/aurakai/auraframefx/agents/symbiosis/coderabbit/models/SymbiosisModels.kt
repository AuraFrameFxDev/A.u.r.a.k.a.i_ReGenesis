package dev.aurakai.auraframefx.agents.symbiosis.coderabbit.models

data class HotspotReport(
    val repoPath: String,
    val hotspots: List<String>,
    val complexityScore: Float,
    val timestamp: Long = System.currentTimeMillis()
)

data class PatchProposal(
    val id: String,
    val targetFile: String,
    val diff: String,
    val rationale: String
)

data class IssueTemplate(
    val title: String,
    val body: String,
    val labels: List<String>
)
