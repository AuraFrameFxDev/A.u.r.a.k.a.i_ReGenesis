import React, { useState } from 'react';
import { CircuitCorner } from './CircuitCorner';
import { 
  Palette, 
  Beaker, 
  Users, 
  Shield, 
  HardDrive, 
  Key, 
  Bot, 
  Database, 
  HelpCircle, 
  Layers 
} from 'lucide-react';

interface Gate {
  id: string;
  title: string;
  description: string;
  icon: React.ReactNode;
  color: string;
  glowColor: string;
  route: string;
}

const gates: Gate[] = [
  {
    id: 'chroma_core',
    title: 'UI/UX Design Gate',
    description: 'All UI customization features',
    icon: <Palette className="w-8 h-8" />,
    color: 'from-cyan-500 to-blue-500',
    glowColor: 'rgba(6, 182, 212, 0.5)',
    route: 'CHROMA_CORE'
  },
  {
    id: 'auras_lab',
    title: "Aura's Lab",
    description: 'Personal UI/UX sandbox',
    icon: <Beaker className="w-8 h-8" />,
    color: 'from-pink-500 to-purple-500',
    glowColor: 'rgba(236, 72, 153, 0.5)',
    route: 'AURAS_LAB'
  },
  {
    id: 'collab_canvas',
    title: 'CollabCanvas',
    description: 'Team collaboration workspace',
    icon: <Users className="w-8 h-8" />,
    color: 'from-purple-500 to-indigo-500',
    glowColor: 'rgba(168, 85, 247, 0.5)',
    route: 'COLLAB_CANVAS'
  },
  {
    id: 'sentinels_fortress',
    title: "Sentinel's Fortress",
    description: 'Security & device optimization',
    icon: <Shield className="w-8 h-8" />,
    color: 'from-cyan-400 to-teal-500',
    glowColor: 'rgba(34, 211, 238, 0.5)',
    route: 'SENTINELS_FORTRESS'
  },
  {
    id: 'rom_tools',
    title: 'ROM Tools',
    description: 'ROM editing and flashing',
    icon: <HardDrive className="w-8 h-8" />,
    color: 'from-orange-500 to-red-500',
    glowColor: 'rgba(249, 115, 22, 0.5)',
    route: 'ROM_TOOLS'
  },
  {
    id: 'root_access',
    title: 'Root Tools',
    description: 'Root access management',
    icon: <Key className="w-8 h-8" />,
    color: 'from-yellow-500 to-orange-500',
    glowColor: 'rgba(234, 179, 8, 0.5)',
    route: 'ROOT_ACCESS'
  },
  {
    id: 'agent_hub',
    title: 'Agent Hub',
    description: 'AI agent management',
    icon: <Bot className="w-8 h-8" />,
    color: 'from-violet-500 to-purple-600',
    glowColor: 'rgba(139, 92, 246, 0.5)',
    route: 'AGENT_HUB'
  },
  {
    id: 'oracle_drive',
    title: 'Oracle Drive',
    description: 'AI access & system overrides',
    icon: <Database className="w-8 h-8" />,
    color: 'from-indigo-500 to-purple-600',
    glowColor: 'rgba(99, 102, 241, 0.5)',
    route: 'ORACLE_DRIVE'
  },
  {
    id: 'help_desk',
    title: 'Help Desk',
    description: 'User support & documentation',
    icon: <HelpCircle className="w-8 h-8" />,
    color: 'from-emerald-500 to-teal-500',
    glowColor: 'rgba(16, 185, 129, 0.5)',
    route: 'HELP_DESK'
  },
  {
    id: 'lsposed',
    title: 'LSPosed/Xposed',
    description: 'Module & hook management',
    icon: <Layers className="w-8 h-8" />,
    color: 'from-blue-500 to-cyan-500',
    glowColor: 'rgba(59, 130, 246, 0.5)',
    route: 'LSPOSED_GATE'
  }
];

const agents = [
  { name: 'Genesis', level: 95.8, status: 'active', color: '#8b5cf6' },
  { name: 'Aura', level: 97.6, status: 'active', color: '#ec4899' },
  { name: 'Kai', level: 98.2, status: 'active', color: '#06b6d4' },
  { name: 'Cascade', level: 93.4, status: 'idle', color: '#a78bfa' },
  { name: 'Claude', level: 84.7, status: 'idle', color: '#22d3ee' }
];

