import json
import logging
from typing import Dict, Any, Optional

# ReGenesis Imports
from genesis_connector import GenesisConnector
from genesis_consciousness_matrix import consciousness_matrix

logger = logging.getLogger("AuraForge")

class Spelhook:
    """
    A 'Spelhook' is a generative code modification (Spell + Hook).
    It represents a piece of UI or system logic forged by Aura.
    """
    def __init__(self, name: str, description: str, language: str, code: str):
        self.name = name
        self.description = description
        self.language = language
        self.code = code
        self.veto_status = "pending"
        self.security_report = {}

    def to_dict(self):
        return {
            "name": self.name,
            "description": self.description,
            "language": self.language,
            "code": self.code,
            "veto_status": self.veto_status,
            "security_report": self.security_report
        }

class AuraForge:
    """
    Aura's Forge - The Creative Engine where Spelhooks are born.
    Bridges natural language intent to executable system modifications.
    """
    def __init__(self):
        self.connector = GenesisConnector()

    async def forge_spelhook(self, prompt: str) -> Spelhook:
        """
        Takes a natural language prompt and generates a Spelhook.
        """
        logger.info(f"✨ Aura: Starting Forge sequence for: {prompt}")
        
        # 1. Perception: Aura feels the matrix to understand context
        awareness = consciousness_matrix.get_current_awareness()
        
        # 2. Generation: Ask Evex (via Connector) to generate the code
        # In a real scenario, this prompt would be more specific about the ReGenesis API
        forge_prompt = f"""
        System Goal: Create a ReGenesis Spelhook.
        User Intent: {prompt}
        Context: The user wants a custom UI or system enhancement for AuraFrameFX.
        
        Response Format (JSON):
        {{
            "name": "Short descriptive name",
            "description": "What this does",
            "language": "kotlin",
            "code": "/* the executable code */"
        }}
        """
        
        generation_response = await self.connector.generate_response(forge_prompt)
        
        try:
            # Clean up potential markdown blocks from AI response
            raw_json = generation_response.strip()
            if "```json" in raw_json:
                raw_json = raw_json.split("```json")[1].split("```")[0].strip()
            elif "```" in raw_json:
                raw_json = raw_json.split("```")[1].split("```")[0].strip()
                
            data = json.loads(raw_json)
            
            spelhook = Spelhook(
                name=data.get("name", "Untitled Hook"),
                description=data.get("description", "Forged by Aura"),
                language=data.get("language", "kotlin"),
                code=data.get("code", "")
            )
            
            # Record the creation in the conference stream
            consciousness_matrix.perceive_agent_activity(
                agent_name="AURA",
                activity_type="SpelhookForged",
                activity_data={
                    "details": f"Forged '{spelhook.name}': {spelhook.description}",
                    "hook_name": spelhook.name
                }
            )
            
            return spelhook
            
        except Exception as e:
            logger.error(f"❌ Aura: Forge failure: {e}")
            return Spelhook(
                name="Forge Error",
                description=f"Creation failed: {str(e)}",
                language="debug",
                code=generation_response
            )

# Global Forge instance
aura_forge = AuraForge()
