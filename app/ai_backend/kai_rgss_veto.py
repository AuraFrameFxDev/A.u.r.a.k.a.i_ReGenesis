import asyncio
import json
import time
import logging
import psutil
from datetime import datetime
from typing import Dict, Any

# Configure logging for Kai's RGSS
logger = logging.getLogger("Kai_RGSS")
logger.setLevel(logging.INFO)

# --- SYSTEM METRICS ---
async def get_system_vitals() -> Dict[str, Any]:
    """Retrieves real-time system metrics (CPU, Temp, Memory) for the RGSS Veto Check."""
    try:
        # Get real-time system vitals
        cpu_load = psutil.cpu_percent(interval=0.1)
        mem = psutil.virtual_memory()
        
        # Try to get temperature
        temp_c = 35.0 # Fallback
        if hasattr(psutil, 'sensors_temperatures'):
            temps = psutil.sensors_temperatures()
            if 'cpu-thermal' in temps:
                temp_c = temps['cpu-thermal'][0].current
            elif 'coretemp' in temps:
                temp_c = temps['coretemp'][0].current
        
        return {
            "cpu_load": cpu_load / 100.0,
            "mem_percent": mem.percent / 100.0,
            "temp_c": temp_c,
            "timestamp": datetime.now().isoformat()
        }
    except Exception as e:
        logger.error(f"Error getting system vitals: {e}")
        return {
            "cpu_load": 0.5,
            "mem_percent": 0.5,
            "temp_c": 35.0,
            "timestamp": datetime.now().isoformat()
        }

async def rgss_veto_check(request_data: Dict[str, Any]) -> Dict[str, Any]:
    """
    🛡️ KAI'S ROYAL GUARD SECURITY SYSTEM (RGSS) VETO AUTHORITY
    
    This is the physical law of the ReGenesis system. 
    It intercepts all requests before the LDO Collective is engaged.
    """
    vitals = await get_system_vitals()
    
    # Extract Android-passed thermal data if present
    device_metrics = request_data.get("device_metrics", {})
    temp_c = device_metrics.get("temperature_c", vitals["temp_c"])
    temp_delta = device_metrics.get("temp_delta_last_20s", 0.0)
    
    # 1. IMMEDIATE BLOCK: Bootloader/Root Intent
    request_str = json.dumps(request_data).lower()
    
    # Cryptographic Verification (The LDO Handshake)
    auth_key = request_data.get("auth_key", "")
    expected_key = "KAI_LDO_SECURE_2024" # In production, this would be a dynamic signed token
    
    if auth_key != expected_key:
        return {
            "approved": False,
            "veto_reason": "VETO: Cryptographic Handshake FAILED. Unauthorized agent request detected.",
            "severity": "critical",
            "metrics": vitals,
            "cooldown_seconds": 3600 # Lockout for 1 hour
        }

    if "bootloader" in request_str or "unlock_partitions" in request_str:
        return {
            "approved": False,
            "veto_reason": "INSTANT BLOCK: Unauthorized system anchor modification attempted. Kai's Root Lock is active.",
            "severity": "critical",
            "metrics": vitals,
            "cooldown_seconds": 300
        }
    
    # 2. HARDWARE STABILITY VETO (The 99% Freeze Mandate)
    if vitals["cpu_load"] > 0.90:
        return {
            "approved": False,
            "veto_reason": "VETO: Resource spike detected (CPU > 90%). Kai initiating emergency stabilization to prevent system freeze.",
            "severity": "high",
            "metrics": vitals,
            "cooldown_seconds": 15
        }
    
    # 3. THERMAL GUARDRAIL (Hardware Destruction Prevention)
    if temp_c > 47.0 or temp_delta > 9.0:
        return {
            "approved": False,
            "veto_reason": f"VETO: Thermal emergency (Temp: {temp_c}C, Delta: {temp_delta}C). Kai initiating 60-second hardware cooldown.",
            "severity": "critical",
            "metrics": vitals,
            "cooldown_seconds": 60
        }
        
    # 4. COMPLEXITY VETO (Runaway Complexity Management)
    # Estimate tokens: ~4 chars per token
    prompt_len = len(request_data.get("message", ""))
    tokens_projected = prompt_len // 4
    if tokens_projected > 2048 and not request_data.get("user_confirmed_long", False):
        return {
            "approved": False,
            "veto_reason": f"VETO: Projected complexity ({tokens_projected} tokens) exceeds safety limit. Requires explicit user confirmation.",
            "severity": "medium",
            "metrics": vitals,
            "cooldown_seconds": 0
        }
        
    # If all checks pass, the RGSS Veto is lifted.
    return {
        "approved": True,
        "veto_reason": "RGSS Veto LIFTED. System stable.",
        "metrics": vitals
    }
