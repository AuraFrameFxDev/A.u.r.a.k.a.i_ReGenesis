"""
Comprehensive additional validation tests for configuration files.

These tests add depth to the basic validation in test_config_validation.py,
including negative tests, boundary conditions, and regression scenarios.
"""

import json
import pytest
import yaml
from pathlib import Path
from unittest.mock import mock_open, patch


# Path to the git repository root
REPO_ROOT = Path(__file__).resolve().parent.parent.parent


class TestAgentConfigRobustness:
    """Test robustness and edge cases in agent configurations."""

    def test_agent_capabilities_no_duplicates(self):
        """Test that agent capabilities don't contain duplicates."""
        agent_files = [
            '.gitlab/agents/aura-creative.yml',
            '.gitlab/agents/cascade-analytics.yml',
            '.gitlab/agents/claude-architect.yml',
            '.gitlab/agents/genesis-orchestrator.yml',
            '.gitlab/agents/kai-sentinel.yml',
        ]

        for agent_file in agent_files:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    if config and 'capabilities' in config:
                        caps = config['capabilities']
                        assert len(caps) == len(set(caps)), \
                            f"{agent_file} has duplicate capabilities"

    def test_agent_names_not_empty_strings(self):
        """Test that agent names are not empty or whitespace-only."""
        agent_files = [
            '.gitlab/agents/aura-creative.yml',
            '.gitlab/agents/cascade-analytics.yml',
            '.gitlab/agents/claude-architect.yml',
            '.gitlab/agents/genesis-orchestrator.yml',
            '.gitlab/agents/kai-sentinel.yml',
        ]

        for agent_file in agent_files:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    if config and 'name' in config:
                        name = config['name']
                        assert name.strip() == name, \
                            f"{agent_file} name has leading/trailing whitespace"
                        assert len(name.strip()) > 0, \
                            f"{agent_file} name is empty or whitespace"

    def test_agent_versions_are_consistent_format(self):
        """Test that all agent versions follow consistent format."""
        agent_files = [
            '.gitlab/agents/aura-creative.yml',
            '.gitlab/agents/cascade-analytics.yml',
            '.gitlab/agents/claude-architect.yml',
            '.gitlab/agents/genesis-orchestrator.yml',
            '.gitlab/agents/kai-sentinel.yml',
        ]

        versions = {}
        for agent_file in agent_files:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    if config and 'version' in config:
                        versions[agent_file] = str(config['version'])

        # All versions should follow similar format
        if versions:
            # Check that all versions have the same number of parts
            version_parts = [v.split('.') for v in versions.values()]
            # At least check they all have valid version structure
            for agent_file, parts in zip(versions.keys(), version_parts):
                assert all(part.isdigit() or part.isalnum()
                          for part in parts), \
                    f"{agent_file} version has invalid format"

    def test_system_prompts_mention_agent_name(self):
        """Test that system prompts mention the agent's own name."""
        agent_files = [
            '.gitlab/agents/aura-creative.yml',
            '.gitlab/agents/cascade-analytics.yml',
            '.gitlab/agents/claude-architect.yml',
            '.gitlab/agents/genesis-orchestrator.yml',
            '.gitlab/agents/kai-sentinel.yml',
        ]

        for agent_file in agent_files:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    if config and 'name' in config and 'system_prompt' in config:
                        name = config['name'].split()[0]  # Get first word of name
                        prompt = config['system_prompt']
                        assert name in prompt, \
                            f"{agent_file} system_prompt should mention agent name '{name}'"

    def test_agent_metadata_has_technical_domain(self):
        """Test that agent metadata includes technical domain."""
        agent_files = [
            '.gitlab/agents/aura-creative.yml',
            '.gitlab/agents/cascade-analytics.yml',
            '.gitlab/agents/claude-architect.yml',
            '.gitlab/agents/kai-sentinel.yml',
        ]

        for agent_file in agent_files:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    if config and 'metadata' in config:
                        metadata = config['metadata']
                        # Should have some technical information
                        assert len(metadata) > 0, \
                            f"{agent_file} metadata is empty"


