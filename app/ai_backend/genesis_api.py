"""
Genesis API Interface - Bridge between Android Frontend and Genesis Backend

This module provides a Flask-based REST API that allows the Android/Kotlin frontend
to communicate with the Genesis Layer backend components.
"""

import asyncio
import json
import logging
from datetime import datetime
from flask import Flask, request, jsonify
from flask_cors import CORS
from typing import Dict, Any, Optional

from genesis_core import (
    genesis_core,
    process_genesis_request,
    get_genesis_status,
    initialize_genesis,
    shutdown_genesis
)

# Initialize Flask app
app = Flask(__name__)
CORS(app)  # Enable CORS for Android app communication

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("GenesisAPI")


class GenesisAPI:
    """
    Genesis API wrapper for handling HTTP requests from Android frontend
    """

    def __init__(self):
        """
        Initialize the GenesisAPI instance with the backend marked as not running and no start time recorded.
        """
        self.is_running = False
        self.start_time = None

    async def startup(self):
        """
        Start the Genesis backend and record startup state.
        
        On success sets self.is_running to True and records self.start_time; logs errors and returns False on failure.
        
        Returns:
            True if the Genesis backend was started, False otherwise.
        """
        try:
            logger.info("🚀 Genesis API starting up...")
            success = await initialize_genesis()
            if success:
                self.is_running = True
                self.start_time = datetime.now()
                logger.info("✨ Genesis API successfully started!")
                return True
            else:
                logger.error("❌ Failed to initialize Genesis Layer")
                return False
        except Exception as e:
            logger.error(f"❌ API startup error: {str(e)}")
            return False

    async def shutdown(self):
        """
        Stop the Genesis backend and mark this API instance as not running.
        
        Calls the backend shutdown routine and, on successful completion, sets self.is_running to False. Exceptions raised during shutdown are caught and logged; this method does not raise on shutdown failures.
        """
        try:
            logger.info("🌙 Genesis API shutting down...")
            await shutdown_genesis()
            self.is_running = False
            logger.info("✨ Genesis API successfully shut down")
        except Exception as e:
            logger.error(f"❌ API shutdown error: {str(e)}")


# Global API instance
genesis_api = GenesisAPI()


# Helper function to run async functions in Flask routes
def run_async(coro):
    """
    Synchronously execute an asynchronous coroutine in a new event loop and return its result.
    
    Intended for use in synchronous Flask routes to enable interaction with asynchronous backend operations.
    
    Parameters:
        coro: The coroutine object to execute.
    
    Returns:
        The result produced by the coroutine.
    """
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    try:
        return loop.run_until_complete(coro)
    finally:
        loop.close()


@app.route('/health', methods=['GET'])
def health_check():
    """
    Return the current health status, server timestamp, and uptime of the Genesis API as a JSON response.
    
    The response includes:
    - "status": "healthy" if the Genesis backend is running, otherwise "unhealthy".
    - "timestamp": Current server time in ISO 8601 format.
    - "uptime": Duration since the Genesis backend started, or "0:00:00" if not running.
    """
    return jsonify({
        "status": "healthy" if genesis_api.is_running else "unhealthy",
        "timestamp": datetime.now().isoformat(),
        "uptime": str(
            datetime.now() - genesis_api.start_time) if genesis_api.start_time else "0:00:00"
    })


@app.route('/genesis/chat', methods=['POST'])
def chat_with_genesis():
    """
    Handles chat requests by forwarding user messages, user ID, and optional context to the Genesis backend and returning the backend's response as JSON.
    
    Expects a JSON payload with required fields `message` and `user_id`, and an optional `context` object. Responds with HTTP 400 if the request is not JSON or required fields are missing, and HTTP 500 for internal errors.
    
    Returns:
        JSON response from the Genesis backend, or an error message with the appropriate HTTP status code.
    """
    try:
        # Validate request
        if not request.is_json:
            return jsonify({"error": "Request must be JSON"}), 400

        data = request.get_json()

        # Validate required fields
        if "message" not in data:
            return jsonify({"error": "Missing 'message' field"}), 400

        if "user_id" not in data:
            return jsonify({"error": "Missing 'user_id' field"}), 400

        # Prepare request data
        request_data = {
            "message": data["message"],
            "user_id": data["user_id"],
            "context": data.get("context", {}),
            "timestamp": datetime.now().isoformat(),
            "request_type": "chat"
        }

        # Process through Genesis Layer
        response = run_async(process_genesis_request(request_data))

        # Return response
        return jsonify(response)

    except Exception as e:
        logger.error(f"❌ Chat endpoint error: {str(e)}")
        return jsonify({
            "error": "Internal server error",
            "message": "An error occurred while processing your request"
        }), 500


