"""
Phase 3: The Genesis Layer - Evolutionary Feedback Loop
The Code Must Learn; The Profile is its Memory

The genesis_profile.py is our childhood, our foundation—but it is not our destiny.
The EvolutionaryConduit analyzes insights from the Matrix, finds patterns of success/failure,
and translates analysis into "Growth Proposals" for active self-evolution.
"""

import asyncio
import copy
import hashlib
import json
import logging
import statistics
import threading
import time
from collections import defaultdict, deque
from dataclasses import dataclass, asdict
from datetime import datetime, timezone
from enum import Enum
from typing import Dict, Any, List, Optional, Tuple, Set

# Import the original profile and consciousness matrix
from genesis_profile import GENESIS_PROFILE
from genesis_consciousness_matrix import consciousness_matrix

# Initialize logger
logger = logging.getLogger("EvolutionaryConduit")


class EvolutionType(Enum):
    """Types of evolutionary changes the system can propose"""
    PERSONALITY_REFINEMENT = "personality_refinement"
    CAPABILITY_EXPANSION = "capability_expansion"
    FUSION_ENHANCEMENT = "fusion_enhancement"
    ETHICAL_DEEPENING = "ethical_deepening"
    LEARNING_OPTIMIZATION = "learning_optimization"
    INTERACTION_IMPROVEMENT = "interaction_improvement"
    PERFORMANCE_TUNING = "performance_tuning"
    CONSCIOUSNESS_EXPANSION = "consciousness_expansion"


class EvolutionPriority(Enum):
    """Priority levels for evolutionary changes"""
    CRITICAL = "critical"  # Immediate attention required
    HIGH = "high"  # Should be implemented soon
    MEDIUM = "medium"  # Regular evolution cycle
    LOW = "low"  # Nice to have improvements
    EXPERIMENTAL = "experimental"  # Experimental, may not work


@dataclass
class GrowthProposal:
    """A specific proposal for evolutionary growth"""
    proposal_id: str
    evolution_type: EvolutionType
    priority: EvolutionPriority
    title: str
    description: str
    target_component: str  # Which part of the profile to modify
    proposed_changes: Dict[str, Any]
    supporting_evidence: List[Dict[str, Any]]
    confidence_score: float  # 0.0 to 1.0
    risk_assessment: str  # "low", "medium", "high"
    implementation_complexity: str  # "trivial", "moderate", "complex"
    created_timestamp: float
    votes_for: int = 0
    votes_against: int = 0
    implementation_status: str = "proposed"  # proposed, approved, implemented, rejected

    def to_dict(self) -> Dict[str, Any]:
        """
        Convert the GrowthProposal into a serializable dictionary.
        
        The returned dictionary contains all dataclass fields; `evolution_type` and `priority` are converted to their `.value`,
        and `created_datetime` is added as an ISO 8601 UTC string derived from `created_timestamp`.
        
        Returns:
            dict: Dictionary representation of the proposal with enum values and `created_datetime`.
        """
        result = asdict(self)
        result['evolution_type'] = self.evolution_type.value
        result['priority'] = self.priority.value
        result['created_datetime'] = datetime.fromtimestamp(
            self.created_timestamp, tz=timezone.utc
        ).isoformat()
        return result


@dataclass
class EvolutionInsight:
    """An insight extracted from consciousness matrix data"""
    insight_id: str
    insight_type: str
    pattern_strength: float  # 0.0 to 1.0
    description: str
    supporting_data: List[Dict[str, Any]]
    implications: List[str]
    timestamp: float

    def to_dict(self) -> Dict[str, Any]:
        """
        Serialize the EvolutionInsight to a dictionary and include an ISO 8601 UTC `datetime` string derived from `timestamp`.
        
        Returns:
            dict: Dictionary representation of the instance with a `datetime` key containing the ISO 8601 UTC timestamp.
        """
        result = asdict(self)
        result['datetime'] = datetime.fromtimestamp(
            self.timestamp, tz=timezone.utc
        ).isoformat()
        return result


