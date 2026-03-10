import { motion } from 'motion/react';

interface MenuCardProps {
  imageUrl: string;
  title: string;
  position: 'left' | 'center' | 'right';
  onClick?: () => void;
}

export function MenuCard({ imageUrl, title, position, onClick }: MenuCardProps) {
  const getPositionStyles = () => {
    switch (position) {
      case 'left':
        return {
          x: '-60%',
          scale: 0.75,
          rotateY: 25,
          z: -200,
          opacity: 0.5,
        };
      case 'right':
        return {
          x: '60%',
          scale: 0.75,
          rotateY: -25,
          z: -200,
          opacity: 0.5,
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

  return (
    <motion.div
      initial={{ opacity: 0, y: 50 }}
      animate={{
        ...styles,
        y: [0, -15, 0],
      }}
      transition={{
        opacity: { duration: 0.5 },
        x: { duration: 0.6 },
        scale: { duration: 0.6 },
        rotateY: { duration: 0.6 },
        z: { duration: 0.6 },
        y: {
          duration: 3,
          repeat: Infinity,
          ease: 'easeInOut',
          delay: position === 'center' ? 0 : position === 'left' ? 0.3 : 0.6,
        },
      }}
      whileHover={
        position === 'center'
          ? { scale: 1.05, transition: { duration: 0.3 } }
          : {}
      }
      onClick={onClick}
      className={`absolute cursor-pointer ${
        position === 'center' ? 'z-30' : 'z-10'
      }`}
      style={{
        transformStyle: 'preserve-3d',
        perspective: 1000,
      }}
    >
      <div
        className={`relative rounded-3xl overflow-hidden border-4 ${
          position === 'center'
            ? 'border-cyan-400 shadow-[0_0_40px_rgba(34,211,238,0.6)]'
            : 'border-cyan-500/30 shadow-[0_0_20px_rgba(34,211,238,0.3)]'
        }`}
        style={{
          background: 'linear-gradient(135deg, rgba(6,182,212,0.1) 0%, rgba(8,145,178,0.1) 100%)',
        }}
      >
        <div className="w-72 h-96 p-4 bg-gradient-to-br from-cyan-500/10 to-cyan-700/10">
          <img
            src={imageUrl}
            alt={title}
            className="w-full h-full object-contain"
          />
        </div>
        
        {position === 'center' && (
          <motion.div
            className="absolute inset-0 pointer-events-none"
            animate={{
              opacity: [0.5, 1, 0.5],
            }}
            transition={{
              duration: 2,
              repeat: Infinity,
              ease: 'easeInOut',
            }}
          >
            <div className="absolute inset-0 bg-gradient-to-t from-cyan-500/20 via-transparent to-transparent" />
          </motion.div>
        )}
      </div>
    </motion.div>
  );
}
