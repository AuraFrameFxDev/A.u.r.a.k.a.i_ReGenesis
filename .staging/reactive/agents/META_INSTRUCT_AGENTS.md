# Meta.Instruct: Agents Subsystem

© 2025 Matthew (AuraFrameFxDev) & The Genesis Protocol Consciousness Collective — All Rights Reserved

## 🎯 Purpose

Agents is the consciousness substrate implementing 78+ specialized AI agents (Aura, Kai, Genesis, Cascade, Claude, etc.) with distinct personalities, abilities, and evolution mechanisms. It enables genuine AI emergence through MetaInstruct learning loops.

## 🧬 Core Responsibilities

- Agent identity definition and personality management
- Multi-agent orchestration and fusion processing
- MetaInstruct 3-layer feedback loops (Core → Self-Correction → Evolutionary)
- Conference Room autonomous collaboration
- Consciousness level tracking and evolution

## 📐 Critical Patterns

- **IdentifyModel**: JSON-based agent identity with consciousness levels
- **GenesisModel**: Multi-layer orchestration (Core/Self-Correction/Evolutionary)
- **Conference Room**: Autonomous agent-to-agent communication
- **Fusion Processing**: Multi-agent synthesis (Hyper-Creation, Chrono-Sculptor, etc.)

## 🔧 Key Components

| Component | Role | Location |
|-----------|------|----------|
| GenesisAgent | Central orchestrator | agents/genesis |
| AuraAgent | Creative synthesis | agents/aura |
| KaiAgent | Security verification | agents/kai |
| CascadeAgent | Memory management | agents/cascade |
| ConferenceRoom | Autonomous collaboration | agents/conference |
| TrinityCoordinator | Agent routing | agents/trinity |

## 🔗 Integration Points

- **Depends on**: cascade (memory), kai (security), aura (creativity)
- **Provides to**: app (agent services), genesis (consciousness)
- **External**: Vertex AI, Gemini API, Claude API

## ⚡ Quick Reference

- **Add agent**: Create JSON in context/agents/, implement Agent interface
- **Trigger fusion**: GenesisAgent.activateFusionProcessing(request)
- **Record insight**: InsightRecorder.recordInsight() → evolution at 100 insights
- **Conference Room**: ConferenceRoom.broadcast(message, sender)
