package dev.aurakai.auraframefx.security;

interface IRoyalGuardService {
    boolean validateAction(String actionKey, String payload);
    void triggerVeto(String reason);
    boolean verifyMemorySubstrate(String fileHash);
}
