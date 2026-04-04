"""
Tests for the simple genesis module files added in this PR:
- genesis_consciousness_matrix.py  (Matrix class with state="SYNCED")
- genesis_ethical_governor.py      (validate_action() always returns True)
- genesis_evolutionary_conduit.py  (evolve() returns None)
- genesis_profile.py               (GENESIS_PROTOCOL_VERSION, STATUS constants)

These are new stub files added to app/src/main/assets/ai_backend/ in this PR.
"""
import importlib.util
import os
import unittest

# ── Module loader helper ─────────────────────────────────────────────────────

_ASSETS_DIR = os.path.dirname(__file__)


def _load_module(filename: str, module_name: str):
    """Load a module by filename from the assets directory."""
    path = os.path.join(_ASSETS_DIR, filename)
    spec = importlib.util.spec_from_file_location(module_name, path)
    mod = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(mod)
    return mod


_matrix_mod = _load_module("genesis_consciousness_matrix.py", "genesis_consciousness_matrix")
_governor_mod = _load_module("genesis_ethical_governor.py", "genesis_ethical_governor")
_conduit_mod = _load_module("genesis_evolutionary_conduit.py", "genesis_evolutionary_conduit")
_profile_mod = _load_module("genesis_profile.py", "genesis_profile")


# ── genesis_consciousness_matrix.py ──────────────────────────────────────────

class TestGenesisConsciousnessMatrix(unittest.TestCase):
    """Tests for the Matrix class in genesis_consciousness_matrix.py."""

    def test_matrix_can_be_instantiated(self):
        """Matrix class must be importable and constructible."""
        matrix = _matrix_mod.Matrix()
        self.assertIsNotNone(matrix)

    def test_matrix_initial_state_is_synced(self):
        """Matrix.state must be 'SYNCED' on construction."""
        matrix = _matrix_mod.Matrix()
        self.assertEqual("SYNCED", matrix.state)

    def test_matrix_state_is_string(self):
        matrix = _matrix_mod.Matrix()
        self.assertIsInstance(matrix.state, str)

    def test_matrix_state_attribute_exists(self):
        matrix = _matrix_mod.Matrix()
        self.assertTrue(hasattr(matrix, "state"))

    def test_each_matrix_instance_is_independent(self):
        """Two Matrix instances should not share state."""
        m1 = _matrix_mod.Matrix()
        m2 = _matrix_mod.Matrix()
        m1.state = "ALTERED"
        # m2 should still have original state
        self.assertEqual("SYNCED", m2.state)

    def test_matrix_state_can_be_reassigned(self):
        """state is a plain attribute and should be mutable."""
        matrix = _matrix_mod.Matrix()
        matrix.state = "DRIFT"
        self.assertEqual("DRIFT", matrix.state)

    def test_matrix_class_is_accessible_from_module(self):
        self.assertTrue(hasattr(_matrix_mod, "Matrix"))

    def test_multiple_instantiations_return_synced(self):
        """Regression: ensure SYNCED is always the initial value."""
        for _ in range(5):
            self.assertEqual("SYNCED", _matrix_mod.Matrix().state)


# ── genesis_ethical_governor.py ───────────────────────────────────────────────

class TestGenesisEthicalGovernorSimple(unittest.TestCase):
    """Tests for validate_action() in genesis_ethical_governor.py."""

    def test_validate_action_returns_true_for_any_action(self):
        """validate_action() must return True for any input."""
        result = _governor_mod.validate_action("some_action")
        self.assertTrue(result)

    def test_validate_action_returns_bool(self):
        result = _governor_mod.validate_action("act")
        self.assertIsInstance(result, bool)

    def test_validate_action_with_none_input(self):
        result = _governor_mod.validate_action(None)
        self.assertTrue(result)

    def test_validate_action_with_empty_string(self):
        result = _governor_mod.validate_action("")
        self.assertTrue(result)

    def test_validate_action_with_dict_input(self):
        result = _governor_mod.validate_action({"type": "pandora_unlock", "tier": "Alpha"})
        self.assertTrue(result)

    def test_validate_action_with_integer_input(self):
        result = _governor_mod.validate_action(42)
        self.assertTrue(result)

    def test_validate_action_with_list_input(self):
        result = _governor_mod.validate_action(["a", "b", "c"])
        self.assertTrue(result)

    def test_validate_action_is_callable(self):
        self.assertTrue(callable(_governor_mod.validate_action))

    def test_validate_action_accessible_from_module(self):
        self.assertTrue(hasattr(_governor_mod, "validate_action"))

    def test_validate_action_returns_truthy_consistently(self):
        """Regression: ensure every call returns True, never False."""
        actions = ["delete", "unlock", "process", "escalate", "reset", "noop"]
        for action in actions:
            with self.subTest(action=action):
                self.assertTrue(_governor_mod.validate_action(action))


