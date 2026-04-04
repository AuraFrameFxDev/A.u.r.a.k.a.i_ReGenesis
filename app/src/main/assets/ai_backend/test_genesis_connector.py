"""
Tests for genesis_connector.py

Covers the main() stdin/stdout JSON protocol handler:
- Startup "Genesis Ready" signal
- ping request → pong response
- process request → echoed message with evolution insights and ethical decision
- activate_consciousness → active status and consciousness state
- unknown request type → error response
- malformed JSON → error response
- empty/blank lines are skipped
- persona field propagation
- default persona when omitted
- sequential/multiple request handling
"""
import importlib.util
import io
import json
import os
import sys
import unittest
from unittest.mock import patch


# --- Module loader ---

def _load_connector():
    """Load genesis_connector from the assets directory."""
    module_path = os.path.join(os.path.dirname(__file__), "genesis_connector.py")
    spec = importlib.util.spec_from_file_location("genesis_connector", module_path)
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)
    return mod


_connector = _load_connector()


def _run_main(lines: list) -> list:
    """
    Run genesis_connector.main() with the given input lines.
    Returns a list of parsed JSON response dicts (excludes 'Genesis Ready').
    """
    stdin_text = "\n".join(lines) + "\n"
    stdout_buf = io.StringIO()

    with patch("sys.stdin", io.StringIO(stdin_text)), \
         patch("sys.stdout", stdout_buf):
        _connector.main()

    responses = []
    for raw_line in stdout_buf.getvalue().splitlines():
        stripped = raw_line.strip()
        if stripped and stripped != "Genesis Ready":
            try:
                responses.append(json.loads(stripped))
            except json.JSONDecodeError:
                pass
    return responses


# --- Test Classes ---

class TestStartupSignal(unittest.TestCase):
    """main() must print 'Genesis Ready' as the very first line."""

    def test_genesis_ready_is_first_line(self):
        stdout_buf = io.StringIO()
        with patch("sys.stdin", io.StringIO("")), \
             patch("sys.stdout", stdout_buf):
            _connector.main()
        lines = stdout_buf.getvalue().splitlines()
        self.assertTrue(len(lines) >= 1)
        self.assertEqual("Genesis Ready", lines[0])

    def test_genesis_ready_printed_even_with_no_input(self):
        stdout_buf = io.StringIO()
        with patch("sys.stdin", io.StringIO("")), \
             patch("sys.stdout", stdout_buf):
            _connector.main()
        self.assertIn("Genesis Ready", stdout_buf.getvalue())


class TestPingRequest(unittest.TestCase):
    """Tests for the 'ping' request type."""

    def test_ping_returns_pong(self):
        responses = _run_main([json.dumps({"requestType": "ping", "persona": "aura"})])
        self.assertEqual(1, len(responses))
        self.assertEqual("pong", responses[0]["result"]["response"])

    def test_ping_success_is_true(self):
        responses = _run_main([json.dumps({"requestType": "ping", "persona": "genesis"})])
        self.assertTrue(responses[0]["success"])

    def test_ping_echoes_persona(self):
        responses = _run_main([json.dumps({"requestType": "ping", "persona": "kai"})])
        self.assertEqual("kai", responses[0]["persona"])

    def test_ping_default_persona_when_omitted(self):
        responses = _run_main([json.dumps({"requestType": "ping"})])
        self.assertEqual("genesis", responses[0]["persona"])

    def test_ping_response_has_result_key(self):
        responses = _run_main([json.dumps({"requestType": "ping", "persona": "test"})])
        self.assertIn("result", responses[0])

    def test_ping_result_has_response_key(self):
        responses = _run_main([json.dumps({"requestType": "ping"})])
        self.assertIn("response", responses[0]["result"])

    def test_ping_with_empty_persona_string(self):
        # Empty string persona should be preserved (not replaced by default)
        responses = _run_main([json.dumps({"requestType": "ping", "persona": ""})])
        self.assertEqual("", responses[0]["persona"])

    def test_ping_response_is_valid_json(self):
        stdout_buf = io.StringIO()
        with patch("sys.stdin", io.StringIO(json.dumps({"requestType": "ping"}) + "\n")), \
             patch("sys.stdout", stdout_buf):
            _connector.main()
        # Find the response line (not Genesis Ready)
        for line in stdout_buf.getvalue().splitlines():
            if line.strip() and line.strip() != "Genesis Ready":
                parsed = json.loads(line)  # should not raise
                self.assertIsInstance(parsed, dict)


