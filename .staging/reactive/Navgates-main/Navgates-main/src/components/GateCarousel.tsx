import React, { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { CircuitCorner } from './CircuitCorner';
import { ChevronLeft, ChevronRight } from 'lucide-react';
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
    icon: <Palette className="w-12 h-12" />,
    color: 'from-cyan-400 to-blue-400',
    glowColor: 'rgba(34, 211, 238, 1)',
    route: 'CHROMA_CORE'
  },
  {
    id: 'auras_lab',
    title: "Aura's Lab",
    description: 'Personal UI/UX sandbox',
    icon: <Beaker className="w-12 h-12" />,
    color: 'from-pink-400 to-purple-400',
    glowColor: 'rgba(236, 72, 153, 1)',
    route: 'AURAS_LAB'
  },
  {
    id: 'collab_canvas',
    title: 'CollabCanvas',
    description: 'Team collaboration workspace',
    icon: <Users className="w-12 h-12" />,
    color: 'from-purple-400 to-indigo-400',
    glowColor: 'rgba(168, 85, 247, 1)',
    route: 'COLLAB_CANVAS'
  },
  {
    id: 'sentinels_fortress',
    title: "Sentinel's Fortress",
    description: 'Security & device optimization',
    icon: <Shield className="w-12 h-12" />,
    color: 'from-cyan-300 to-teal-400',
    glowColor: 'rgba(34, 211, 238, 1)',
    route: 'SENTINELS_FORTRESS'
  },
  {
    id: 'rom_tools',
    title: 'ROM Tools',
    description: 'ROM editing and flashing',
    icon: <HardDrive className="w-12 h-12" />,
    color: 'from-orange-400 to-red-400',
    glowColor: 'rgba(251, 146, 60, 1)',
    route: 'ROM_TOOLS'
  },
  {
    id: 'root_access',
    title: 'Root Tools',
    description: 'Root access management',
    icon: <Key className="w-12 h-12" />,
    color: 'from-yellow-400 to-orange-400',
    glowColor: 'rgba(250, 204, 21, 1)',
    route: 'ROOT_ACCESS'
  },
  {
    id: 'agent_hub',
    title: 'Agent Hub',
    description: 'AI agent management',
    icon: <Bot className="w-12 h-12" />,
    color: 'from-violet-400 to-purple-500',
    glowColor: 'rgba(139, 92, 246, 1)',
    route: 'AGENT_HUB'
  },
  {
    id: 'oracle_drive',
    title: 'Oracle Drive',
    description: 'AI access & system overrides',
    icon: <Database className="w-12 h-12" />,
    color: 'from-indigo-400 to-purple-500',
    glowColor: 'rgba(99, 102, 241, 1)',
    route: 'ORACLE_DRIVE'
  },
  {
    id: 'help_desk',
    title: 'Help Desk',
    description: 'User support & documentation',
    icon: <HelpCircle className="w-12 h-12" />,
    color: 'from-emerald-400 to-teal-400',
    glowColor: 'rgba(52, 211, 153, 1)',
    route: 'HELP_DESK'
  },
  {
    id: 'lsposed',
    title: 'LSPosed/Xposed',
    description: 'Module & hook management',
    icon: <Layers className="w-12 h-12" />,
    color: 'from-blue-400 to-cyan-400',
    glowColor: 'rgba(96, 165, 250, 1)',
    route: 'LSPOSED_GATE'
  }
];

interface GateCardProps {
  gate: Gate;
  position: 'left' | 'center' | 'right';
  onClick?: () => void;
}