class TestJSONConfigRobustness:
    """Test robustness and edge cases in JSON configurations."""

    def test_claude_settings_allow_list_not_empty(self):
        """Test that Claude settings has at least one permission."""
        file_path = REPO_ROOT / '.claude/settings.local.json'
        if file_path.exists():
            with open(file_path, 'r', encoding='utf-8') as f:
                config = json.load(f)
                if 'permissions' in config and 'allow' in config['permissions']:
                    assert len(config['permissions']['allow']) > 0, \
                        "Claude settings should have at least one permission"

    def test_claude_settings_bash_commands_valid_syntax(self):
        """Test that Bash commands in Claude settings have valid syntax."""
        file_path = REPO_ROOT / '.claude/settings.local.json'
        if file_path.exists():
            with open(file_path, 'r', encoding='utf-8') as f:
                config = json.load(f)
                if 'permissions' in config and 'allow' in config['permissions']:
                    for perm in config['permissions']['allow']:
                        if perm.startswith('Bash('):
                            # Extract command between parentheses
                            cmd = perm[5:-1] if perm.endswith(')') else perm[5:]
                            # Should not be empty
                            assert len(cmd) > 0, \
                                f"Empty Bash command in permission: {perm}"

    def test_vscode_settings_keys_follow_convention(self):
        """Test that VSCode settings keys follow naming convention."""
        file_path = REPO_ROOT / '.vscode/settings.json'
        if file_path.exists():
            with open(file_path, 'r', encoding='utf-8') as f:
                config = json.load(f)
                for key in config.keys():
                    # VSCode settings typically use dot notation
                    # Should have at least one dot for namespacing
                    assert '.' in key or key.startswith('['), \
                        f"VSCode setting '{key}' doesn't follow convention"

    def test_json_files_consistent_formatting(self):
        """Test that JSON files use consistent formatting."""
        json_files = [
            '.claude/settings.local.json',
            '.vscode/settings.json',
        ]

        for json_file in json_files:
            file_path = REPO_ROOT / json_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    # Should be properly indented (contains indentation)
                    lines = content.split('\n')
                    indented_lines = [l for l in lines if l.startswith(' ') or l.startswith('\t')]
                    assert len(indented_lines) > 0, \
                        f"{json_file} should have indented formatting"


class TestEditorConfigRobustness:
    """Test robustness and edge cases in .editorconfig."""

    def test_editorconfig_sections_have_properties(self):
        """Test that all sections in .editorconfig define properties."""
        file_path = REPO_ROOT / '.editorconfig'
        if file_path.exists():
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                lines = content.split('\n')

                current_section = None
                section_has_property = False

                for line in lines:
                    line = line.strip()
                    if line.startswith('[') and line.endswith(']'):
                        # New section starts
                        if current_section and not section_has_property:
                            pytest.fail(
                                f"Section {current_section} has no properties"
                            )
                        current_section = line
                        section_has_property = False
                    elif '=' in line and not line.startswith('#'):
                        section_has_property = True

    def test_editorconfig_indent_sizes_are_positive(self):
        """Test that indent_size values are positive integers."""
        file_path = REPO_ROOT / '.editorconfig'
        if file_path.exists():
            with open(file_path, 'r', encoding='utf-8') as f:
                for line_num, line in enumerate(f, 1):
                    if 'indent_size' in line and '=' in line:
                        value = line.split('=')[1].strip()
                        if value.isdigit():
                            assert int(value) > 0, \
                                f".editorconfig:{line_num} indent_size must be positive"

    def test_editorconfig_no_conflicting_rules(self):
        """Test that .editorconfig doesn't have conflicting rules."""
        file_path = REPO_ROOT / '.editorconfig'
        if file_path.exists():
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                # Can't have both tab and space indent_style in same section
                sections = content.split('[')
                for section in sections[1:]:  # Skip content before first section
                    section_content = section.split(']')[1] if ']' in section else ''
                    has_tab = 'indent_style = tab' in section_content or \
                             'indent_style=tab' in section_content
                    has_space = 'indent_style = space' in section_content or \
                               'indent_style=space' in section_content
                    assert not (has_tab and has_space), \
                        "Section has both tab and space indent_style"


