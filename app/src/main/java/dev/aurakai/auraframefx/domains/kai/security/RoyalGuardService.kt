package dev.aurakai.auraframefx.domains.kai.security

/**
 * @deprecated Replaced by [dev.aurakai.auraframefx.domains.kai.security.provenance.RoyalGuardServiceImpl]
 * which uses hardware-backed provenance chain validation via ProvenanceValidator.
 */
@Deprecated(
    "Use RoyalGuardServiceImpl in the provenance package instead",
    replaceWith = ReplaceWith(
        "RoyalGuardServiceImpl",
        "dev.aurakai.auraframefx.domains.kai.security.provenance.RoyalGuardServiceImpl"
    )
)
typealias RoyalGuardService = dev.aurakai.auraframefx.domains.kai.security.provenance.RoyalGuardServiceImpl