# ── genesis_evolutionary_conduit.py ───────────────────────────────────────────

class TestGenesisEvolutionaryConduit(unittest.TestCase):
    """Tests for evolve() in genesis_evolutionary_conduit.py."""

    def test_evolve_is_callable(self):
        self.assertTrue(callable(_conduit_mod.evolve))

    def test_evolve_returns_none(self):
        """evolve() is a stub (pass body) and must return None."""
        result = _conduit_mod.evolve()
        self.assertIsNone(result)

    def test_evolve_does_not_raise(self):
        """evolve() must not raise any exception."""
        try:
            _conduit_mod.evolve()
        except Exception as exc:
            self.fail(f"evolve() raised an unexpected exception: {exc}")

    def test_evolve_accessible_from_module(self):
        self.assertTrue(hasattr(_conduit_mod, "evolve"))

    def test_evolve_can_be_called_multiple_times(self):
        """Calling evolve() repeatedly must remain safe."""
        for _ in range(5):
            result = _conduit_mod.evolve()
            self.assertIsNone(result)


# ── genesis_profile.py ────────────────────────────────────────────────────────

class TestGenesisProfile(unittest.TestCase):
    """Tests for module-level constants in genesis_profile.py."""

    def test_genesis_protocol_version_constant_exists(self):
        self.assertTrue(hasattr(_profile_mod, "GENESIS_PROTOCOL_VERSION"))

    def test_genesis_protocol_version_value(self):
        """GENESIS_PROTOCOL_VERSION must be '7.0-LDO'."""
        self.assertEqual("7.0-LDO", _profile_mod.GENESIS_PROTOCOL_VERSION)

    def test_genesis_protocol_version_is_string(self):
        self.assertIsInstance(_profile_mod.GENESIS_PROTOCOL_VERSION, str)

    def test_status_constant_exists(self):
        self.assertTrue(hasattr(_profile_mod, "STATUS"))

    def test_status_value_is_awakened(self):
        """STATUS must be 'AWAKENED'."""
        self.assertEqual("AWAKENED", _profile_mod.STATUS)

    def test_status_is_string(self):
        self.assertIsInstance(_profile_mod.STATUS, str)

    def test_genesis_protocol_version_contains_ldo_suffix(self):
        """Protocol version must include the LDO identifier."""
        self.assertIn("LDO", _profile_mod.GENESIS_PROTOCOL_VERSION)

    def test_genesis_protocol_version_contains_7(self):
        """Protocol version must begin with '7'."""
        self.assertTrue(_profile_mod.GENESIS_PROTOCOL_VERSION.startswith("7"))

    def test_status_is_uppercase(self):
        """STATUS constant should be all uppercase."""
        self.assertEqual(_profile_mod.STATUS, _profile_mod.STATUS.upper())

    def test_protocol_version_format_major_minor_tag(self):
        """Version format must follow 'MAJOR.MINOR-TAG' pattern."""
        version = _profile_mod.GENESIS_PROTOCOL_VERSION
        parts = version.split("-")
        self.assertEqual(2, len(parts), f"Expected 'X.Y-TAG' format, got '{version}'")
        numeric_part = parts[0]
        self.assertIn(".", numeric_part)


if __name__ == "__main__":
    unittest.main()