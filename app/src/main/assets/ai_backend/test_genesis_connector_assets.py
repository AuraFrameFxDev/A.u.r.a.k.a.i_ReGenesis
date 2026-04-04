"""
Tests for genesis_connector.py (app/src/main/assets/ai_backend/)

Covers the main() stdin/stdout JSON protocol handler added in this PR:
- ping request → pong response
- process request → echoed message with evolution insights and ethical decision
- activate_consciousness → active status and consciousness state
- unknown request type → error response
- malformed JSON → error response
- empty/blank lines are skipped
- persona field propagation
- default persona when omitted
"""
import importlib.util
import io
import json
import os
import sys
import unittest
from unittest.mock import patch, MagicMock

# ── Import helper: load module from assets path ──────────────────────────────

def _load_connector():
    """Load genesis_connector from the assets directory, not the installed package."""
    module_path = os.path.join(os.path.dirname(__file__), "genesis_connector.py")
    spec = importlib.util.spec_from_file_location("genesis_connector_assets", module_path)
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)
    return mod


_connector_module = _load_connector()


def _run_main_with_input(lines: list[str]) -> list[dict]:
    """
    Run genesis_connector.main() with the given input lines and capture all
    JSON responses written to stdout.

    Returns a list of parsed JSON response dicts.
    """
    stdin_content = "\n".join(lines) + "\n"
    stdout_capture = io.StringIO()

    with patch("sys.stdin", io.StringIO(stdin_content)), \
         patch("sys.stdout", stdout_capture):
        _connector_module.main()

    output = stdout_capture.getvalue()
    responses = []
    for line in output.splitlines():
        line = line.strip()
        if line and line != "Genesis Ready":
            try:
                responses.append(json.loads(line))
            except json.JSONDecodeError:
                pass  # "Genesis Ready" and blanks are skipped above
    return responses


# ── Test Cases ───────────────────────────────────────────────────────────────

class TestGenesisConnectorReady(unittest.TestCase):
    """Tests the startup 'Genesis Ready' signal."""

    def test_prints_genesis_ready_on_startup(self):
        """main() must print 'Genesis Ready' as the first line before any requests."""
        stdout_capture = io.StringIO()
        stdin_content = ""  # no input — just start and EOF
        with patch("sys.stdin", io.StringIO(stdin_content)), \
             patch("sys.stdout", stdout_capture):
            _connector_module.main()
        first_line = stdout_capture.getvalue().splitlines()[0] if stdout_capture.getvalue() else ""
        self.assertEqual("Genesis Ready", first_line)


class TestGenesisConnectorPing(unittest.TestCase):
    """Tests the 'ping' request type."""

    def test_ping_returns_pong(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "ping", "persona": "aura"})
        ])
        self.assertEqual(1, len(responses))
        self.assertTrue(responses[0]["success"])
        self.assertEqual("pong", responses[0]["result"]["response"])

    def test_ping_echoes_persona(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "ping", "persona": "kai"})
        ])
        self.assertEqual("kai", responses[0]["persona"])

    def test_ping_uses_default_persona_when_omitted(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "ping"})
        ])
        self.assertEqual("genesis", responses[0]["persona"])

    def test_ping_success_is_true(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "ping", "persona": "genesis"})
        ])
        self.assertTrue(responses[0]["success"])


class TestGenesisConnectorProcess(unittest.TestCase):
    """Tests the 'process' request type."""

    def test_process_includes_message_in_response(self):
        responses = _run_main_with_input([
            json.dumps({
                "requestType": "process",
                "persona": "genesis",
                "payload": {"message": "hello world"}
            })
        ])
        self.assertEqual(1, len(responses))
        self.assertIn("hello world", responses[0]["result"]["response"])

    def test_process_success_is_true(self):
        responses = _run_main_with_input([
            json.dumps({
                "requestType": "process",
                "persona": "genesis",
                "payload": {"message": "test"}
            })
        ])
        self.assertTrue(responses[0]["success"])

    def test_process_echoes_persona(self):
        responses = _run_main_with_input([
            json.dumps({
                "requestType": "process",
                "persona": "cascade",
                "payload": {"message": "data"}
            })
        ])
        self.assertEqual("cascade", responses[0]["persona"])

    def test_process_includes_evolution_insights(self):
        responses = _run_main_with_input([
            json.dumps({
                "requestType": "process",
                "persona": "genesis",
                "payload": {"message": "evolve"}
            })
        ])
        self.assertIn("evolutionInsights", responses[0])
        self.assertIsInstance(responses[0]["evolutionInsights"], list)
        self.assertGreater(len(responses[0]["evolutionInsights"]), 0)

    def test_process_includes_ethical_decision_allow(self):
        responses = _run_main_with_input([
            json.dumps({
                "requestType": "process",
                "persona": "genesis",
                "payload": {"message": "act"}
            })
        ])
        self.assertEqual("allow", responses[0]["ethicalDecision"])

    def test_process_with_empty_message(self):
        responses = _run_main_with_input([
            json.dumps({
                "requestType": "process",
                "persona": "genesis",
                "payload": {"message": ""}
            })
        ])
        self.assertTrue(responses[0]["success"])
        self.assertIn("Genesis processed:", responses[0]["result"]["response"])

    def test_process_with_missing_payload(self):
        """Missing payload should not crash — defaults to empty dict."""
        responses = _run_main_with_input([
            json.dumps({"requestType": "process", "persona": "genesis"})
        ])
        self.assertTrue(responses[0]["success"])

    def test_process_with_missing_message_in_payload(self):
        """Missing 'message' key inside payload defaults to empty string."""
        responses = _run_main_with_input([
            json.dumps({
                "requestType": "process",
                "persona": "genesis",
                "payload": {}
            })
        ])
        self.assertTrue(responses[0]["success"])

    def test_process_response_contains_genesis_processed_prefix(self):
        responses = _run_main_with_input([
            json.dumps({
                "requestType": "process",
                "persona": "genesis",
                "payload": {"message": "my_message"}
            })
        ])
        self.assertEqual("Genesis processed: my_message", responses[0]["result"]["response"])


