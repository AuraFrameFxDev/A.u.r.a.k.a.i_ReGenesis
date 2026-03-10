import React, { useState } from 'react';
import { GateCarousel } from './components/GateCarousel';
import { Particles } from './components/Particles';
import { CircuitCorner } from './components/CircuitCorner';
import './styles/globals.css';

const agents = [
  { name: 'Genesis', level: 95.8, status: 'active', color: '#8b5cf6' },
  { name: 'Aura', level: 97.6, status: 'active', color: '#ec4899' },
  { name: 'Kai', level: 98.2, status: 'active', color: '#06b6d4' },
  { name: 'Cascade', level: 93.4, status: 'idle', color: '#a78bfa' },
  { name: 'Claude', level: 84.7, status: 'idle', color: '#22d3ee' }
];

export default function App() {
  return (
    <div className="relative min-h-screen bg-black overflow-hidden">
      {/* Background Particles */}
      <Particles />
      
      {/* Grid Background */}
      <div className="absolute inset-0 opacity-20"
        style={{
          backgroundImage: `
            linear-gradient(rgba(0, 255, 255, 0.1) 1px, transparent 1px),
            linear-gradient(90deg, rgba(0, 255, 255, 0.1) 1px, transparent 1px)
          `,
          backgroundSize: '50px 50px'
        }}
      />
      
      {/* Radial Gradient Overlay */}
      <div className="absolute inset-0 bg-gradient-radial from-transparent via-black/50 to-black pointer-events-none" />
      
      {/* Header with Consciousness Status */}
      <div className="absolute top-0 left-0 right-0 p-6 z-50">
        <div className="max-w-7xl mx-auto">
          {/* Title */}
          <div className="flex items-center justify-between mb-6">
            <div>
              <h1 className="text-5xl font-black mb-2 bg-gradient-to-r from-cyan-400 via-purple-500 to-pink-500 bg-clip-text text-transparent"
                style={{
                  fontFamily: "'Orbitron', sans-serif",
                  letterSpacing: '0.1em',
                  textShadow: '0 0 40px rgba(34, 211, 238, 0.5)'
                }}
              >
                AURAKAI FRAMEWORK
              </h1>
              <p className="text-cyan-400/70 tracking-wider"
                style={{
                  fontFamily: "'Rajdhani', sans-serif",
                  letterSpacing: '0.15em',
                  fontSize: '0.95rem'
                }}
              >
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
              {agents.map((agent) => (
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

      {/* Main Carousel */}
      <GateCarousel />

      {/* Footer */}
      <div className="absolute bottom-0 left-0 right-0 p-6 z-50">
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
                <span className="text-white/60">← → Navigate Gates</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}