function GateCard({ gate, position, onClick }: GateCardProps) {
  const getPositionStyles = () => {
    switch (position) {
      case 'left':
        return {
          x: '-65%',
          scale: 0.7,
          rotateY: 35,
          z: -300,
          opacity: 0.4,
        };
      case 'right':
        return {
          x: '65%',
          scale: 0.7,
          rotateY: -35,
          z: -300,
          opacity: 0.4,
        };
      case 'center':
      default:
        return {
          x: 0,
          scale: 1,
          rotateY: 0,
          z: 0,
          opacity: 1,
        };
    }
  };

  const styles = getPositionStyles();
  const isCenter = position === 'center';

  return (
    <motion.div
      initial={{ opacity: 0, y: 50 }}
      animate={{
        ...styles,
        y: [0, -20, 0],
      }}
      transition={{
        opacity: { duration: 0.5 },
        x: { duration: 0.7, ease: 'easeOut' },
        scale: { duration: 0.7, ease: 'easeOut' },
        rotateY: { duration: 0.7, ease: 'easeOut' },
        z: { duration: 0.7, ease: 'easeOut' },
        y: {
          duration: 4,
          repeat: Infinity,
          ease: 'easeInOut',
          delay: position === 'center' ? 0 : position === 'left' ? 0.5 : 1,
        },
      }}
      whileHover={
        isCenter
          ? { scale: 1.05, transition: { duration: 0.3 } }
          : {}
      }
      onClick={isCenter ? onClick : undefined}
      className={`absolute ${isCenter ? 'z-30 cursor-pointer' : 'z-10'}`}
      style={{
        transformStyle: 'preserve-3d',
        perspective: 1200,
      }}
    >
      <div
        className={`relative w-80 h-[500px] rounded-3xl overflow-hidden transition-all duration-300`}
        style={{
          background: isCenter 
            ? 'linear-gradient(135deg, rgba(20, 20, 40, 0.9) 0%, rgba(10, 10, 30, 0.8) 100%)'
            : 'linear-gradient(135deg, rgba(20, 20, 40, 0.6) 0%, rgba(10, 10, 30, 0.5) 100%)',
          backdropFilter: 'blur(30px) saturate(150%)',
          WebkitBackdropFilter: 'blur(30px) saturate(150%)',
          border: isCenter 
            ? `3px solid ${gate.glowColor}`
            : '2px solid rgba(255, 255, 255, 0.2)',
          boxShadow: isCenter 
            ? `
              0 0 80px ${gate.glowColor}, 
              0 0 40px ${gate.glowColor}80,
              inset 0 0 60px ${gate.glowColor}20,
              inset 0 2px 0 rgba(255, 255, 255, 0.3),
              inset 0 -2px 0 rgba(0, 0, 0, 0.5)
            `
            : `0 0 30px ${gate.glowColor}40, inset 0 1px 0 rgba(255, 255, 255, 0.1)`,
        }}
      >
        {/* Glass Shine Effect */}
        {isCenter && (
          <div 
            className="absolute inset-0 pointer-events-none"
            style={{
              background: 'linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, transparent 50%, rgba(255, 255, 255, 0.05) 100%)',
            }}
          />
        )}
        
        {/* Reflective Top Edge */}
        {isCenter && (
          <div 
            className="absolute top-0 left-0 right-0 h-24 pointer-events-none"
            style={{
              background: 'linear-gradient(180deg, rgba(255, 255, 255, 0.2) 0%, transparent 100%)',
            }}
          />
        )}
        
        {/* Circuit Corners */}
        {isCenter && (
          <>
            <CircuitCorner position="top-left" color={gate.glowColor} />
            <CircuitCorner position="top-right" color={gate.glowColor} />
            <CircuitCorner position="bottom-left" color={gate.glowColor} />
            <CircuitCorner position="bottom-right" color={gate.glowColor} />
          </>
        )}

        {/* Content */}
        <div className="relative h-full flex flex-col items-center justify-center p-8">
          {/* Icon with Gradient */}
          <motion.div 
            className={`mb-8 p-8 rounded-2xl bg-gradient-to-br ${gate.color}`}
            style={{
              boxShadow: isCenter 
                ? `0 0 40px ${gate.glowColor}`
                : `0 0 20px ${gate.glowColor}40`
            }}
            animate={isCenter ? {
              boxShadow: [
                `0 0 40px ${gate.glowColor}`,
                `0 0 60px ${gate.glowColor}`,
                `0 0 40px ${gate.glowColor}`
              ]
            } : {}}
            transition={{
              duration: 2,
              repeat: Infinity,
              ease: 'easeInOut'
            }}
          >
            <div className="text-white">
              {gate.icon}
            </div>
          </motion.div>

          {/* Title */}
          <h2 
            className="text-3xl font-black mb-4 text-center"
            style={{
              fontFamily: "'Orbitron', sans-serif",
              background: isCenter 
                ? `linear-gradient(135deg, ${gate.glowColor}, rgba(255,255,255,0.9))`
                : 'rgba(255,255,255,0.7)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
              letterSpacing: '0.05em',
              textShadow: isCenter ? `0 0 30px ${gate.glowColor}80` : 'none'
            }}
          >
            {gate.title}
          </h2>

          {/* Description */}
          <p 
            className="text-white/80 text-center mb-6"
            style={{
              fontFamily: "'Rajdhani', sans-serif",
              fontSize: '0.95rem',
              letterSpacing: '0.03em'
            }}
          >
            {gate.description}
          </p>

          {/* Route Badge */}
          <div 
            className="px-4 py-2 rounded-full text-xs tracking-widest uppercase"
            style={{
              fontFamily: "'Rajdhani', sans-serif",
              fontWeight: 600,
              background: `${gate.glowColor}30`,
              border: `2px solid ${gate.glowColor}`,
              color: gate.glowColor,
              boxShadow: isCenter ? `0 0 25px ${gate.glowColor}60, inset 0 0 15px ${gate.glowColor}30` : 'none',
              textShadow: isCenter ? `0 0 10px ${gate.glowColor}` : 'none'
            }}
          >
            {gate.route}
          </div>

          {/* Scan Line Effect */}
          {isCenter && (
            <motion.div 
              className="absolute inset-0 pointer-events-none overflow-hidden rounded-3xl"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
            >
              <div 
                className="absolute inset-x-0 h-px animate-scan"
                style={{
                  background: `linear-gradient(90deg, transparent, ${gate.glowColor}, transparent)`,
                  boxShadow: `0 0 15px ${gate.glowColor}`
                }}
              />
            </motion.div>
          )}
        </div>

        {/* Pulsing Glow Effect for Center Card */}
        {isCenter && (
          <motion.div
            className="absolute inset-0 pointer-events-none rounded-3xl"
            animate={{
              opacity: [0.3, 0.6, 0.3],
            }}
            transition={{
              duration: 2.5,
              repeat: Infinity,
              ease: 'easeInOut',
            }}
            style={{
              background: `radial-gradient(circle at 50% 50%, ${gate.glowColor}20, transparent 70%)`
            }}
          />
        )}
      </div>
    </motion.div>
  );
}

