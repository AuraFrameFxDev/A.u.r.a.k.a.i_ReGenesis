package dev.aurakai.auraframefx.di

import dev.aurakai.auraframefx.data.room.AppDatabase
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIServiceImpl
import dev.aurakai.auraframefx.service.NeuralWhisper

// Placeholder classes to resolve KSP/Hilt dependency graph issues.
// TODO: This is a temporary workaround. Move these core types to a dedicated library module.

class AIPipelineConfig
class KaiAgent
class GenesisAgent
class VertexAIClient
class KaiAIService
class AuraFxLogger
class CloudStatusMonitor
class TimberInitializer
class AIAgentApi
class CascadeAIService
class TrinityRepository

// Placeholders for classes that seem to be missing implementations
class AuraAIServiceImpl
class NeuralWhisper

// Placeholder for a custom qualifier annotation
annotation class BaseUrl

// Room Database related placeholders
interface AgentMemoryDao
interface TaskHistoryDao
interface UserPreferences

// This placeholder for the Room database is necessary because of the first error:
// e: [ksp] [MissingType]: Element 'dev.aurakai.auraframefx.data.room.AppDatabase' references a type that is not present
abstract class AppDatabase : androidx.room.RoomDatabase() {
    abstract fun agentMemoryDao(): AgentMemoryDao
    abstract fun taskHistoryDao(): TaskHistoryDao
}