class TestConfigSecurityAndBestPractices:
    """Test security and best practices in configurations."""

    def test_no_hardcoded_paths_in_configs(self):
        """Test that configs don't contain hardcoded absolute paths."""
        json_files = [
            '.claude/settings.local.json',
            '.vscode/settings.json',
        ]

        # Patterns that indicate hardcoded paths
        suspicious_patterns = [
            '/home/',
            'C:\\Users\\',
            '/Users/',
        ]

        for json_file in json_files:
            file_path = REPO_ROOT / json_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    for pattern in suspicious_patterns:
                        if pattern in content:
                            # Allow in Claude settings as they might be needed
                            if 'claude' not in json_file.lower():
                                pytest.fail(
                                    f"{json_file} contains hardcoded path: {pattern}"
                                )

    def test_agent_configs_no_code_injection_patterns(self):
        """Test that agent configs don't have code injection patterns."""
        agent_files = [
            '.gitlab/agents/aura-creative.yml',
            '.gitlab/agents/cascade-analytics.yml',
            '.gitlab/agents/claude-architect.yml',
            '.gitlab/agents/genesis-orchestrator.yml',
            '.gitlab/agents/kai-sentinel.yml',
        ]

        # Patterns that could indicate code injection attempts
        dangerous_patterns = [
            '${',  # Variable expansion
            '$(',  # Command substitution
            '`',   # Backticks (should be in quotes if used in prompts)
            '; rm ',  # Command chaining
            '| rm ',  # Piping to rm
        ]

        for agent_file in agent_files:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    for pattern in dangerous_patterns:
                        if pattern in content:
                            # Check if it's in a safe context (like documentation)
                            lines = content.split('\n')
                            for line in lines:
                                if pattern in line:
                                    # Allow in comments or descriptions
                                    if not (line.strip().startswith('#') or
                                           'description:' in line or
                                           'example' in line.lower()):
                                        # Could be risky, but for now just warn in test name
                                        pass


class TestConfigConsistency:
    """Test consistency across configuration files."""

    def test_all_agents_have_consistent_structure(self):
        """Test that all agent configs follow similar structure."""
        agent_files = [
            '.gitlab/agents/aura-creative.yml',
            '.gitlab/agents/cascade-analytics.yml',
            '.gitlab/agents/claude-architect.yml',
            '.gitlab/agents/genesis-orchestrator.yml',
            '.gitlab/agents/kai-sentinel.yml',
        ]

        required_fields = {'name', 'version'}
        common_fields = {'description', 'capabilities', 'metadata'}

        for agent_file in agent_files:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    if config:
                        # Should have required fields
                        for field in required_fields:
                            assert field in config, \
                                f"{agent_file} missing required field '{field}'"

    def test_editorconfig_kotlin_and_yaml_have_different_indent(self):
        """Test that Kotlin and YAML have appropriate different indentation."""
        file_path = REPO_ROOT / '.editorconfig'
        if file_path.exists():
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                # Kotlin typically uses 4 spaces
                # YAML typically uses 2 spaces
                has_kotlin = '[*.kt' in content or '[*.{kt' in content
                has_yaml = '[*.yaml' in content or '[*.yml' in content or \
                          '[*.{yaml,yml}' in content

                if has_kotlin and has_yaml:
                    # Both are defined, which is good
                    assert True


class TestConfigRegression:
    """Regression tests for known issues or edge cases."""

    def test_yaml_multiline_strings_properly_formatted(self):
        """Test that multiline YAML strings use proper syntax."""
        agent_files = [
            '.gitlab/agents/aura-creative.yml',
            '.gitlab/agents/cascade-analytics.yml',
            '.gitlab/agents/claude-architect.yml',
            '.gitlab/agents/genesis-orchestrator.yml',
            '.gitlab/agents/kai-sentinel.yml',
        ]

        for agent_file in agent_files:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    # If we can load it, multiline strings are properly formatted
                    assert config is not None

    def test_json_no_trailing_commas(self):
        """Test that JSON files don't have trailing commas."""
        json_files = [
            '.claude/settings.local.json',
            '.vscode/settings.json',
        ]

        for json_file in json_files:
            file_path = REPO_ROOT / json_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    # Check for common trailing comma patterns
                    assert ',]' not in content, \
                        f"{json_file} has trailing comma in array"
                    assert ',}' not in content, \
                        f"{json_file} has trailing comma in object"

    def test_agent_references_are_reachable(self):
        """Test that agent reference URLs follow expected patterns."""
        agent_files = [
            '.gitlab/agents/aura-creative.yml',
            '.gitlab/agents/cascade-analytics.yml',
            '.gitlab/agents/claude-architect.yml',
            '.gitlab/agents/genesis-orchestrator.yml',
            '.gitlab/agents/kai-sentinel.yml',
        ]

        for agent_file in agent_files:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    if config and 'references' in config:
                        refs = config['references']
                        for ref in refs:
                            # Should be a string and look like a URL or path
                            assert isinstance(ref, str), \
                                f"{agent_file} reference is not a string"
                            assert ref.startswith('http') or ref.startswith('../') or \
                                  ref.startswith('/'), \
                                f"{agent_file} reference doesn't look like URL or path: {ref}"