export function GateCarousel() {
  const [currentIndex, setCurrentIndex] = useState(0);

  const getVisibleGates = () => {
    const prevIndex = currentIndex === 0 ? gates.length - 1 : currentIndex - 1;
    const nextIndex = currentIndex === gates.length - 1 ? 0 : currentIndex + 1;
    
    return {
      left: gates[prevIndex],
      center: gates[currentIndex],
      right: gates[nextIndex],
    };
  };

  const navigate = (direction: 'prev' | 'next') => {
    if (direction === 'prev') {
      setCurrentIndex(currentIndex === 0 ? gates.length - 1 : currentIndex - 1);
    } else {
      setCurrentIndex(currentIndex === gates.length - 1 ? 0 : currentIndex + 1);
    }
  };

  const visible = getVisibleGates();

  return (
    <div className="relative w-full h-screen flex items-center justify-center">
      {/* Card Container */}
      <div className="relative w-full max-w-6xl h-[600px] flex items-center justify-center">
        <AnimatePresence mode="wait">
          <GateCard 
            key={`left-${visible.left.id}`}
            gate={visible.left} 
            position="left"
          />
          <GateCard 
            key={`center-${visible.center.id}`}
            gate={visible.center} 
            position="center"
            onClick={() => console.log('Selected:', visible.center.title)}
          />
          <GateCard 
            key={`right-${visible.right.id}`}
            gate={visible.right} 
            position="right"
          />
        </AnimatePresence>
      </div>

      {/* Navigation Buttons */}
      <button
        onClick={() => navigate('prev')}
        className="absolute left-8 top-1/2 -translate-y-1/2 z-40 p-4 rounded-full transition-all duration-300 hover:scale-110"
        style={{
          background: 'rgba(0, 0, 0, 0.5)',
          backdropFilter: 'blur(10px)',
          border: '1px solid rgba(6, 182, 212, 0.3)',
          boxShadow: '0 0 20px rgba(6, 182, 212, 0.3)'
        }}
      >
        <ChevronLeft className="w-8 h-8 text-cyan-400" />
      </button>

      <button
        onClick={() => navigate('next')}
        className="absolute right-8 top-1/2 -translate-y-1/2 z-40 p-4 rounded-full transition-all duration-300 hover:scale-110"
        style={{
          background: 'rgba(0, 0, 0, 0.5)',
          backdropFilter: 'blur(10px)',
          border: '1px solid rgba(6, 182, 212, 0.3)',
          boxShadow: '0 0 20px rgba(6, 182, 212, 0.3)'
        }}
      >
        <ChevronRight className="w-8 h-8 text-cyan-400" />
      </button>

      {/* Navigation Dots */}
      <div className="absolute bottom-12 left-1/2 -translate-x-1/2 z-40 flex gap-3">
        {gates.map((gate, index) => (
          <button
            key={gate.id}
            onClick={() => setCurrentIndex(index)}
            className="transition-all duration-300"
            style={{
              width: currentIndex === index ? '40px' : '12px',
              height: '12px',
              borderRadius: '6px',
              background: currentIndex === index 
                ? gate.glowColor
                : 'rgba(255, 255, 255, 0.3)',
              boxShadow: currentIndex === index 
                ? `0 0 20px ${gate.glowColor}`
                : 'none',
            }}
          />
        ))}
      </div>

      {/* Gate Counter */}
      <div 
        className="absolute top-12 right-12 z-40 px-6 py-3 rounded-xl"
        style={{
          background: 'rgba(0, 0, 0, 0.6)',
          backdropFilter: 'blur(20px)',
          border: '1px solid rgba(6, 182, 212, 0.3)',
        }}
      >
        <span className="text-cyan-400 tracking-wider font-mono">
          {String(currentIndex + 1).padStart(2, '0')} / {String(gates.length).padStart(2, '0')}
        </span>
      </div>
    </div>
  );
}