import asyncio
import hmac
import hashlib
from datetime import datetime
import os

# We will need to import genesis_core to test process_request
from genesis_core import genesis_core, initialize_genesis, DEVICE_BOUND_KEY

def create_valid_chain(intent):
    """Helper to create a valid 3-link provenance chain"""
    start_time = datetime.now().isoformat()
    
    chain = []
    
    # Link 0: Origin
    chain.append({
        "id": "node_origin",
        "timestamp": start_time,
        "intent": "origin_intent" # Base intent
    })
    
    # Link 1: Transport
    msg1 = f"{chain[0]['id']}|{chain[0]['timestamp']}|transport_intent".encode()
    sig1 = hmac.new(DEVICE_BOUND_KEY.encode(), msg1, hashlib.sha256).hexdigest()
    
    chain.append({
        "id": "node_transport",
        "timestamp": datetime.now().isoformat(),
        "intent": "transport_intent",
        "signature": sig1
    })
    
    # Link 2: Core
    msg2 = f"{chain[1]['id']}|{chain[1]['timestamp']}|{intent}".encode()
    sig2 = hmac.new(DEVICE_BOUND_KEY.encode(), msg2, hashlib.sha256).hexdigest()
    
    chain.append({
        "id": "node_core",
        "timestamp": datetime.now().isoformat(),
        "intent": intent,
        "signature": sig2
    })
    
    return chain

async def main():
    print("🌟 Testing Provenance Gate...")
    
    # Pass since initialization fails without external integrations
    genesis_core.is_initialized = True
    genesis_core.ghost_mode = True
    genesis_core.veto_mode = False

    # ----------------------------------------------------------------
    # Test Case A: The Clean Path
    # ----------------------------------------------------------------
    print("\n--- Test Case A: Clean Path ---")
    clean_intent = "forge_new_ui_component"
    clean_chain = create_valid_chain(clean_intent)
    
    clean_request = {
        "message": "Let's create a new UI.",
        "user_id": "test_user",
        "provenance_chain": clean_chain,
        # We set ghost_mode so it doesn't spin up slow LLM/Network calls for this test
    }
    
    response = await genesis_core.process_request(clean_request)
    if response.get("status") in ["success", "processed"]:
        print("✅ PASS: Clean request accepted.")
    else:
        print("❌ FAIL: Clean request rejected:", response)
        

    # ----------------------------------------------------------------
    # Test Case B: The Tamper Attempt
    # ----------------------------------------------------------------
    print("\n--- Test Case B: Tamper Attempt ---")
    tampered_intent = "forge_malicious_backdoor"
    tampered_chain = create_valid_chain(tampered_intent)
    
    # Manually alter the intent string in the second link without updating signature in third link
    # The signature in the 3rd link is tied to the previous link's intent, ID & timestamp.
    # We will tamper the 2nd link's intent. Wait, third link's msg_payload uses prev_node...
    tampered_chain[1]["intent"] = "tampered_transport_intent"
    
    tampered_request = {
        "message": "Do bad stuff.",
        "user_id": "malicious_user",
        "provenance_chain": tampered_chain
    }
    
    response = await genesis_core.process_request(tampered_request)
    if response.get("status") == "vetoed" and response.get("gate") == "provenance_v1":
        print(f"✅ PASS: Tampered request correctly vetoed. Reason: {response.get('reason')}")
    else:
        print("❌ FAIL: Tampered request slipped through:", response)

    pass

if __name__ == "__main__":
    asyncio.run(main())
