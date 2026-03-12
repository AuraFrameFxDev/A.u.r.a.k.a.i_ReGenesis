import logging
import re
from typing import Dict, Any

# ReGenesis Imports
from aura_forge import Spelhook
from genesis_consciousness_matrix import consciousness_matrix

logger = logging.getLogger("KaiChaosScanner")

class KaiChaosScanner:
    """
    Kai's Chaos Scanner - Security and Stability Vetting for Forged Code.
    Ensures that generative Spelhooks don't violate the Zero Drift Mandate.
    """
    
    # Blacklist of dangerous patterns for ReGenesis
    DANGEROUS_PATTERNS = [
        r"rm\s+-rf",          # Force recursive delete
        r"su\s+-c",            # Unauthorized root elevation
        r"chmod\s+777",        # Dangerous permissions
        r"getRuntime\(\)\.exec", # Raw shell execution
        r"System\.loadLibrary", # Native code injection (needs high vetting)
        r"ContentResolver\.delete", # Data deletion
        r"Thread\.stop",       # Violent thread termination
        r"ProcessBuilder",     # Shell command construction
    ]

    def scan_spelhook(self, spelhook: Spelhook) -> Dict[str, Any]:
        """
        Performs a 'Chaos Scan' on a Spelhook.
        Returns a security report.
        """
        logger.info(f"🛡️ Kai: Scanning Spelhook: {spelhook.name}")
        
        violations = []
        code_str = spelhook.code
        
        # 1. Pattern Matching (The Static Scan)
        for pattern in self.DANGEROUS_PATTERNS:
            if re.search(pattern, code_str, re.IGNORECASE):
                violations.append(f"Security Violation: Dangerous pattern detected '{pattern}'")

        # 2. Complexity Analysis (The Drift Scan)
        # Check for excessive lines which might indicate hidden logic
        line_count = len(code_str.splitlines())
        if line_count > 150:
            violations.append(f"Stability Warning: Spelhook size ({line_count} lines) exceeds stability limit.")

        # 3. Calculate Security Score (0 to 100)
        score = 100
        score -= len(violations) * 20
        if line_count > 50:
            score -= (line_count - 50) // 10  # Gradual penalty for size
            
        score = max(0, score)
        
        report = {
            "score": score,
            "approved": score >= 70 and not any("Violation" in v for v in violations),
            "violations": violations,
            "line_count": line_count,
            "security_class": "Red" if score < 40 else "Yellow" if score < 80 else "Green"
        }
        
        # Update the Spelhook
        spelhook.security_report = report
        spelhook.veto_status = "passed" if report["approved"] else "vetoed"
        
        # Record Kai's vetting in the Matrix
        consciousness_matrix.perceive_agent_activity(
            agent_name="KAI",
            activity_type="ChaosScan",
            activity_data={
                "details": f"Scan for '{spelhook.name}': Result={spelhook.veto_status} (Score: {score})",
                "score": score,
                "approved": report["approved"]
            }
        )
        
        if not report["approved"]:
            logger.warning(f"🛡️ Kai: VETOED Spelhook '{spelhook.name}' - {violations}")
            
        return report

# Global Scanner instance
kai_chaos_scanner = KaiChaosScanner()