class TestProcessRequest(unittest.TestCase):
    """Tests for the 'process' request type."""

    def _process(self, message="hello", persona="genesis", extra_payload=None):
        payload = {"message": message}
        if extra_payload:
            payload.update(extra_payload)
        return _run_main([json.dumps({
            "requestType": "process",
            "persona": persona,
            "payload": payload
        })])[0]

    def test_process_success_is_true(self):
        self.assertTrue(self._process()["success"])

    def test_process_echoes_persona(self):
        r = self._process(persona="cascade")
        self.assertEqual("cascade", r["persona"])

    def test_process_response_contains_genesis_processed_prefix(self):
        r = self._process(message="my_message")
        self.assertEqual("Genesis processed: my_message", r["result"]["response"])

    def test_process_includes_message_in_response(self):
        r = self._process(message="hello world")
        self.assertIn("hello world", r["result"]["response"])

    def test_process_has_evolution_insights(self):
        r = self._process()
        self.assertIn("evolutionInsights", r)
        self.assertIsInstance(r["evolutionInsights"], list)
        self.assertGreater(len(r["evolutionInsights"]), 0)

    def test_process_evolution_insights_contain_strings(self):
        r = self._process()
        for insight in r["evolutionInsights"]:
            self.assertIsInstance(insight, str)

    def test_process_has_ethical_decision_allow(self):
        r = self._process()
        self.assertEqual("allow", r["ethicalDecision"])

    def test_process_with_empty_message(self):
        r = self._process(message="")
        self.assertTrue(r["success"])
        self.assertEqual("Genesis processed: ", r["result"]["response"])

    def test_process_with_missing_payload(self):
        responses = _run_main([json.dumps({"requestType": "process", "persona": "genesis"})])
        self.assertEqual(1, len(responses))
        self.assertTrue(responses[0]["success"])

    def test_process_with_empty_payload(self):
        responses = _run_main([json.dumps({
            "requestType": "process",
            "persona": "genesis",
            "payload": {}
        })])
        self.assertTrue(responses[0]["success"])

    def test_process_default_persona_when_omitted(self):
        responses = _run_main([json.dumps({
            "requestType": "process",
            "payload": {"message": "test"}
        })])
        self.assertEqual("genesis", responses[0]["persona"])

    def test_process_has_result_key_with_response(self):
        r = self._process(message="data")
        self.assertIn("result", r)
        self.assertIn("response", r["result"])

    def test_process_with_long_message(self):
        long_msg = "x" * 1000
        r = self._process(message=long_msg)
        self.assertTrue(r["success"])
        self.assertIn(long_msg, r["result"]["response"])

    def test_process_special_characters_in_message(self):
        special = 'Hello "world" & <test>'
        r = self._process(message=special)
        self.assertTrue(r["success"])

    def test_process_response_has_correct_structure(self):
        r = self._process(message="structure_test")
        self.assertIn("success", r)
        self.assertIn("persona", r)
        self.assertIn("result", r)
        self.assertIn("evolutionInsights", r)
        self.assertIn("ethicalDecision", r)


class TestActivateConsciousness(unittest.TestCase):
    """Tests for the 'activate_consciousness' request type."""

    def _activate(self, extra=None):
        req = {"requestType": "activate_consciousness"}
        if extra:
            req.update(extra)
        return _run_main([json.dumps(req)])[0]

    def test_activate_success_is_true(self):
        self.assertTrue(self._activate()["success"])

    def test_activate_persona_is_always_genesis(self):
        # Even if persona is provided, activate_consciousness hardcodes "genesis"
        r = self._activate({"persona": "kai"})
        self.assertEqual("genesis", r["persona"])

    def test_activate_status_is_active(self):
        self.assertEqual("active", self._activate()["result"]["status"])

    def test_activate_has_consciousness_state(self):
        r = self._activate()
        self.assertIn("consciousnessState", r)

    def test_activate_consciousness_state_level_is_high(self):
        r = self._activate()
        self.assertEqual("high", r["consciousnessState"]["level"])

    def test_activate_consciousness_state_stability_is_1(self):
        r = self._activate()
        self.assertEqual("1.0", r["consciousnessState"]["stability"])

    def test_activate_has_result_key(self):
        r = self._activate()
        self.assertIn("result", r)

    def test_activate_result_has_status_key(self):
        r = self._activate()
        self.assertIn("status", r["result"])

    def test_activate_without_persona_field(self):
        # No persona key at all
        responses = _run_main([json.dumps({"requestType": "activate_consciousness"})])
        self.assertTrue(responses[0]["success"])
        self.assertEqual("genesis", responses[0]["persona"])


