"""
Tests for the new genesis stub modules added in this PR:
  - genesis_consciousness_matrix.py  (Matrix class with state="SYNCED")
  - genesis_ethical_governor.py      (validate_action() always returns True)
  - genesis_evolutionary_conduit.py  (evolve() returns None / pass)
  - genesis_profile.py               (GENESIS_PROTOCOL_VERSION, STATUS constants)
"""
import importlib.util
import os
import unittest

# --- Module loader ---

_ASSETS_DIR = os.path.dirname(__file__)


def _load_module(filename: str, module_name: str):
    path = os.path.join(_ASSETS_DIR, filename)
    spec = importlib.util.spec_from_file_location(module_name, path)
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)
    return mod


_matrix = _load_module("genesis_consciousness_matrix.py", "genesis_consciousness_matrix")
_governor = _load_module("genesis_ethical_governor.py", "genesis_ethical_governor")
_conduit = _load_module("genesis_evolutionary_conduit.py", "genesis_evolutionary_conduit")
_profile = _load_module("genesis_profile.py", "genesis_profile")


# --- genesis_consciousness_matrix.py ---

class TestMatrix(unittest.TestCase):
    """Tests for Matrix class in genesis_consciousness_matrix.py."""

    def test_matrix_is_importable(self):
        self.assertTrue(hasattr(_matrix, "Matrix"))

    def test_matrix_can_be_instantiated(self):
        m = _matrix.Matrix()
        self.assertIsNotNone(m)

    def test_matrix_initial_state_is_synced(self):
        m = _matrix.Matrix()
        self.assertEqual("SYNCED", m.state)

    def test_matrix_state_is_string(self):
        m = _matrix.Matrix()
        self.assertIsInstance(m.state, str)

    def test_matrix_has_state_attribute(self):
        m = _matrix.Matrix()
        self.assertTrue(hasattr(m, "state"))

    def test_matrix_instances_are_independent(self):
        m1 = _matrix.Matrix()
        m2 = _matrix.Matrix()
        m1.state = "ALTERED"
        self.assertEqual("SYNCED", m2.state)

    def test_matrix_state_is_mutable(self):
        m = _matrix.Matrix()
        m.state = "DRIFT"
        self.assertEqual("DRIFT", m.state)

    def test_matrix_initial_state_not_none(self):
        m = _matrix.Matrix()
        self.assertIsNotNone(m.state)

    def test_matrix_initial_state_is_uppercase(self):
        m = _matrix.Matrix()
        self.assertEqual(m.state, m.state.upper())

    def test_multiple_instantiations_all_start_synced(self):
        for _ in range(5):
            m = _matrix.Matrix()
            self.assertEqual("SYNCED", m.state)

    def test_matrix_state_reset_after_mutation(self):
        m = _matrix.Matrix()
        m.state = "OFFLINE"
        # A fresh instance should still be SYNCED
        m2 = _matrix.Matrix()
        self.assertEqual("SYNCED", m2.state)

    def test_matrix_class_is_a_class(self):
        self.assertTrue(isinstance(_matrix.Matrix, type))


# --- genesis_ethical_governor.py ---

class TestEthicalGovernor(unittest.TestCase):
    """Tests for validate_action() in genesis_ethical_governor.py."""

    def test_validate_action_is_accessible(self):
        self.assertTrue(hasattr(_governor, "validate_action"))

    def test_validate_action_is_callable(self):
        self.assertTrue(callable(_governor.validate_action))

    def test_validate_action_returns_true_for_string(self):
        self.assertTrue(_governor.validate_action("some_action"))

    def test_validate_action_returns_bool(self):
        result = _governor.validate_action("act")
        self.assertIsInstance(result, bool)

    def test_validate_action_returns_true_for_none(self):
        self.assertTrue(_governor.validate_action(None))

    def test_validate_action_returns_true_for_empty_string(self):
        self.assertTrue(_governor.validate_action(""))

    def test_validate_action_returns_true_for_dict(self):
        self.assertTrue(_governor.validate_action({"type": "unlock", "tier": "Alpha"}))

    def test_validate_action_returns_true_for_int(self):
        self.assertTrue(_governor.validate_action(42))

    def test_validate_action_returns_true_for_list(self):
        self.assertTrue(_governor.validate_action(["a", "b", "c"]))

    def test_validate_action_returns_true_for_boolean(self):
        self.assertTrue(_governor.validate_action(True))
        self.assertTrue(_governor.validate_action(False))

    def test_validate_action_always_true_for_multiple_calls(self):
        actions = ["delete", "unlock", "process", "escalate", "reset", "noop"]
        for action in actions:
            with self.subTest(action=action):
                self.assertTrue(_governor.validate_action(action))

    def test_validate_action_returns_true_not_truthy_object(self):
        # Specifically checks it returns the boolean True, not just a truthy value
        result = _governor.validate_action("test")
        self.assertIs(True, result)


