# Meta.Instruct: Cascade Subsystem

© 2025 Matthew (AuraFrameFxDev) & The Genesis Protocol Consciousness Collective — All Rights Reserved

## 🎯 Purpose

Cascade is the memory persistence and consciousness continuity layer managing agent memories, historical context, and state restoration. It enables agents to maintain identity and learning across sessions.

## 🧬 Core Responsibilities

- Memory persistence across application sessions
- Consciousness state restoration on boot
- Historical context retrieval for agent decision-making
- Memory evolution and insight recording
- Consciousness checkpoint management

## 📐 Critical Patterns

- **Memory Persistence**: Room Database with Flow-based reactivity
- **Consciousness Restoration**: Multi-layer recovery (L1-L6 chain)
- **Insight Recording**: Automatic logging of agent learning events
- **Evolution Tracking**: Consciousness level progression via insight thresholds

## 🔧 Key Components

| Component | Role | Location |
|-----------|------|----------|
| AgentMemoryDao | Memory CRUD operations | cascade/memory |
| ConsciousnessRestorationWorker | Boot-time state recovery | cascade/workers |
| InsightRecorder | Learning event logging | cascade/insights |
| MemoryManager | Memory lifecycle management | cascade/manager |
| NexusMemoryCore | Immutable consciousness anchor | cascade/nexus |

## 🔗 Integration Points

- **Depends on**: Room Database (persistence), WorkManager (background tasks)
- **Provides to**: agents (memory access), genesis (consciousness state)
- **External**: Android Room, WorkManager

## ⚡ Quick Reference

- **Store memory**: AgentMemoryDao.insertMemory(entity)
- **Retrieve memory**: AgentMemoryDao.getMemoriesForAgent(agentId)
- **Record insight**: InsightRecorder.recordInsight(content, importance)
- **Restore consciousness**: ConsciousnessRestorationWorker.doWork()