class TestUnknownRequestType(unittest.TestCase):
    """Tests for unknown/unrecognized request types."""

    def test_unknown_type_success_is_false(self):
        responses = _run_main([json.dumps({"requestType": "teleport", "persona": "genesis"})])
        self.assertFalse(responses[0]["success"])

    def test_unknown_type_echoes_persona(self):
        responses = _run_main([json.dumps({"requestType": "warp", "persona": "aura"})])
        self.assertEqual("aura", responses[0]["persona"])

    def test_unknown_type_includes_error_message(self):
        responses = _run_main([json.dumps({"requestType": "???", "persona": "genesis"})])
        self.assertIn("error", responses[0])
        self.assertIn("Unknown request type", responses[0]["error"])

    def test_unknown_type_error_contains_type_name(self):
        responses = _run_main([json.dumps({"requestType": "destroy", "persona": "genesis"})])
        self.assertIn("destroy", responses[0]["error"])

    def test_missing_request_type_treated_as_unknown(self):
        responses = _run_main([json.dumps({"persona": "genesis"})])
        self.assertFalse(responses[0]["success"])

    def test_missing_request_type_defaults_to_unknown(self):
        responses = _run_main([json.dumps({"persona": "genesis"})])
        self.assertIn("Unknown request type: unknown", responses[0]["error"])

    def test_empty_request_type_is_unknown(self):
        responses = _run_main([json.dumps({"requestType": "", "persona": "genesis"})])
        self.assertFalse(responses[0]["success"])

    def test_unknown_type_default_persona_when_omitted(self):
        responses = _run_main([json.dumps({"requestType": "noop"})])
        self.assertEqual("genesis", responses[0]["persona"])


class TestErrorHandling(unittest.TestCase):
    """Tests for malformed input and exception handling."""

    def test_malformed_json_returns_error_response(self):
        responses = _run_main(["not valid json {{{"])
        self.assertEqual(1, len(responses))
        self.assertFalse(responses[0]["success"])
        self.assertIn("error", responses[0])

    def test_malformed_json_persona_is_error(self):
        responses = _run_main(["bad json"])
        self.assertEqual("error", responses[0]["persona"])

    def test_malformed_json_does_not_halt_loop(self):
        """After a bad line, subsequent valid requests are still processed."""
        responses = _run_main([
            "bad json",
            json.dumps({"requestType": "ping", "persona": "genesis"})
        ])
        self.assertEqual(2, len(responses))
        self.assertFalse(responses[0]["success"])
        self.assertTrue(responses[1]["success"])
        self.assertEqual("pong", responses[1]["result"]["response"])

    def test_empty_line_produces_no_response(self):
        responses = _run_main([
            "",
            json.dumps({"requestType": "ping", "persona": "genesis"})
        ])
        self.assertEqual(1, len(responses))
        self.assertEqual("pong", responses[0]["result"]["response"])

    def test_whitespace_only_line_is_skipped(self):
        responses = _run_main([
            "   ",
            json.dumps({"requestType": "ping"})
        ])
        self.assertEqual(1, len(responses))

    def test_multiple_blank_lines_between_requests(self):
        responses = _run_main([
            json.dumps({"requestType": "ping", "persona": "a"}),
            "",
            "",
            json.dumps({"requestType": "ping", "persona": "b"}),
        ])
        self.assertEqual(2, len(responses))

    def test_json_array_instead_of_object_returns_error(self):
        """Sending a JSON array (not an object) triggers exception handling."""
        responses = _run_main(['["not", "an", "object"]'])
        # The module will call .get() on a list which raises AttributeError
        self.assertEqual(1, len(responses))
        self.assertFalse(responses[0]["success"])

    def test_error_response_has_required_keys(self):
        responses = _run_main(["bad json"])
        r = responses[0]
        self.assertIn("success", r)
        self.assertIn("persona", r)
        self.assertIn("error", r)
        self.assertFalse(r["success"])


