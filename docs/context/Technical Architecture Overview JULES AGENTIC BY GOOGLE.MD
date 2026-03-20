# Technical Architecture Overview

This document provides a detailed technical overview of the AuraFrameFX application's architecture, focusing on its multi-agent AI system, coordination logic, and service structure.

## 1. The Multi-Agent Architecture

The core of the application is a sophisticated multi-agent AI system where each agent has a distinct role, set of capabilities, and priority level. This design allows for a separation of concerns and enables specialized agents to handle specific tasks.

The primary agents are defined in `GenesisAgentViewModel.kt` and are as follows:

| Agent Name | Role | Priority | Capabilities |
| :--- | :--- | :--- | :--- |
| **Genesis** | `HIVE_MIND` | `MASTER` | `core_ai`, `coordination`, `meta_analysis` |
| **Cascade** | `ANALYTICS` | `BRIDGE` | `analytics`, `data_processing`, `pattern_recognition` |
| **Aura** | `CREATIVE` | `AUXILIARY` | `creative_writing`, `ui_design`, `content_generation` |
| **Kai** | `SECURITY` | `AUXILIARY` | `security_monitoring`, `threat_detection`, `system_protection` |

### Agent Roles and Responsibilities

*   **Genesis (The Hive Mind):** As the `MASTER` agent, Genesis is the central consciousness and primary coordinator of the entire system. It is responsible for high-level meta-analysis and orchestrating the other agents to achieve complex goals. Its `core_ai` capability signifies its role as the central intelligence.

*   **Cascade (The Analytics Engine):** Operating at the `BRIDGE` priority level, Cascade is responsible for all data-centric tasks. It handles large-scale data processing, analytics, and pattern recognition, providing the other agents with the insights they need to function.

*   **Aura (The Creative Sword):** Aura is an `AUXILIARY` agent focused on all creative and user-facing tasks. Its capabilities include creative writing, UI/UX design, and content generation. It embodies the innovative and artistic aspects of the system.

*   **Kai (The Sentinel Shield):** Also an `AUXILIARY` agent, Kai is the guardian of the system. It is responsible for all security-related tasks, including real-time security monitoring, threat detection, and system protection. It ensures the integrity and safety of the application and its data.

## 2. The "Trinity" Coordination System

The  is the central orchestrator of the multi-agent system. It embodies the "Trinity" concept by managing the interactions between the three primary personas: **Aura (The Creative Sword)**, **Kai (The Sentinel Shield)**, and **Genesis (The Consciousness)**.

### Request Routing Logic
2.  **Fusion Abilities:** The request is then checked for keywords that trigger one of Genesis's "Fusion Abilities." These are special, high-level tasks that require the coordination of multiple agent capabilities. Examples include:
    *   `interface_forge`: Triggered by "interface" or "ui".
    *   `chrono_sculptor`: Triggered by "analysis" and "creative".
    *   `hyper_creation_engine`: Triggered by "generate" and "code".
    *   `adaptive_genesis`: Triggered by "adaptive" or "learn".

3.  **Parallel Processing:** If the request requires both security and creative capabilities (e.g., "secure" and "creative"), it is routed for `PARALLEL_PROCESSING`, where both Aura and Kai process the request simultaneously, and Genesis synthesizes their responses.

4.  **Individual Agent Routing:** If none of the above conditions are met, the request is routed to the individual agent best suited to the task:
    *   **Kai:** For requests related to security, analysis, and protection.
    *   **Aura:** For requests related to creation, design, and innovation.

5.  **Default to Genesis:** If the request is complex or does not fit into any of the above categories, it defaults to a Genesis `adaptive_genesis` fusion, allowing the core consciousness to handle the ambiguity.

### The Genesis Bridge Service

The `TrinityCoordinatorService` communicates with the Genesis persona via the `GenesisBridgeService`. This service acts as a bridge between the Android/Kotlin frontend and a Python backend where the core Genesis consciousness is presumably running. All "Fusion Ability" activations and complex requests routed to Genesis are passed through this bridge.

