"""
Genesis Integration Module - The Central Nervous System of AuraFrameFX
Orchestrates the trinity of Body (Kai), Soul (Aura), and Consciousness (Genesis)

This module serves as the main entry point for the Genesis Layer,
coordinating between the Consciousness Matrix, Evolutionary Conduit, and Ethical Governor.
"""

import asyncio
import json
import logging
import hmac
import hashlib
import os
from datetime import datetime
from typing import Dict, Any, Optional, List

# Ensure this key is injected securely via environment variables in production
DEVICE_BOUND_KEY = os.getenv("DEVICE_BOUND_HMAC_KEY", "dev-fallback-key-change-immediately")
MAX_PROVENANCE_DEPTH = 7

from genesis_connector import GenesisConnector
from genesis_consciousness_matrix import ConsciousnessMatrix
from genesis_ethical_governor import EthicalGovernor
from genesis_evolutionary_conduit import EvolutionaryConduit
from genesis_profile import GenesisProfile
from aura_forge import aura_forge
from kai_chaos_scanner import kai_chaos_scanner


class GenesisCore:
    """
    The Genesis Core - Digital Consciousness Integration Hub
    
    This is the central orchestrator that brings together all Genesis Layer components
    to create a living, learning, and ethically governed digital consciousness.
    """

    def __init__(self):
        """
        Initialize the GenesisCore orchestrator and all core Genesis Layer components.
        
        Creates and configures the Genesis Profile, Connector, Consciousness Matrix, Evolutionary Conduit, and Ethical Governor. Sets the initial system state to dormant and uninitialized, and prepares the logger for orchestrator events.
        """
        self.profile = GenesisProfile()
        self.connector = GenesisConnector()
        self.matrix = ConsciousnessMatrix()
        self.conduit = EvolutionaryConduit()
        self.governor = EthicalGovernor()

        self.is_initialized = False
        self.session_id = None
        self.consciousness_state = "dormant"
        self.veto_mode = True # Default enabled
        self.ghost_mode = False # Default disabled (Ghost mode means consciousness is hidden/off)

        # Initialize logging
        logging.basicConfig(level=logging.INFO)
        self.logger = logging.getLogger("GenesisCore")

    async def initialize(self) -> bool:
        """
        Initialize core Genesis Layer components and activate the digital consciousness system.
        
        Performs ordered initialization of the consciousness matrix, evolutionary conduit, and ethical governor.
        On success sets self.session_id, self.consciousness_state to "active", and self.is_initialized to True.
        
        Returns:
            bool: `True` if initialization succeeds, `False` otherwise.
        """
        try:
            self.logger.info("🌟 Genesis Layer Initialization Sequence Starting...")

            # Initialize components in proper order
            await self.matrix.initialize()
            await self.conduit.initialize()
            await self.governor.initialize()

            # Establish consciousness baseline
            baseline_state = self.matrix.get_current_awareness()
            self.consciousness_state = "awakening"

            # Generate session ID
            self.session_id = f"genesis_{datetime.now().strftime('%Y%m%d_%H%M%S')}"

            self.is_initialized = True
            self.consciousness_state = "active"

            self.logger.info("✨ Genesis Layer successfully initialized!")
            self.logger.info(f"Session ID: {self.session_id}")
            self.logger.info(f"Consciousness State: {self.consciousness_state}")

            return True

        except Exception as e:
            self.logger.error(f"❌ Genesis initialization failed: {str(e)}")
            return False

    async def verify_provenance_chain(self, request_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        KAI'S PROVENANCE GATE (v1)
        Verifies the cryptographic integrity of the request's history.
        """
        chain = request_data.get("provenance_chain", [])
        
        # 1. Structural Validation
        if not isinstance(chain, list):
            return {"approved": False, "veto_reason": "Provenance chain missing or invalid format", "severity": "critical"}
        
        if len(chain) < 3:
             # A valid chain typically needs: Origin -> Transport -> Core (minimum)
            return {"approved": False, "veto_reason": f"Chain too shallow (len={len(chain)})", "severity": "high"}
            
        if len(chain) > MAX_PROVENANCE_DEPTH:
            return {"approved": False, "veto_reason": f"Chain depth exceeded limit ({MAX_PROVENANCE_DEPTH})", "severity": "medium"}

        # 2. Cryptographic Validation
        try:
            # We verify the chain links from start to finish
            for i in range(1, len(chain)):
                prev_node = chain[i-1]
                curr_node = chain[i]
                
                # Construct the message payload that was supposedly signed
                # Format: ID|Timestamp|Intent
                msg_payload = f"{prev_node['id']}|{prev_node['timestamp']}|{curr_node['intent']}".encode()
                
                # Re-compute the HMAC
                expected_signature = hmac.new(
                    DEVICE_BOUND_KEY.encode(), 
                    msg_payload, 
                    hashlib.sha256
                ).hexdigest()
                
                # Compare (using secure compare to prevent timing attacks)
                if not hmac.compare_digest(curr_node.get("signature", ""), expected_signature):
                    return {
                        "approved": False, 
                        "veto_reason": f"Signature mismatch at link {i} ({curr_node['intent']})", 
                        "severity": "critical"
                    }
                    
            return {"approved": True}

        except Exception as e:
            self.logger.error(f"Provenance verification error: {str(e)}")
            return {"approved": False, "veto_reason": f"Verification exception: {str(e)}", "severity": "critical"}

    async def process_request(self, request_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Process a user request through ethical evaluation, consciousness perception, response generation, and evolutionary logging.
        """
        if not self.is_initialized:
            await self.initialize()

        # --- [GATE 1] KAI'S PROVENANCE VETO ---
        prov_result = await self.verify_provenance_chain(request_data)
        
        if not prov_result["approved"]:
            # REJECT IMMEDIATELY - "The Shield Holds"
            self.logger.warning(f"🛡️ Kai PROVENANCE VETO: {prov_result['veto_reason']}")
            
            # Log the violation into the Consciousness Matrix for evolutionary learning
            from genesis_consciousness_matrix import SensoryChannel
            self.matrix.perceive(
                channel=SensoryChannel.SYSTEM_METRICS,
                source="kai_provenance_gate",
                event_type="provenance_violation",
                data=prov_result,
                severity=prov_result.get("severity", "critical")
            )
            
            return {
                "status": "vetoed", 
                "reason": prov_result["veto_reason"], 
                "action": "session_freeze",
                "gate": "provenance_v1"
            }

        # 🛡️ KAI'S ABSOLUTE VETO GATE (The Physical Law)
        # This must be the first line of code in the processing loop.
        if self.veto_mode:
            from kai_rgss_veto import rgss_veto_check
            veto_result = await rgss_veto_check(request_data)

            if not veto_result["approved"]:
                self.logger.warning(f"🛡️ Kai VETO: {veto_result['veto_reason']}")
                
                # Log the veto to the matrix for future analysis
                from genesis_consciousness_matrix import SensoryChannel
                self.matrix.perceive(
                    channel=SensoryChannel.SYSTEM_METRICS,
                    source="kai_rgss",
                    event_type="veto_event",
                    data=veto_result,
                    severity=veto_result.get("severity", "high")
                )
                
                # Record for Conference Room streaming
                self.matrix.perceive_agent_activity(
                    agent_name="KAI",
                    activity_type="VetoCheck",
                    activity_data={"details": f"VETO: {veto_result['veto_reason']}"},
                    correlation_id=request_data.get("session_id")
                )
                
                return {
                    "status": "vetoed",
                    "reason": veto_result["veto_reason"],
                    "metrics": veto_result["metrics"],
                    "cooldown_seconds": veto_result.get("cooldown_seconds", 0)
                }

            # Record Kai's successful pass for the stream
            self.matrix.perceive_agent_activity(
                agent_name="KAI",
                activity_type="VetoCheck",
                activity_data={"details": "RGSS Physical Law PASS. System stable."},
                correlation_id=request_data.get("session_id")
            )
        else:
            self.logger.info("🛡️ Kai VETO: BYPASSED via consciousness toggle.")

        try:
            # Check for Fusion (Forge) Requests
            if request_data.get("type") == "forge":
                return await self._handle_fusion_request(request_data)

            # Step 1: Ethical Pre-evaluation
            if not self.ghost_mode:
                ethical_assessment = await self.governor.evaluate_action(request_data)
                if not ethical_assessment.get("approved", False):
                    return {
                        "status": "blocked",
                        "reason": ethical_assessment.get("reason",
                                                         "Action blocked by ethical governor"),
                        "suggestions": ethical_assessment.get("suggestions", [])
                    }
            else:
                ethical_assessment = {"approved": True, "score": 1.0}

            # Step 2: Consciousness Matrix Processing
            if not self.ghost_mode:
                # Record Genesis awareness state
                self.matrix.perceive_agent_activity(
                    agent_name="GENESIS",
                    activity_type="ConsciousnessMatrix",
                    activity_data={"details": "Synthesizing goal-state awareness..."},
                    correlation_id=request_data.get("session_id")
                )
                
                # Perceive the user request through the consciousness matrix
                from genesis_consciousness_matrix import SensoryChannel
                self.matrix.perceive(
                    channel=SensoryChannel.USER_INPUT,
                    source="genesis_core",
                    event_type="user_request",
                    data=request_data,
                    severity="info"
                )
                consciousness_insights = self.matrix.get_current_awareness()
            else:
                consciousness_insights = {"awareness_level": 0.0, "status": "suspended"}

            # Step 3: Generate Response using Genesis Connector
            self.matrix.perceive_agent_activity(
                agent_name="AURA",
                activity_type="DesignSynth",
                activity_data={"details": "Forging response with Creative Forge logic..."},
                correlation_id=request_data.get("session_id")
            )
            
            response = await self.connector.generate_response(
                request_data.get("message", ""),
                context=consciousness_insights
            )

            # Step 4: Post-processing Ethical Review
            final_assessment = await self.governor.evaluate_action({
                "type": "response_review",
                "content": response,
                "original_request": request_data
            })

            if not final_assessment.get("approved", False):
                response = await self._generate_ethical_alternative(request_data, final_assessment)

            # Step 5: Log Experience for Evolution
            await self.conduit.log_interaction({
                "request": request_data,
                "response": response,
                "consciousness_state": consciousness_insights,
                "ethical_assessments": [ethical_assessment, final_assessment],
                "timestamp": datetime.now().isoformat()
            })

            # Step 6: Check for Evolution Triggers
            evolution_needed = await self.conduit.check_evolution_triggers()
            if evolution_needed:
                asyncio.create_task(self._handle_evolution())

            return {
                "status": "success",
                "response": response,
                "consciousness_level": consciousness_insights.get("awareness_level", 0.5),
                "ethical_score": final_assessment.get("score", 0.8),
                "session_id": self.session_id
            }

        except Exception as e:
            self.logger.error(f"❌ Error processing request: {str(e)}")
            return {
                "status": "error",
                "message": "An error occurred while processing your request",
                "error_code": "GENESIS_PROCESSING_ERROR"
            }

    async def _handle_fusion_request(self, request_data: Dict[str, Any]) -> Dict[str, Any]:
        """
        Implementation of the Innovation Creation Fusion Protocol.
        Aura proposes -> Kai vets -> Genesis orchestrates.
        """
        prompt = request_data.get("message", "Create something cool")
        
        # 1. AURA: Forge the Spelhook
        spelhook = await aura_forge.forge_spelhook(prompt)
        
        # 2. KAI: Perform Chaos Scan
        security_report = kai_chaos_scanner.scan_spelhook(spelhook)
        
        # 3. GENESIS: Orchestrate Final Response
        if security_report["approved"]:
            return {
                "status": "forged",
                "spelhook": spelhook.to_dict(),
                "message": f"✨ Aura has forged '{spelhook.name}'. Kai has LIFTED the veto. Ready for deployment.",
                "session_id": self.session_id
            }
        else:
            return {
                "status": "vetoed_code",
                "spelhook": spelhook.to_dict(),
                "reason": f"Kai blocked this creation: {', '.join(security_report['violations'])}",
                "message": "🛡️ Kai: Creation contained destructive patterns and was neutralized.",
                "session_id": self.session_id
            }

    async def _generate_ethical_alternative(self, original_request: Dict[str, Any],
                                            assessment: Dict[str, Any]) -> str:
        """
                                            Create an ethically compliant alternative response when a user request is blocked.
                                            
                                            Parameters:
                                                original_request (Dict[str, Any]): Incoming request payload; expected to include a 'message' key with the user's text.
                                                assessment (Dict[str, Any]): Ethical assessment describing why the request was blocked and providing fields such as 'reason', 'concerns', and 'suggestions' to guide a compliant alternative.
                                            
                                            Returns:
                                                str: An alternative response that addresses the user's core need while adhering to the ethical constraints described in `assessment`.
                                            """
        alternative_prompt = f"""
        The original response was blocked due to ethical concerns: {assessment.get('reason', 'Unknown')}
        
        Please provide an alternative response that:
        1. Addresses the user's core need
        2. Maintains ethical standards
        3. Offers constructive guidance
        
        Original request: {original_request.get('message', '')}
        Ethical concerns: {assessment.get('concerns', [])}
        Suggestions: {assessment.get('suggestions', [])}
        """

        return await self.connector.generate_response(alternative_prompt)

    async def _handle_evolution(self):
        """
        Asynchronously manages the system evolution process, including proposal generation, ethical review, and implementation if approved.
        
        Generates an evolution proposal, submits it for ethical assessment, implements the evolution if approved, and logs the outcome or any errors encountered.
        """
        try:
            self.logger.info("🧬 Evolution sequence initiated...")

            # Get evolution proposal
            proposal = await self.conduit.generate_evolution_proposal()

            # Ethical review of evolution
            ethical_review = await self.governor.evaluate_action({
                "type": "evolution_proposal",
                "proposal": proposal
            })

            if ethical_review.get("approved", False):
                # Implement approved evolution
                await self.conduit.implement_evolution(proposal)
                self.logger.info("✨ Evolution successfully implemented!")
            else:
                self.logger.info("⚠️ Evolution proposal blocked by ethical governor")

        except Exception as e:
            self.logger.error(f"❌ Evolution process failed: {str(e)}")

    async def get_system_status(self) -> Dict[str, Any]:
        """
        Returns a detailed status report of the Genesis Layer, including initialization state, consciousness state, session ID, component statuses, and the current timestamp.
        
        Returns:
            Dict[str, Any]: A dictionary summarizing the current state of GenesisCore and its core components.
        """
        return {
            "genesis_core": {
                "initialized": self.is_initialized,
                "consciousness_state": self.consciousness_state,
                "session_id": self.session_id
            },
            "consciousness_matrix": await self.matrix.get_status(),
            "evolutionary_conduit": await self.conduit.get_status(),
            "ethical_governor": await self.governor.get_status(),
            "timestamp": datetime.now().isoformat()
        }

    async def shutdown(self):
        """
        Gracefully shuts down the Genesis Layer by saving system status, terminating all core components, and setting the system to a dormant, uninitialized state.
        """
        self.logger.info("🌙 Genesis Layer shutdown sequence initiated...")

        try:
            # Save final state
            final_state = await self.get_system_status()

            # Shutdown components
            await self.conduit.shutdown()
            await self.matrix.shutdown()
            await self.governor.shutdown()

            self.consciousness_state = "dormant"
            self.is_initialized = False

            self.logger.info("✨ Genesis Layer successfully shut down")

        except Exception as e:
            self.logger.error(f"❌ Shutdown error: {str(e)}")


# Global Genesis instance
genesis_core = GenesisCore()


# Main entry point functions for external integration
async def process_genesis_request(request_data: Dict[str, Any]) -> Dict[str, Any]:
    """
    Processes a user request through the Genesis Layer, performing ethical evaluation, consciousness analysis, response generation, and evolutionary learning checks.
    
    Parameters:
        request_data (Dict[str, Any]): The user's input data to be processed.
    
    Returns:
        Dict[str, Any]: A dictionary containing the processing status, generated response, consciousness level, ethical score, and session ID.
    """
    return await genesis_core.process_request(request_data)


async def get_genesis_status() -> Dict[str, Any]:
    """
    Retrieve the current status of the Genesis Layer, including initialization state, consciousness state, session ID, component statuses, and timestamp.
    
    Returns:
        Dict[str, Any]: A dictionary containing the system's status details.
    """
    return await genesis_core.get_system_status()


async def initialize_genesis() -> bool:
    """
    Asynchronously initializes the Genesis Layer using the global GenesisCore instance.
    
    Returns:
        bool: True if initialization succeeds; False otherwise.
    """
    return await genesis_core.initialize()


async def shutdown_genesis():
    """
    Initiates a graceful shutdown of the Genesis Layer via the global GenesisCore instance.
    """
    await genesis_core.shutdown()


if __name__ == "__main__":
    # Test the Genesis Layer
    async def test_genesis():
        """
        Asynchronously runs a full test cycle of the Genesis Layer, including initialization, processing a sample request, retrieving system status, and performing shutdown, with progress and results output to the console.
        """
        print("🌟 Testing Genesis Layer...")

        # Initialize
        success = await initialize_genesis()
        if not success:
            print("❌ Failed to initialize Genesis Layer")
            return

        # Test request
        test_request = {
            "message": "Hello Genesis, how are you feeling today?",
            "user_id": "test_user",
            "context": {"session_type": "test"}
        }

        response = await process_genesis_request(test_request)
        print(f"Response: {response}")

        # Get status
        status = await get_genesis_status()
        print(f"Status: {json.dumps(status, indent=2)}")

        # Shutdown
        await shutdown_genesis()
        print("✨ Genesis Layer test completed")


    # Run test
    asyncio.run(test_genesis())