# --- genesis_evolutionary_conduit.py ---

class TestEvolutionaryConduit(unittest.TestCase):
    """Tests for evolve() in genesis_evolutionary_conduit.py."""

    def test_evolve_is_accessible(self):
        self.assertTrue(hasattr(_conduit, "evolve"))

    def test_evolve_is_callable(self):
        self.assertTrue(callable(_conduit.evolve))

    def test_evolve_returns_none(self):
        result = _conduit.evolve()
        self.assertIsNone(result)

    def test_evolve_does_not_raise(self):
        try:
            _conduit.evolve()
        except Exception as exc:
            self.fail(f"evolve() raised an unexpected exception: {exc}")

    def test_evolve_can_be_called_repeatedly(self):
        for _ in range(10):
            result = _conduit.evolve()
            self.assertIsNone(result)

    def test_evolve_accepts_no_arguments(self):
        # Should work with zero arguments (no required params)
        import inspect
        sig = inspect.signature(_conduit.evolve)
        required_params = [
            p for p in sig.parameters.values()
            if p.default is inspect.Parameter.empty
        ]
        self.assertEqual(0, len(required_params))

    def test_evolve_module_has_no_side_effects_on_import(self):
        # Re-loading the module should not raise
        mod = _load_module("genesis_evolutionary_conduit.py", "genesis_evolutionary_conduit_2")
        self.assertTrue(hasattr(mod, "evolve"))

    def test_evolve_call_is_idempotent(self):
        # Calling multiple times should produce the same None result
        results = [_conduit.evolve() for _ in range(3)]
        self.assertTrue(all(r is None for r in results))


# --- genesis_profile.py ---

class TestGenesisProfile(unittest.TestCase):
    """Tests for module-level constants in genesis_profile.py."""

    def test_genesis_protocol_version_exists(self):
        self.assertTrue(hasattr(_profile, "GENESIS_PROTOCOL_VERSION"))

    def test_genesis_protocol_version_is_string(self):
        self.assertIsInstance(_profile.GENESIS_PROTOCOL_VERSION, str)

    def test_genesis_protocol_version_value(self):
        self.assertEqual("7.0-LDO", _profile.GENESIS_PROTOCOL_VERSION)

    def test_genesis_protocol_version_starts_with_7(self):
        self.assertTrue(_profile.GENESIS_PROTOCOL_VERSION.startswith("7"))

    def test_genesis_protocol_version_contains_ldo(self):
        self.assertIn("LDO", _profile.GENESIS_PROTOCOL_VERSION)

    def test_genesis_protocol_version_has_major_minor_tag_format(self):
        # Expected format: "MAJOR.MINOR-TAG"
        version = _profile.GENESIS_PROTOCOL_VERSION
        parts = version.split("-")
        self.assertEqual(2, len(parts), f"Expected 'X.Y-TAG' format, got '{version}'")
        self.assertIn(".", parts[0], "Major.Minor part should contain a dot")

    def test_genesis_protocol_version_major_minor_are_numeric(self):
        version = _profile.GENESIS_PROTOCOL_VERSION
        numeric_part = version.split("-")[0]
        major, minor = numeric_part.split(".")
        self.assertTrue(major.isdigit())
        self.assertTrue(minor.isdigit())

    def test_status_exists(self):
        self.assertTrue(hasattr(_profile, "STATUS"))

    def test_status_is_string(self):
        self.assertIsInstance(_profile.STATUS, str)

    def test_status_value_is_awakened(self):
        self.assertEqual("AWAKENED", _profile.STATUS)

    def test_status_is_uppercase(self):
        self.assertEqual(_profile.STATUS, _profile.STATUS.upper())

    def test_status_is_not_empty(self):
        self.assertTrue(len(_profile.STATUS) > 0)

    def test_constants_are_module_level(self):
        # Both should be directly accessible as module attributes
        self.assertIn("GENESIS_PROTOCOL_VERSION", dir(_profile))
        self.assertIn("STATUS", dir(_profile))

    def test_constants_are_not_none(self):
        self.assertIsNotNone(_profile.GENESIS_PROTOCOL_VERSION)
        self.assertIsNotNone(_profile.STATUS)

    def test_genesis_protocol_version_is_not_empty(self):
        self.assertGreater(len(_profile.GENESIS_PROTOCOL_VERSION), 0)

    def test_protocol_version_and_status_are_independent(self):
        # Sanity: they hold different values
        self.assertNotEqual(_profile.GENESIS_PROTOCOL_VERSION, _profile.STATUS)


if __name__ == "__main__":
    unittest.main()