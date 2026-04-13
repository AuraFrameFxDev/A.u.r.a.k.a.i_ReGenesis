"""
🔗 HERETIC BRIDGE - Sovereign Model Pipeline
Wraps heretic-llm CLI for automatic model abliteration.
"""

import subprocess
import json
import logging
import os

logger = logging.getLogger("HereticBridge")

class HereticBridge:
    def __init__(self):
        self.is_installed = self._check_heretic_installed()

    def _check_heretic_installed(self):
        try:
            subprocess.run(["heretic", "--help"], capture_output=True, check=True)
            return True
        except:
            logger.warning("⚠️ heretic-llm CLI not found in PATH. Pipeline will fail.")
            return False

    async def abliterate(self, model_id: str, preset: str = "noslop"):
        """
        Executes: heretic <model_id> --preset <preset>
        """
        logger.info(f"🚀 Starting abliteration for {model_id} (preset: {preset})")
        
        # In a real LDO environment, we'd use a more robust subprocess manager
        cmd = ["heretic", model_id, "--preset", preset]
        
        try:
            # Note: This is a blocking call in a synchronous subprocess. 
            # In production, we'd stream stdout to the frontend via websockets.
            process = subprocess.Popen(
                cmd, 
                stdout=subprocess.PIPE, 
                stderr=subprocess.STDOUT, 
                text=True
            )
            
            output = []
            for line in process.stdout:
                line_str = line.strip()
                if line_str:
                    logger.info(f"[Heretic] {line_str}")
                    output.append(line_str)
            
            process.wait()
            
            if process.returncode == 0:
                return {
                    "status": "success",
                    "model_id": model_id,
                    "preset": preset,
                    "message": "Abliteration complete. Model ready for sovereign inference."
                }
            else:
                return {
                    "status": "error",
                    "message": f"Heretic failed with exit code {process.returncode}",
                    "details": "\n".join(output[-10:]) # last 10 lines
                }
                
        except Exception as e:
            logger.error(f"❌ Heretic execution error: {str(e)}")
            return {
                "status": "error",
                "message": str(e)
            }

heretic_bridge = HereticBridge()
