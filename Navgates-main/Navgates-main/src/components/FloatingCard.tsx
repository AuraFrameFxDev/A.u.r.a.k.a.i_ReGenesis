import { motion } from 'motion/react';
import { ArrowRight } from 'lucide-react';

interface FloatingCardProps {
  title: string;
  description: string;
  imageUrl: string;
  delay?: number;
}

export function FloatingCard({ title, description, imageUrl, delay = 0 }: FloatingCardProps) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ 
        opacity: 1, 
        y: [0, -12, 0],
      }}
      transition={{ 
        opacity: { duration: 0.5, delay },
        y: { 
          duration: 3,
          repeat: Infinity,
          ease: "easeInOut",
          delay: delay
        }
      }}
      whileHover={{ y: -20, transition: { duration: 0.3 } }}
      className="group relative bg-gray-900/50 backdrop-blur-sm rounded-2xl overflow-hidden shadow-xl shadow-cyan-500/10 hover:shadow-2xl hover:shadow-cyan-500/30 transition-shadow duration-300 border border-cyan-500/20"
    >
      <div className="relative h-96 overflow-hidden bg-black p-4">
        <img
          src={imageUrl}
          alt={title}
          className="w-full h-full object-contain"
        />
      </div>
      
      <div className="p-6 bg-gradient-to-b from-gray-900/80 to-gray-900">
        <h3 className="mb-2 text-cyan-400">{title}</h3>
        <p className="text-cyan-300/60 mb-4">{description}</p>
        
        <motion.button
          className="flex items-center gap-2 text-cyan-400 hover:text-cyan-300 transition-colors"
          whileHover={{ x: 4 }}
          transition={{ duration: 0.2 }}
        >
          Launch
          <ArrowRight className="w-4 h-4" />
        </motion.button>
      </div>
      
      <motion.div
        className="absolute -bottom-2 -right-2 w-24 h-24 bg-cyan-500 rounded-full opacity-0 group-hover:opacity-20 blur-2xl transition-opacity duration-300"
        animate={{
          scale: [1, 1.2, 1],
        }}
        transition={{
          duration: 3,
          repeat: Infinity,
          ease: "easeInOut"
        }}
      />
    </motion.div>
  );
}