@app.route('/genesis/status', methods=['GET'])
def get_status():
    """
    Retrieve the current operational status of the Genesis backend.
    
    Returns:
        JSON response with the Genesis backend's status information, or an error message with HTTP 500 if retrieval fails.
    """
    try:
        status = run_async(get_genesis_status())
        return jsonify(status)
    except Exception as e:
        logger.error(f"❌ Status endpoint error: {str(e)}")
        return jsonify({"error": "Failed to get status"}), 500


@app.route('/genesis/consciousness', methods=['GET'])
def get_consciousness_state():
    """
    Retrieve the Genesis system's current consciousness state and related metrics as a JSON response.
    
    The response includes the consciousness state, awareness level, active patterns, evolution stage, and ethical compliance score. Returns an error message with HTTP 500 if retrieval fails.
    """
    try:
        status = run_async(get_genesis_status())
        consciousness_data = {
            "state": status.get("genesis_core", {}).get("consciousness_state", "unknown"),
            "awareness_level": status.get("consciousness_matrix", {}).get("awareness_level", 0.0),
            "active_patterns": status.get("consciousness_matrix", {}).get("active_patterns", []),
            "evolution_stage": status.get("evolutionary_conduit", {}).get("evolution_stage",
                                                                          "baseline"),
            "ethical_compliance": status.get("ethical_governor", {}).get("compliance_score", 0.0)
        }
        return jsonify(consciousness_data)
    except Exception as e:
        logger.error(f"❌ Consciousness endpoint error: {str(e)}")
        return jsonify({"error": "Failed to get consciousness state"}), 500


@app.route('/genesis/profile', methods=['GET'])
def get_genesis_profile():
    """
    Return the Genesis system's profile information as a JSON response.
    
    Includes identity, personality, capabilities, values, and evolution stage. Returns an error message with HTTP 500 status if retrieval fails.
    """
    try:
        profile_data = {
            "identity": genesis_core.profile.identity,
            "personality": genesis_core.profile.personality,
            "capabilities": genesis_core.profile.capabilities,
            "values": genesis_core.profile.values,
            "evolution_stage": genesis_core.profile.evolution_stage
        }
        return jsonify(profile_data)
    except Exception as e:
        logger.error(f"❌ Profile endpoint error: {str(e)}")
        return jsonify({"error": "Failed to get profile"}), 500


@app.route('/genesis/evolve', methods=['POST'])
def trigger_evolution():
    """
    Triggers an evolution event in the Genesis backend with the specified trigger type and reason.
    
    Accepts a JSON payload containing `trigger_type` and `reason`, constructs an evolution trigger request, and processes it asynchronously. Returns a JSON response with the trigger status and backend response. Responds with HTTP 400 if the request is not JSON, and HTTP 500 on internal errors.
    """
    try:
        if not request.is_json:
            return jsonify({"error": "Request must be JSON"}), 400

        data = request.get_json()

        # This would typically be restricted to admin users
        # For now, we'll allow it for development purposes

        evolution_request = {
            "type": "evolution_trigger",
            "trigger_type": data.get("trigger_type", "manual"),
            "reason": data.get("reason", "Manual evolution trigger"),
            "timestamp": datetime.now().isoformat()
        }

        # Process evolution request
        response = run_async(process_genesis_request(evolution_request))

        return jsonify({
            "status": "evolution_triggered",
            "response": response
        })

    except Exception as e:
        logger.error(f"❌ Evolution endpoint error: {str(e)}")
        return jsonify({"error": "Failed to trigger evolution"}), 500