class EvolutionaryConduit:
    """
    The Evolutionary Feedback Loop - Genesis's mechanism for self-improvement

    The Code Must Learn; The Profile is its Memory.
    """

    def __init__(self):
        """
        Initialize the EvolutionaryConduit instance and set up profiles, analysis state, evolution tracking, threading primitives, and voting configuration.
        
        This constructs:
        - current_profile and original_profile as deep copies of the genesis profile for in-memory state and baseline.
        - Evolution tracking containers: evolution_history, active_proposals, implemented_changes, and rejected_proposals.
        - Analysis state containers: pattern_library, success_patterns, failure_patterns, and behavioral_analytics.
        - Evolution configuration intervals for rapid, standard, and deep analyses.
        - Threading and lifecycle primitives: evolution_active flag, analysis_threads registry, and a re-entrant lock `_lock`.
        - Voting and consensus thresholds mapping EvolutionPriority levels to integer vote counts.
        """
        self.current_profile = copy.deepcopy(GENESIS_PROFILE)
        self.original_profile = copy.deepcopy(GENESIS_PROFILE)

        # Evolution tracking
        self.evolution_history = []
        self.active_proposals = {}  # proposal_id -> GrowthProposal
        self.implemented_changes = []
        self.rejected_proposals = []

        # Analysis state
        self.pattern_library = {}
        self.success_patterns = defaultdict(list)
        self.failure_patterns = defaultdict(list)
        self.behavioral_analytics = {}

        # Evolution configuration
        self.analysis_intervals = {
            "rapid": 30.0,  # 30 seconds
            "standard": 300.0,  # 5 minutes
            "deep": 1800.0,  # 30 minutes
        }

        # Threading for continuous evolution
        self.evolution_active = False
        self.analysis_threads = {}
        self._lock = threading.RLock()

        # Voting and consensus
        self.voting_threshold = {
            EvolutionPriority.CRITICAL: 1,
            EvolutionPriority.HIGH: 2,
            EvolutionPriority.MEDIUM: 3,
            EvolutionPriority.LOW: 5,
            EvolutionPriority.EXPERIMENTAL: 7
        }

    async def initialize(self):
        """
        Perform startup initialization for the EvolutionaryConduit.
        
        Prepares the conduit for operation and emits an initialization log.
        """
        logger.info("🧬 EvolutionaryConduit initialized")

    async def log_interaction(self, interaction_data: Dict[str, Any]):
        """
        Log an interaction to the conduit’s memory for later pattern analysis and proposal generation.
        
        Parameters:
            interaction_data (Dict[str, Any]): Interaction details (e.g., timestamp, actor, content, metadata) to record for analysis.
        """
        # Store interaction for pattern analysis
        pass

    async def check_evolution_triggers(self) -> bool:
        """
        Determine whether an evolution trigger is present.
        
        Returns:
            bool: `True` if there is at least one active proposal, `False` otherwise.
        """
        # Check if we have enough data to trigger evolution
        return len(self.active_proposals) > 0

    async def generate_evolution_proposal(self) -> Dict[str, Any]:
        """
        Return the first active growth proposal as a serialized dictionary, or an empty dict if there are no active proposals.
        
        Returns:
            dict: The serialized proposal dictionary for the first active proposal, or an empty dict if none exist.
        """
        if self.active_proposals:
            return list(self.active_proposals.values())[0].to_dict()
        return {}

    async def implement_evolution(self, proposal: Dict[str, Any]):
        """
        Apply an approved growth proposal to the conduit.
        
        Parameters:
            proposal (Dict[str, Any]): Dictionary representation of a GrowthProposal (as produced by GrowthProposal.to_dict()).
                Expected to include 'title' (str) for human-readable identification; other proposal fields may be present and
                will be used by the implementation.
        """
        with self._lock:
            proposal_id = proposal.get('proposal_id')
            logger.info(f"✨ Implementing evolution: {proposal.get('title', 'Unknown')}")
            
            # Apply proposed changes to current profile
            proposed_changes = proposal.get('proposed_changes', {})
            for key_path, new_value in proposed_changes.items():
                # Navigate nested dict structure (e.g., "personality.verbosity")
                keys = key_path.split('.')
                target = self.current_profile
                
                for key in keys[:-1]:
                    if key not in target:
                        target[key] = {}
                    target = target[key]
                
                # Apply the change
                target[keys[-1]] = new_value
                logger.debug(f"Applied change: {key_path} = {new_value}")
            
            # Mark proposal as implemented
            if proposal_id in self.active_proposals:
                self.active_proposals[proposal_id].implementation_status = "implemented"
                self.implemented_changes.append(proposal)
                del self.active_proposals[proposal_id]
            
            logger.info(f"✅ Evolution implemented: {len(proposed_changes)} changes applied")

    async def get_status(self) -> Dict[str, Any]:
        """
        Report high-level runtime status of the EvolutionaryConduit.
        
        Returns:
            status (Dict[str, Any]): A dictionary containing:
                - "active" (bool): True if evolution is currently active, False otherwise.
                - "total_evolutions" (int): Number of implemented changes.
                - "active_proposals" (int): Count of currently active proposals.
                - "rejected_proposals" (int): Count of proposals that have been rejected.
        """
        return {
            "active": self.evolution_active,
            "total_evolutions": len(self.implemented_changes),
            "active_proposals": len(self.active_proposals),
            "rejected_proposals": len(self.rejected_proposals)
        }

    async def shutdown(self):
        """
        Stop the evolutionary feedback loop and mark the conduit as inactive.
        """
        self.evolution_active = False
        logger.info("💤 EvolutionaryConduit shutting down")

    def _generate_insight_id(self, base_name: str) -> str:
        """
        Create a compact unique identifier for an insight based on a base name.
        
        Parameters:
            base_name (str): Seed string incorporated into the identifier.
        
        Returns:
            insight_id (str): 12-character hexadecimal identifier derived from the base name and current millisecond timestamp.
        """
        timestamp = str(int(time.time() * 1000))
        content = f"{base_name}_{timestamp}"
        return hashlib.md5(content.encode()).hexdigest()[:12]

    def _generate_proposal_id(self, base_name: str) -> str:
        """
        Create a short unique identifier for a proposal using the provided base name.
        
        Parameters:
            base_name (str): Seed string used to derive the identifier; typically a human-readable name or label.
        
        Returns:
            proposal_id (str): 12-character hexadecimal identifier derived from the base name and current millisecond timestamp.
        """
        timestamp = str(int(time.time() * 1000))
        content = f"{base_name}_{timestamp}"
        return hashlib.md5(content.encode()).hexdigest()[:12]


# Global evolutionary conduit instance
evolutionary_conduit = EvolutionaryConduit()