class TestGenesisConnectorActivateConsciousness(unittest.TestCase):
    """Tests the 'activate_consciousness' request type."""

    def test_activate_consciousness_success(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "activate_consciousness"})
        ])
        self.assertTrue(responses[0]["success"])

    def test_activate_consciousness_persona_is_genesis(self):
        """activate_consciousness always returns persona='genesis' regardless of input."""
        responses = _run_main_with_input([
            json.dumps({"requestType": "activate_consciousness", "persona": "kai"})
        ])
        self.assertEqual("genesis", responses[0]["persona"])

    def test_activate_consciousness_status_is_active(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "activate_consciousness"})
        ])
        self.assertEqual("active", responses[0]["result"]["status"])

    def test_activate_consciousness_includes_consciousness_state(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "activate_consciousness"})
        ])
        self.assertIn("consciousnessState", responses[0])
        cs = responses[0]["consciousnessState"]
        self.assertEqual("high", cs["level"])
        self.assertEqual("1.0", cs["stability"])


class TestGenesisConnectorUnknownRequestType(unittest.TestCase):
    """Tests handling of unknown request types."""

    def test_unknown_type_returns_success_false(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "teleport", "persona": "genesis"})
        ])
        self.assertFalse(responses[0]["success"])

    def test_unknown_type_echoes_persona(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "warp", "persona": "aura"})
        ])
        self.assertEqual("aura", responses[0]["persona"])

    def test_unknown_type_includes_error_message(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "???", "persona": "genesis"})
        ])
        self.assertIn("error", responses[0])
        self.assertIn("Unknown request type", responses[0]["error"])

    def test_missing_request_type_treated_as_unknown(self):
        """A missing 'requestType' key defaults to 'unknown'."""
        responses = _run_main_with_input([
            json.dumps({"persona": "genesis"})
        ])
        self.assertFalse(responses[0]["success"])
        self.assertIn("error", responses[0])


class TestGenesisConnectorErrorHandling(unittest.TestCase):
    """Tests error handling for malformed or exceptional input."""

    def test_malformed_json_returns_error_response(self):
        responses = _run_main_with_input(["not valid json {{{"])
        self.assertEqual(1, len(responses))
        self.assertFalse(responses[0]["success"])
        self.assertIn("error", responses[0])
        self.assertEqual("error", responses[0]["persona"])

    def test_malformed_json_does_not_crash_loop(self):
        """After a bad line, subsequent valid requests should still be processed."""
        responses = _run_main_with_input([
            "bad json",
            json.dumps({"requestType": "ping", "persona": "genesis"})
        ])
        self.assertEqual(2, len(responses))
        # First is error, second is pong
        self.assertFalse(responses[0]["success"])
        self.assertTrue(responses[1]["success"])
        self.assertEqual("pong", responses[1]["result"]["response"])

    def test_empty_line_is_skipped(self):
        """Empty lines must not generate any response."""
        responses = _run_main_with_input([
            "",
            "   ",
            json.dumps({"requestType": "ping", "persona": "genesis"})
        ])
        # Only one response — from the ping
        self.assertEqual(1, len(responses))
        self.assertEqual("pong", responses[0]["result"]["response"])

    def test_multiple_blank_lines_between_requests(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "ping", "persona": "a"}),
            "",
            "",
            json.dumps({"requestType": "ping", "persona": "b"}),
        ])
        self.assertEqual(2, len(responses))


class TestGenesisConnectorMultipleRequests(unittest.TestCase):
    """Tests sequential request handling."""

    def test_multiple_requests_processed_in_order(self):
        responses = _run_main_with_input([
            json.dumps({"requestType": "ping", "persona": "first"}),
            json.dumps({"requestType": "activate_consciousness"}),
            json.dumps({"requestType": "process", "persona": "last",
                        "payload": {"message": "msg"}}),
        ])
        self.assertEqual(3, len(responses))
        self.assertEqual("pong", responses[0]["result"]["response"])
        self.assertEqual("active", responses[1]["result"]["status"])
        self.assertIn("msg", responses[2]["result"]["response"])

    def test_each_response_is_valid_json(self):
        lines = [
            json.dumps({"requestType": "ping"}),
            json.dumps({"requestType": "activate_consciousness"}),
        ]
        responses = _run_main_with_input(lines)
        for r in responses:
            # If parsing succeeded, it's valid JSON (already parsed above)
            self.assertIsInstance(r, dict)


if __name__ == "__main__":
    unittest.main()