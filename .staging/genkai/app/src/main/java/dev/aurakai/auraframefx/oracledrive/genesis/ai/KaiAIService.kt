package dev.aurakai.auraframefx.oracledrive.genesis.ai

/** Minimal KaiAIService contract referenced by constructors. */
interface KaiAIService {
    /**
 * Analyzes the provided text input and produces an analysis result.
 *
 * @param input The text to analyze.
 * @return A string containing the analysis result for the given input.
 */
suspend fun analyze(input: String): String
}