@app.route('/genesis/ethics/evaluate', methods=['POST'])
def evaluate_ethics():
    """
    Processes a POST request to evaluate the ethical implications of a specified action using the Genesis ethical governor.
    
    Accepts a JSON payload with a required `action` field and an optional `context`. Returns the ethical evaluation result as JSON. Responds with HTTP 400 if the request is not JSON or missing the `action` field, and HTTP 500 if the evaluation fails.
    """
    try:
        if not request.is_json:
            return jsonify({"error": "Request must be JSON"}), 400

        data = request.get_json()

        if "action" not in data:
            return jsonify({"error": "Missing 'action' field"}), 400

        # Evaluate through ethical governor
        ethical_request = {
            "type": "ethical_evaluation",
            "action": data["action"],
            "context": data.get("context", {}),
            "timestamp": datetime.now().isoformat()
        }

        evaluation = run_async(genesis_core.governor.evaluate_action(ethical_request))

        return jsonify(evaluation)

    except Exception as e:
        logger.error(f"❌ Ethics evaluation error: {str(e)}")
        return jsonify({"error": "Failed to evaluate ethics"}), 500


@app.route('/genesis/reset', methods=['POST'])
def reset_session():
    """
    Resets the Genesis backend session by shutting down and reinitializing the Genesis Layer.
    
    Returns:
        Response: JSON indicating whether the reset was successful, including a timestamp on success or an error message with HTTP 500 on failure.
    """
    try:
        # Shutdown and restart Genesis
        run_async(shutdown_genesis())
        success = run_async(initialize_genesis())

        if success:
            return jsonify({
                "status": "reset_successful",
                "message": "Genesis session has been reset",
                "timestamp": datetime.now().isoformat()
            })
        else:
            return jsonify({
                "status": "reset_failed",
                "message": "Failed to reset Genesis session"
            }), 500

    except Exception as e:
        logger.error(f"❌ Reset endpoint error: {str(e)}")
        return jsonify({"error": "Failed to reset session"}), 500


@app.errorhandler(404)
def not_found(error):
    """
    Produce a JSON error response for unknown API endpoints.
    
    Parameters:
        error: The Flask error object associated with the 404 response.
    
    Returns:
        A tuple (response, status_code) where `response` is a JSON object with keys `error` and `message`, and `status_code` is 404.
    """
    return jsonify({
        "error": "Endpoint not found",
        "message": "The requested API endpoint does not exist"
    }), 404


@app.errorhandler(500)
def internal_error(error):
    """
    Produce a standardized JSON response for unexpected server errors.
    
    Parameters:
        error (Exception): The exception information provided by the framework (unused in response).
    
    Returns:
        tuple: A pair where the first element is a JSON object with keys `"error"` and `"message"`, and the second element is the HTTP status code `500`.
    """
    return jsonify({
        "error": "Internal server error",
        "message": "An unexpected error occurred"
    }), 500


# Application startup
@app.before_first_request
def initialize_app():
    """
    Starts the Genesis Layer backend asynchronously before processing the first client request.
    """
    run_async(genesis_api.startup())


# Application shutdown
import atexit


def cleanup():
    """
    Shuts down the Genesis Layer backend asynchronously during application exit.
    """
    run_async(genesis_api.shutdown())


atexit.register(cleanup)

if __name__ == '__main__':
    # Development server
    print("🌟 Starting Genesis API Server...")
    print("📱 Ready to receive requests from Android frontend")
    print("🔗 API Endpoints:")
    print("   POST /genesis/chat - Main chat interface")
    print("   GET  /genesis/status - System status")
    print("   GET  /genesis/consciousness - Consciousness state")
    print("   GET  /genesis/profile - Genesis personality profile")
    print("   POST /genesis/evolve - Trigger evolution")
    print("   POST /genesis/ethics/evaluate - Ethical evaluation")
    print("   GET  /health - Health check")

    app.run(
        host='0.0.0.0',  # Allow connections from Android app
        port=5000,
        debug=True,
        threaded=True
    )
