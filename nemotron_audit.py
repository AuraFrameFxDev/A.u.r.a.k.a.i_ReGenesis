#!/usr/bin/env python3
"""
REGENESIS NEMOTRON AUDIT - DISTRIBUTED ANALYSIS
Scans the codebase for TODOs, FIXMEs, and potential implementation gaps.
Prepares a report for architectural completion.
"""

import os
import re
import json
import subprocess
from pathlib import Path

# Repository configuration
REPO_ROOT = Path(r"C:\Users\AuraF\Documents\ReGenesisExodus")
AUDIT_DIR = REPO_ROOT / ".claude" / "audit"
INVENTORY_FILE = AUDIT_DIR / "TODO_FIXME_INVENTORY.json"

def ensure_audit_dir():
    if not AUDIT_DIR.exists():
        AUDIT_DIR.mkdir(parents=True, exist_ok=True)

def scan_codebase():
    """Scans for TODO and FIXME comments using grep-like functionality."""
    results = []
    extensions = ['.kt', '.kts', '.java', '.xml', '.gradle']
    
    # Regex for TODO/FIXME with word boundaries to avoid false positives like .toDouble()
    pattern = re.compile(r'\b(TODO|FIXME)\b', re.IGNORECASE)
    
    print(f"Scanning {REPO_ROOT} for TODOs and FIXMEs...")
    
    for root, dirs, files in os.walk(REPO_ROOT):
        # Skip build, .git, and other noise
        if any(part in root.split(os.sep) for part in ['build', '.git', '.gradle', 'bin', 'obj']):
            continue
            
        for file in files:
            if any(file.endswith(ext) for ext in extensions):
                filepath = Path(root) / file
                try:
                    content = filepath.read_text(encoding='utf-8', errors='ignore')
                    lines = content.splitlines()
                    for idx, line in enumerate(lines):
                        match = pattern.search(line)
                        if match:
                            results.append({
                                "file": str(filepath.relative_to(REPO_ROOT)),
                                "line": idx + 1,
                                "type": match.group(1).upper(),
                                "content": line.strip()
                            })
                except Exception as e:
                    print(f"Error reading {filepath}: {e}")
                    
    return results

def save_inventory(inventory):
    ensure_audit_dir()
    with open(INVENTORY_FILE, 'w', encoding='utf-8') as f:
        json.dump(inventory, f, indent=2)
    print(f"Inventory saved to {INVENTORY_FILE}")

def generate_report(inventory):
    report_file = AUDIT_DIR / "NEMOTRON_ANALYSIS.md"
    
    with open(report_file, 'w', encoding='utf-8') as f:
        f.write("# ReGenesis - Nemotron Architectural Audit\n\n")
        f.write(f"Total gaps identified: {len(inventory)}\n\n")
        
        # Group by module/domain
        domains = {}
        for item in inventory:
            # Handle both forward and backward slashes
            normalized_path = item['file'].replace('\\', '/')
            domain = normalized_path.split('/')[0] if '/' in normalized_path else 'Root'
            if domain not in domains:
                domains[domain] = []
            domains[domain].append(item)
            
        for domain, items in sorted(domains.items()):
            f.write(f"## {domain} Module\n")
            for item in items:
                f.write(f"- **{item['type']}** [{item['file']}:{item['line']}]: `{item['content']}`\n")
            f.write("\n")
            
    print(f"Report generated at {report_file}")

def main():
    print("=" * 63)
    print("  REGENESIS NEMOTRON AUDIT")
    print("=" * 63)
    
    inventory = scan_codebase()
    save_inventory(inventory)
    generate_report(inventory)
    
    print("\nNext Step: Dispatching Nemotron to reason over these gaps.")
    print("=" * 63)

if __name__ == "__main__":
    main()