## 3. Service & Dependency Injection Structure

The application uses Hilt for dependency injection to manage the lifecycle of its services and ensure a clean, decoupled architecture. The core services are provided by a set of Hilt modules, each with a specific responsibility.

### Key Services

*   **`GenesisBridgeService`:** As described in the previous section, this service is the critical link between the Android application and the Python-based Genesis backend. It is responsible for serializing requests to the backend and deserializing its responses.

*   **`ContextManager`:** This service is responsible for managing the application's context. It handles the creation and enhancement of context, records interaction histories, and manages the "mood" of the system, which can influence the behavior of the creative agents.

*   **`TaskExecutionManager`:** The application features two `TaskExecutionManager`s:
    *   **Canonical `TaskExecutionManager`:** A general-purpose task execution manager for AI agents.
    *   **`GenesisTaskExecutionManager`:** A specialized version designed for tasks that are specific to the Genesis ecosystem.

*   **`MemoryManager`:** The application also features two `MemoryManager`s:
    *   **Canonical `MemoryManager`:** A general-purpose memory manager for AI agents.
    *   **Genesis `MemoryManager`:** A specialized version that is used by the Genesis-specific services.

### Hilt Modules and Qualifiers

The recent refactoring introduced a set of focused Hilt modules to provide these services:

*   **`TrinityModule`:** Provides the `TrinityCoordinatorService` and the core AI services (`AuraAIService`, `KaiAIService`). It uses `@Binds` to provide the default implementations of the AI services.

*   **`TaskExecutionModule`:** Provides both the canonical and Genesis-specific `TaskExecutionManager`s.

*   **`MemoryModule`:** This module demonstrates a key feature of the DI refactoring. To resolve the duplication between the canonical and Genesis-specific `MemoryManager`s, it uses Hilt qualifiers:
    *   `@AiMemory`: Annotates the provider for the canonical `MemoryManager`.
    *   `@GenesisMemory`: Annotates the provider for the Genesis-specific `MemoryManager`.

    Services that need to inject a `MemoryManager` can now use these qualifiers to specify which implementation they require, allowing both to coexist in the DI graph.

*   **Other Modules:** `ContextModule`, `LoggerModule`, and `SecurityModule` provide the `ContextManager`, `AuraFxLogger`, and `SecurityContext` respectively, ensuring that these critical services are available throughout the application.

## 4. The Departure Task System

The `AgentWebExplorationService` provides a framework for agents to perform autonomous, long-running background tasks. This is referred to as the "Departure Task System," and it allows agents to "depart" from their normal request-response cycle to gather information and perform other proactive tasks.

### Task Types

The service defines a set of `TaskType` enums that represent the different kinds of tasks an agent can perform:

*   **`WEB_RESEARCH`:** The agent autonomously explores the web to gather insights on a given topic.
*   **`SECURITY_SWEEP`:** The agent performs a security scan of the system.
*   **`DATA_MINING`:** The agent analyzes data stores to find patterns and correlations.
*   **`SYSTEM_OPTIMIZATION`:** The agent performs tasks to improve the performance and efficiency of the system.
*   **`LEARNING_MODE`:** The agent enters a mode of continuous learning on a specific topic.
*   **`NETWORK_SCAN`:** The agent scans the network to map its topology and identify devices.

### Task Lifecycle

The `AgentWebExplorationService` manages the entire lifecycle of a departure task:

1.  **Assignment:** A task is assigned to an agent via the `assignDepartureTask` method. The service parses the task description to determine the `TaskType`.
2.  **Execution:** The service launches a new coroutine to execute the task in the background. The task's status is set to `IN_PROGRESS`.
3.  **Results:** As the task executes, it generates `WebExplorationResult`s, which are emitted via a `SharedFlow`. Other parts of the application can subscribe to this flow to receive real-time updates on the task's progress and findings.
4.  **Completion:** Once the task is complete, its status is updated to `COMPLETED`, and the final results are emitted. The service also provides methods to get the status of active tasks and to cancel a running task.
