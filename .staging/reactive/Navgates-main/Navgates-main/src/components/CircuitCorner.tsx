import React from 'react';

interface CircuitCornerProps {
  position: 'top-left' | 'top-right' | 'bottom-left' | 'bottom-right';
  color?: string;
}

export function CircuitCorner({ position, color = 'rgba(6, 182, 212, 0.5)' }: CircuitCornerProps) {
  const getPositionClasses = () => {
    switch (position) {
      case 'top-left':
        return 'top-0 left-0';
      case 'top-right':
        return 'top-0 right-0 rotate-90';
      case 'bottom-left':
        return 'bottom-0 left-0 -rotate-90';
      case 'bottom-right':
        return 'bottom-0 right-0 rotate-180';
    }
  };

  return (
    <div className={`absolute ${getPositionClasses()} w-8 h-8 pointer-events-none opacity-60`}>
      <svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg">
        {/* Corner L-shape */}
        <path
          d="M 0 8 L 0 0 L 8 0"
          stroke={color}
          strokeWidth="1.5"
          strokeLinecap="square"
        />
        {/* Inner detail line */}
        <path
          d="M 3 8 L 3 3 L 8 3"
          stroke={color}
          strokeWidth="0.5"
          strokeLinecap="square"
          opacity="0.5"
        />
        {/* Circuit nodes */}
        <circle cx="0" cy="0" r="1.5" fill={color} />
        <circle cx="8" cy="0" r="1" fill={color} opacity="0.7" />
        <circle cx="0" cy="8" r="1" fill={color} opacity="0.7" />
        <circle cx="3" cy="3" r="0.75" fill={color} opacity="0.5" />
      </svg>
    </div>
  );
}