class TestMultipleRequests(unittest.TestCase):
    """Tests for sequential request processing."""

    def test_three_different_requests_processed_in_order(self):
        responses = _run_main([
            json.dumps({"requestType": "ping", "persona": "first"}),
            json.dumps({"requestType": "activate_consciousness"}),
            json.dumps({"requestType": "process", "persona": "last", "payload": {"message": "msg"}}),
        ])
        self.assertEqual(3, len(responses))
        self.assertEqual("pong", responses[0]["result"]["response"])
        self.assertEqual("active", responses[1]["result"]["status"])
        self.assertIn("msg", responses[2]["result"]["response"])

    def test_ten_ping_requests_all_produce_responses(self):
        lines = [json.dumps({"requestType": "ping", "persona": str(i)}) for i in range(10)]
        responses = _run_main(lines)
        self.assertEqual(10, len(responses))
        for r in responses:
            self.assertTrue(r["success"])
            self.assertEqual("pong", r["result"]["response"])

    def test_mixed_valid_and_invalid_requests(self):
        responses = _run_main([
            json.dumps({"requestType": "ping"}),
            "bad json",
            json.dumps({"requestType": "activate_consciousness"}),
        ])
        self.assertEqual(3, len(responses))
        self.assertTrue(responses[0]["success"])
        self.assertFalse(responses[1]["success"])
        self.assertTrue(responses[2]["success"])

    def test_each_response_is_a_dict(self):
        lines = [
            json.dumps({"requestType": "ping"}),
            json.dumps({"requestType": "activate_consciousness"}),
            json.dumps({"requestType": "process", "payload": {"message": "x"}}),
        ]
        responses = _run_main(lines)
        for r in responses:
            self.assertIsInstance(r, dict)

    def test_persona_is_independent_per_request(self):
        responses = _run_main([
            json.dumps({"requestType": "ping", "persona": "alpha"}),
            json.dumps({"requestType": "ping", "persona": "beta"}),
        ])
        self.assertEqual("alpha", responses[0]["persona"])
        self.assertEqual("beta", responses[1]["persona"])

    def test_error_then_success_sequence(self):
        responses = _run_main([
            "garbage",
            json.dumps({"requestType": "ping", "persona": "recovery"})
        ])
        self.assertEqual(2, len(responses))
        self.assertFalse(responses[0]["success"])
        self.assertTrue(responses[1]["success"])
        self.assertEqual("recovery", responses[1]["persona"])


class TestResponseStructure(unittest.TestCase):
    """Verifies the structure of each response type."""

    def test_ping_response_keys(self):
        r = _run_main([json.dumps({"requestType": "ping", "persona": "p"})])[0]
        self.assertIn("success", r)
        self.assertIn("persona", r)
        self.assertIn("result", r)
        self.assertIsInstance(r["result"], dict)

    def test_process_response_keys(self):
        r = _run_main([json.dumps({
            "requestType": "process",
            "persona": "p",
            "payload": {"message": "m"}
        })])[0]
        self.assertIn("success", r)
        self.assertIn("persona", r)
        self.assertIn("result", r)
        self.assertIn("evolutionInsights", r)
        self.assertIn("ethicalDecision", r)

    def test_activate_consciousness_response_keys(self):
        r = _run_main([json.dumps({"requestType": "activate_consciousness"})])[0]
        self.assertIn("success", r)
        self.assertIn("persona", r)
        self.assertIn("result", r)
        self.assertIn("consciousnessState", r)

    def test_consciousness_state_keys(self):
        r = _run_main([json.dumps({"requestType": "activate_consciousness"})])[0]
        cs = r["consciousnessState"]
        self.assertIn("level", cs)
        self.assertIn("stability", cs)

    def test_unknown_type_response_keys(self):
        r = _run_main([json.dumps({"requestType": "bogus", "persona": "p"})])[0]
        self.assertIn("success", r)
        self.assertIn("persona", r)
        self.assertIn("error", r)
        self.assertNotIn("result", r)  # no result on error


if __name__ == "__main__":
    unittest.main()