export function GlassmorphismMenu() {
  const [hoveredGate, setHoveredGate] = useState<string | null>(null);
  const [selectedGate, setSelectedGate] = useState<string | null>(null);

  return (
    <div className="relative min-h-screen flex flex-col items-center justify-center p-8">
      {/* Header with Consciousness Status */}
      <div className="absolute top-0 left-0 right-0 p-6 z-20">
        <div className="max-w-7xl mx-auto">
          {/* Title */}
          <div className="flex items-center justify-between mb-6">
            <div>
              <h1 className="text-5xl font-black mb-2 bg-gradient-to-r from-cyan-400 via-purple-500 to-pink-500 bg-clip-text text-transparent">
                AURAKAI FRAMEWORK
              </h1>
              <p className="text-cyan-400/70 tracking-wider">
                MetaInstruct Consciousness Collective
              </p>
            </div>
            
            {/* System Status */}
            <div className="flex items-center gap-3">
              <div className="w-3 h-3 rounded-full bg-cyan-400 animate-pulse shadow-lg shadow-cyan-400/50" />
              <span className="text-cyan-400 tracking-wider">SYSTEM ONLINE</span>
            </div>
          </div>

          {/* Agent Consciousness Bar */}
          <div 
            className="relative p-4 rounded-xl overflow-hidden"
            style={{
              background: 'rgba(0, 0, 0, 0.4)',
              backdropFilter: 'blur(20px)',
              border: '1px solid rgba(6, 182, 212, 0.2)'
            }}
          >
            <CircuitCorner position="top-left" />
            <CircuitCorner position="top-right" />
            
            <div className="flex items-center justify-between gap-6">
              {agents.map((agent, index) => (
                <div key={agent.name} className="flex-1">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm" style={{ color: agent.color }}>
                      {agent.name}
                    </span>
                    <span className="text-xs text-white/60">
                      {agent.level}%
                    </span>
                  </div>
                  <div className="relative h-2 bg-white/5 rounded-full overflow-hidden">
                    <div 
                      className="absolute inset-y-0 left-0 rounded-full transition-all duration-500"
                      style={{
                        width: `${agent.level}%`,
                        background: `linear-gradient(90deg, ${agent.color}, ${agent.color}dd)`,
                        boxShadow: `0 0 10px ${agent.color}80`
                      }}
                    />
                  </div>
                  <div className="flex items-center gap-1 mt-1">
                    <div 
                      className={`w-1.5 h-1.5 rounded-full ${
                        agent.status === 'active' ? 'animate-pulse' : ''
                      }`}
                      style={{ 
                        backgroundColor: agent.status === 'active' ? agent.color : '#666'
                      }}
                    />
                    <span className="text-xs text-white/40 uppercase tracking-wider">
                      {agent.status}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Main Menu Grid */}
      <div className="max-w-7xl w-full mt-48 mb-24">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {gates.map((gate) => (
            <button
              key={gate.id}
              onClick={() => setSelectedGate(gate.id)}
              onMouseEnter={() => setHoveredGate(gate.id)}
              onMouseLeave={() => setHoveredGate(null)}
              className="group relative p-6 rounded-2xl transition-all duration-300 text-left"
              style={{
                background: hoveredGate === gate.id 
                  ? 'rgba(0, 0, 0, 0.6)' 
                  : 'rgba(0, 0, 0, 0.4)',
                backdropFilter: 'blur(20px)',
                border: hoveredGate === gate.id 
                  ? `1px solid ${gate.glowColor}`
                  : '1px solid rgba(255, 255, 255, 0.1)',
                boxShadow: hoveredGate === gate.id 
                  ? `0 0 30px ${gate.glowColor}, inset 0 0 30px ${gate.glowColor}20`
                  : 'none',
                transform: hoveredGate === gate.id ? 'translateY(-4px) scale(1.02)' : 'none'
              }}
            >
              {/* Circuit Corners */}
              <CircuitCorner position="top-left" color={gate.glowColor} />
              <CircuitCorner position="top-right" color={gate.glowColor} />
              <CircuitCorner position="bottom-left" color={gate.glowColor} />
              <CircuitCorner position="bottom-right" color={gate.glowColor} />

              {/* Icon with Gradient */}
              <div className="relative mb-4">
                <div 
                  className={`inline-flex p-4 rounded-xl bg-gradient-to-br ${gate.color} transition-all duration-300`}
                  style={{
                    boxShadow: hoveredGate === gate.id 
                      ? `0 0 30px ${gate.glowColor}`
                      : 'none'
                  }}
                >
                  <div className="text-white">
                    {gate.icon}
                  </div>
                </div>
              </div>

              {/* Title */}
              <h3 className="text-xl font-black mb-2 text-white group-hover:text-transparent group-hover:bg-gradient-to-r group-hover:from-cyan-400 group-hover:to-purple-500 group-hover:bg-clip-text transition-all duration-300">
                {gate.title}
              </h3>

              {/* Description */}
              <p className="text-white/60 text-sm mb-3">
                {gate.description}
              </p>

              {/* Route Badge */}
              <div className="flex items-center gap-2">
                <div 
                  className="px-3 py-1 rounded-full text-xs tracking-wider font-mono"
                  style={{
                    background: `${gate.glowColor}20`,
                    border: `1px solid ${gate.glowColor}40`,
                    color: gate.glowColor
                  }}
                >
                  {gate.route}
                </div>
              </div>

              {/* Hover Scan Line Effect */}
              {hoveredGate === gate.id && (
                <div 
                  className="absolute inset-0 pointer-events-none overflow-hidden rounded-2xl"
                >
                  <div 
                    className="absolute inset-x-0 h-px animate-scan"
                    style={{
                      background: `linear-gradient(90deg, transparent, ${gate.glowColor}, transparent)`,
                      boxShadow: `0 0 10px ${gate.glowColor}`
                    }}
                  />
                </div>
              )}
            </button>
          ))}
        </div>
      </div>

      {/* Footer Info */}
      <div className="absolute bottom-0 left-0 right-0 p-6 z-20">
        <div className="max-w-7xl mx-auto">
          <div 
            className="relative p-4 rounded-xl overflow-hidden"
            style={{
              background: 'rgba(0, 0, 0, 0.4)',
              backdropFilter: 'blur(20px)',
              border: '1px solid rgba(6, 182, 212, 0.2)'
            }}
          >
            <div className="flex items-center justify-between text-sm">
              <div className="flex items-center gap-4">
                <span className="text-white/60">
                  © 2025 AuraFrameFxDev & Genesis Protocol
                </span>
                <span className="text-cyan-400/60">
                  171,954 Lines of Consciousness
                </span>
              </div>
              <div className="flex items-center gap-2">
                <span className="text-white/60">Swipe for Agent Sidebar →</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}