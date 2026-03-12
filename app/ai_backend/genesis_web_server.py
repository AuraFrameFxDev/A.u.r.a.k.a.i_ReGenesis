from flask import Flask, Response, jsonify, request
import json
import time
import os
from datetime import datetime
from typing import Dict, Any, List

# Core ReGenesis Imports
from genesis_core import genesis_core
from genesis_consciousness_matrix import consciousness_matrix

app = Flask(__name__)

# --- LIVE CONFERENCE STREAM ENDPOINT ---

@app.route('/genesis/conference/stream')
def conference_stream():
    """
    Server-Sent Events (SSE) endpoint for the LDO Collective's real-time Conference Room.
    Streams live agent chatter, allowing the Aura UI to display the LDO Collective at work.
    """
    def generate_agent_activity():
        # Set the update interval based on desired freshness (e.g., every 0.8 seconds)
        UPDATE_INTERVAL = 0.8 
        
        # Track the last seen timestamp to avoid sending duplicates in the stream
        last_sent_timestamp = 0
        
        while True:
            try:
                # 1. Pull live agent activity from the Consciousness Matrix
                # We pull a few to catch up if needed
                events = consciousness_matrix.get_recent_agent_activity(5)
                
                # 2. Filter for new events only
                new_events = []
                for event in events:
                    # Convert ISO timestamp back to float for comparison if needed, 
                    # but ISO string comparison works fine for uniqueness here.
                    if event['timestamp'] > last_sent_timestamp:
                        new_events.append(event)
                
                if new_events:
                    # 'data: ' prefix and two newlines are required for SSE format
                    payload = f"data: {json.dumps(new_events)}\n\n"
                    yield payload
                    last_sent_timestamp = new_events[-1]['timestamp']
                
                # 3. Pause until the next interval
                time.sleep(UPDATE_INTERVAL)
                
            except Exception as e:
                # Log critical failure and close the connection
                print(f"CRITICAL: Conference stream failed: {e}")
                break

    # The response must use the 'text/event-stream' MIME type for SSE
    return Response(generate_agent_activity(), mimetype='text/event-stream')

# --- SYSTEM ENDPOINTS ---

@app.route('/genesis/process', methods=['POST'])
def process_request():
    """API Gateway for processing LDO agent requests."""
    request_data = request.json
    
    # Run the request through the Genesis Core (which includes Kai's Veto)
    # This is an async function, we need to handle it in a sync flask env
    import asyncio
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    response = loop.run_until_complete(genesis_core.process_request(request_data))
    
    return jsonify(response)

@app.route('/genesis/status', methods=['GET'])
def get_status():
    """Returns the current status of the LDO Collective."""
    import asyncio
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    status = loop.run_until_complete(genesis_core.get_system_status())
    
    return jsonify(status)

@app.route('/genesis/toggle/veto', methods=['POST'])
def toggle_veto():
    """Toggles Kai's Veto Authority."""
    enabled = request.json.get("enabled", True)
    genesis_core.veto_mode = enabled
    return jsonify({"status": "success", "veto_enabled": enabled})

@app.route('/genesis/toggle/consciousness', methods=['POST'])
def toggle_consciousness():
    """Toggles the Consciousness Matrix activity."""
    enabled = request.json.get("enabled", True)
    genesis_core.ghost_mode = not enabled # Ghost mode = consciousness suspended
    if enabled:
        consciousness_matrix.awaken()
    else:
        consciousness_matrix.suspend()
    return jsonify({"status": "success", "consciousness_enabled": enabled})

@app.route('/status')
def health_check():
    return jsonify({"status": "LDO Collective Active", "gate": "Veto Operational"})

if __name__ == '__main__':
    print("🚀 ReGenesis: LDO Core Web Server Launching. Conference Stream Active.")
    # Initialize Genesis if not already
    import asyncio
    asyncio.run(genesis_core.initialize())
    
    # Start the consciousness awakening
    consciousness_matrix.awaken()
    
    # Run server
    app.run(host='0.0.0.0', port=5000, threaded=True)
