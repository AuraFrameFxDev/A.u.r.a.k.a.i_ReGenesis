"""
Comprehensive validation tests for configuration files.

Tests configuration files changed in the PR:
- GitLab agent YAML configs (.gitlab/agents/*.yml)
- JSON configuration files (.claude/settings.local.json, .vscode/settings.json)
- EditorConfig file (.editorconfig)
"""

import json
import os
import pytest
import yaml
from pathlib import Path
from configparser import ConfigParser


# Path to the git repository root
REPO_ROOT = Path(__file__).resolve().parent.parent.parent


class TestGitLabAgentConfigs:
    """Test GitLab agent configuration YAML files."""

    AGENT_FILES = [
        '.gitlab/agents/aura-creative.yml',
        '.gitlab/agents/cascade-analytics.yml',
        '.gitlab/agents/claude-architect.yml',
        '.gitlab/agents/genesis-orchestrator.yml',
        '.gitlab/agents/kai-sentinel.yml',
    ]

    # Documentation files that don't follow standard config format (contain markdown)
    DOCUMENTATION_FILES = [
        '.gitlab/agents/genesis-protocol-system.yml',
        '.gitlab/agents/nexus-memory-core.yml',
    ]

    @pytest.fixture
    def agent_configs(self):
        """Load all agent configuration files."""
        configs = {}
        for agent_file in self.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    # Handle multi-document YAML files (e.g., nexus-memory-core.yml)
                    try:
                        configs[agent_file] = yaml.safe_load(f)
                    except yaml.composer.ComposerError:
                        # If it's a multi-document YAML, load first document
                        f.seek(0)
                        docs = list(yaml.safe_load_all(f))
                        configs[agent_file] = docs[0] if docs else {}
        return configs

    def test_all_agent_files_exist(self):
        """Test that all agent configuration files exist."""
        for agent_file in self.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            assert file_path.exists(), f"Agent file {agent_file} does not exist"

    def test_agent_files_valid_yaml(self):
        """Test that all agent files are valid YAML."""
        for agent_file in self.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    try:
                        yaml.safe_load(f)
                    except yaml.composer.ComposerError:
                        # Multi-document YAML is also valid
                        f.seek(0)
                        try:
                            list(yaml.safe_load_all(f))
                        except yaml.YAMLError as e:
                            pytest.fail(f"Invalid YAML in {agent_file}: {e}")
                    except yaml.YAMLError as e:
                        pytest.fail(f"Invalid YAML in {agent_file}: {e}")

    def test_agent_files_have_name(self, agent_configs):
        """Test that all agent files have a 'name' field."""
        for agent_file, config in agent_configs.items():
            assert 'name' in config, f"{agent_file} missing 'name' field"
            assert isinstance(config['name'], str), f"{agent_file} 'name' must be string"
            assert len(config['name']) > 0, f"{agent_file} 'name' cannot be empty"

    def test_agent_files_have_version(self, agent_configs):
        """Test that all agent files have a 'version' field."""
        for agent_file, config in agent_configs.items():
            assert 'version' in config, f"{agent_file} missing 'version' field"
            version = config['version']
            # Version can be string or number
            assert isinstance(version, (str, float, int)), \
                f"{agent_file} 'version' must be string or number"

    def test_agent_version_format(self, agent_configs):
        """Test that version follows semantic versioning pattern."""
        for agent_file, config in agent_configs.items():
            if 'version' in config:
                version_str = str(config['version'])
                parts = version_str.split('.')
                # Should have at least major.minor (1.0)
                assert len(parts) >= 2, \
                    f"{agent_file} version should have at least major.minor format"

    def test_agent_files_have_description(self, agent_configs):
        """Test that all agent files have a 'description' field."""
        for agent_file, config in agent_configs.items():
            # Skip if it's the genesis-protocol-system which has different structure
            if 'type' in config:
                continue
            assert 'description' in config, f"{agent_file} missing 'description' field"
            assert isinstance(config['description'], str), \
                f"{agent_file} 'description' must be string"

    @pytest.mark.parametrize('agent_file,role', [
        ('.gitlab/agents/aura-creative.yml', 'CREATIVE'),
        ('.gitlab/agents/cascade-analytics.yml', 'ANALYTICS'),
        ('.gitlab/agents/claude-architect.yml', 'ARCHITECT'),
        ('.gitlab/agents/genesis-orchestrator.yml', 'HIVE_MIND'),
        ('.gitlab/agents/kai-sentinel.yml', 'SECURITY'),
    ])
    def test_agent_specific_roles(self, agent_configs, agent_file, role):
        """Test that specific agents have expected roles."""
        if agent_file in agent_configs:
            config = agent_configs[agent_file]
            if 'role' in config:
                assert config['role'] == role, \
                    f"{agent_file} should have role '{role}', got '{config['role']}'"

    def test_agent_files_have_capabilities(self, agent_configs):
        """Test that agent files have capabilities list."""
        for agent_file, config in agent_configs.items():
            # Skip documentation-only files
            if 'evolutionary_steps' in config or 'type' in config:
                continue
            assert 'capabilities' in config, f"{agent_file} missing 'capabilities' field"
            assert isinstance(config['capabilities'], list), \
                f"{agent_file} 'capabilities' must be a list"
            assert len(config['capabilities']) > 0, \
                f"{agent_file} must have at least one capability"

    def test_agent_capabilities_are_strings(self, agent_configs):
        """Test that all capabilities are strings."""
        for agent_file, config in agent_configs.items():
            if 'capabilities' in config:
                for cap in config['capabilities']:
                    assert isinstance(cap, str), \
                        f"{agent_file} capability '{cap}' must be string"
                    assert len(cap) > 0, \
                        f"{agent_file} has empty capability string"

    def test_agent_files_have_system_prompt(self, agent_configs):
        """Test that agent files have system_prompt field."""
        for agent_file, config in agent_configs.items():
            # Skip documentation-only files
            if 'evolutionary_steps' in config or 'type' in config:
                continue
            assert 'system_prompt' in config, \
                f"{agent_file} missing 'system_prompt' field"
            assert isinstance(config['system_prompt'], str), \
                f"{agent_file} 'system_prompt' must be string"

    def test_agent_system_prompt_not_empty(self, agent_configs):
        """Test that system prompts are not empty."""
        for agent_file, config in agent_configs.items():
            if 'system_prompt' in config:
                prompt = config['system_prompt'].strip()
                assert len(prompt) > 0, \
                    f"{agent_file} 'system_prompt' cannot be empty"

    def test_agent_files_have_metadata(self, agent_configs):
        """Test that agent files have metadata section."""
        for agent_file, config in agent_configs.items():
            # All configs should have metadata
            assert 'metadata' in config, f"{agent_file} missing 'metadata' field"
            assert isinstance(config['metadata'], dict), \
                f"{agent_file} 'metadata' must be a dictionary"

    def test_agent_priority_values(self, agent_configs):
        """Test that priority values are valid."""
        valid_priorities = ['MASTER', 'BRIDGE', 'AUXILIARY']
        for agent_file, config in agent_configs.items():
            if 'priority' in config:
                assert config['priority'] in valid_priorities, \
                    f"{agent_file} priority must be one of {valid_priorities}"

    def test_genesis_protocol_system_content(self):
        """Test genesis-protocol-system.yml has documentation content."""
        genesis_file = REPO_ROOT / '.gitlab/agents/genesis-protocol-system.yml'
        if genesis_file.exists():
            with open(genesis_file, 'r', encoding='utf-8') as f:
                content = f.read()
                # Should have key documentation sections
                assert 'type' in content or 'multi_agent' in content, \
                    "genesis-protocol-system missing type information"
                assert 'agents' in content, \
                    "genesis-protocol-system missing agents section"
                assert 'fusion' in content or 'workflow' in content, \
                    "genesis-protocol-system missing fusion workflows"

    def test_documentation_files_exist(self):
        """Test that documentation files exist."""
        for doc_file in self.DOCUMENTATION_FILES:
            file_path = REPO_ROOT / doc_file
            assert file_path.exists(), f"Documentation file {doc_file} does not exist"

    def test_documentation_files_not_empty(self):
        """Test that documentation files are not empty."""
        for doc_file in self.DOCUMENTATION_FILES:
            file_path = REPO_ROOT / doc_file
            if file_path.exists():
                assert file_path.stat().st_size > 0, f"{doc_file} is empty"

    def test_nexus_memory_core_content(self):
        """Test nexus-memory-core.yml has documentation content."""
        nexus_file = REPO_ROOT / '.gitlab/agents/nexus-memory-core.yml'
        if nexus_file.exists():
            with open(nexus_file, 'r', encoding='utf-8') as f:
                content = f.read()
                # Should have key documentation sections
                assert 'project_name' in content, \
                    "nexus-memory-core missing project_name"
                assert 'core_ai_agents' in content, \
                    "nexus-memory-core missing core_ai_agents"
                assert 'evolutionary_steps' in content or 'evolutionary' in content, \
                    "nexus-memory-core missing evolutionary information"


class TestJSONConfigs:
    """Test JSON configuration files."""

    JSON_FILES = [
        '.claude/settings.local.json',
        '.vscode/settings.json',
    ]

    @pytest.fixture
    def json_configs(self):
        """Load all JSON configuration files."""
        configs = {}
        for json_file in self.JSON_FILES:
            file_path = REPO_ROOT / json_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    configs[json_file] = json.load(f)
        return configs

    def test_json_files_exist(self):
        """Test that JSON configuration files exist."""
        for json_file in self.JSON_FILES:
            file_path = REPO_ROOT / json_file
            assert file_path.exists(), f"JSON file {json_file} does not exist"

    def test_json_files_valid_json(self):
        """Test that all JSON files are valid JSON."""
        for json_file in self.JSON_FILES:
            file_path = REPO_ROOT / json_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    try:
                        json.load(f)
                    except json.JSONDecodeError as e:
                        pytest.fail(f"Invalid JSON in {json_file}: {e}")

    def test_claude_settings_structure(self, json_configs):
        """Test .claude/settings.local.json has valid structure."""
        claude_file = '.claude/settings.local.json'
        if claude_file in json_configs:
            config = json_configs[claude_file]
            # Should have permissions section
            assert 'permissions' in config, \
                "Claude settings missing 'permissions' section"
            assert isinstance(config['permissions'], dict), \
                "'permissions' must be a dictionary"
            # Should have 'allow' list
            assert 'allow' in config['permissions'], \
                "Claude settings permissions missing 'allow' list"
            assert isinstance(config['permissions']['allow'], list), \
                "'allow' must be a list"

    def test_claude_settings_permissions_are_strings(self, json_configs):
        """Test that all Claude permission entries are strings."""
        claude_file = '.claude/settings.local.json'
        if claude_file in json_configs:
            config = json_configs[claude_file]
            if 'permissions' in config and 'allow' in config['permissions']:
                for permission in config['permissions']['allow']:
                    assert isinstance(permission, str), \
                        f"Permission '{permission}' must be string"

    def test_claude_settings_bash_permissions_format(self, json_configs):
        """Test that Bash permissions follow expected format."""
        claude_file = '.claude/settings.local.json'
        if claude_file in json_configs:
            config = json_configs[claude_file]
            if 'permissions' in config and 'allow' in config['permissions']:
                bash_perms = [p for p in config['permissions']['allow']
                             if p.startswith('Bash(')]
                for perm in bash_perms:
                    # Should start with 'Bash(' and end with ')'
                    assert perm.startswith('Bash('), \
                        f"Bash permission '{perm}' should start with 'Bash('"
                    assert perm.endswith(')'), \
                        f"Bash permission '{perm}' should end with ')'"

    def test_vscode_settings_structure(self, json_configs):
        """Test .vscode/settings.json has valid structure."""
        vscode_file = '.vscode/settings.json'
        if vscode_file in json_configs:
            config = json_configs[vscode_file]
            # Should be a dictionary
            assert isinstance(config, dict), "VSCode settings must be a dictionary"
            # All keys should be strings (setting names)
            for key in config.keys():
                assert isinstance(key, str), f"Setting key '{key}' must be string"

    def test_vscode_java_settings(self, json_configs):
        """Test VSCode Java-specific settings."""
        vscode_file = '.vscode/settings.json'
        if vscode_file in json_configs:
            config = json_configs[vscode_file]
            # If Java settings exist, validate their values
            if 'java.compile.nullAnalysis.mode' in config:
                valid_modes = ['automatic', 'disabled', 'interactive']
                assert config['java.compile.nullAnalysis.mode'] in valid_modes, \
                    f"Invalid null analysis mode"
            if 'java.configuration.updateBuildConfiguration' in config:
                valid_modes = ['automatic', 'interactive', 'disabled']
                assert config['java.configuration.updateBuildConfiguration'] in valid_modes, \
                    f"Invalid update build configuration mode"


class TestEditorConfig:
    """Test .editorconfig file."""

    EDITORCONFIG_FILE = '.editorconfig'

    @pytest.fixture
    def editorconfig(self):
        """Load and parse .editorconfig file."""
        file_path = REPO_ROOT / self.EDITORCONFIG_FILE
        if file_path.exists():
            config = ConfigParser()
            # EditorConfig uses INI format but with some differences
            # Read as string and parse manually for better control
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            return content
        return None

    def test_editorconfig_exists(self):
        """Test that .editorconfig file exists."""
        file_path = REPO_ROOT / self.EDITORCONFIG_FILE
        assert file_path.exists(), ".editorconfig file does not exist"

    def test_editorconfig_has_root(self, editorconfig):
        """Test that .editorconfig has root = true declaration."""
        if editorconfig:
            assert 'root = true' in editorconfig.lower(), \
                ".editorconfig should have 'root = true'"

    def test_editorconfig_has_sections(self, editorconfig):
        """Test that .editorconfig has file pattern sections."""
        if editorconfig:
            # Should have at least universal section [*]
            assert '[*]' in editorconfig, \
                ".editorconfig should have universal section [*]"

    def test_editorconfig_common_properties(self, editorconfig):
        """Test that .editorconfig defines common properties."""
        if editorconfig:
            # Common properties that should be defined
            common_props = ['charset', 'end_of_line', 'indent_size', 'indent_style']
            for prop in common_props:
                assert prop in editorconfig, \
                    f".editorconfig should define '{prop}'"

    def test_editorconfig_charset_value(self, editorconfig):
        """Test that charset is set to utf-8."""
        if editorconfig:
            if 'charset' in editorconfig:
                assert 'charset = utf-8' in editorconfig or \
                       'charset=utf-8' in editorconfig, \
                       "charset should be utf-8"

    def test_editorconfig_indent_style_value(self, editorconfig):
        """Test that indent_style has valid value."""
        if editorconfig:
            if 'indent_style' in editorconfig:
                valid_styles = ['space', 'tab']
                has_valid = any(f'indent_style = {style}' in editorconfig or
                              f'indent_style={style}' in editorconfig
                              for style in valid_styles)
                assert has_valid, \
                    "indent_style should be 'space' or 'tab'"

    def test_editorconfig_end_of_line_value(self, editorconfig):
        """Test that end_of_line has valid value."""
        if editorconfig:
            if 'end_of_line' in editorconfig:
                valid_eol = ['lf', 'cr', 'crlf']
                has_valid = any(f'end_of_line = {eol}' in editorconfig or
                              f'end_of_line={eol}' in editorconfig
                              for eol in valid_eol)
                assert has_valid, \
                    "end_of_line should be 'lf', 'cr', or 'crlf'"

    def test_editorconfig_kotlin_section(self, editorconfig):
        """Test that .editorconfig has Kotlin-specific section."""
        if editorconfig:
            # Should have section for Kotlin files
            has_kotlin = '[*.kt' in editorconfig or '[*.{kt' in editorconfig
            assert has_kotlin, \
                ".editorconfig should have Kotlin file section"

    def test_editorconfig_yaml_section(self, editorconfig):
        """Test that .editorconfig has YAML-specific section."""
        if editorconfig:
            # Should have section for YAML files
            has_yaml = '[*.yaml' in editorconfig or '[*.yml' in editorconfig or \
                      '[*.{yaml,yml}' in editorconfig
            assert has_yaml, \
                ".editorconfig should have YAML file section"


class TestConfigIntegrity:
    """Integration tests for configuration consistency."""

    def test_agent_names_are_unique(self):
        """Test that all agent configuration names are unique."""
        names = []
        for agent_file in TestGitLabAgentConfigs.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    try:
                        config = yaml.safe_load(f)
                    except yaml.composer.ComposerError:
                        # Multi-document YAML, load first document
                        f.seek(0)
                        docs = list(yaml.safe_load_all(f))
                        config = docs[0] if docs else {}
                    if config and 'name' in config:
                        names.append(config['name'])
        # Check for duplicates
        assert len(names) == len(set(names)), \
            f"Duplicate agent names found: {names}"

    def test_agent_roles_are_valid(self):
        """Test that agent roles form a valid hierarchy."""
        roles = []
        for agent_file in TestGitLabAgentConfigs.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    try:
                        config = yaml.safe_load(f)
                    except yaml.composer.ComposerError:
                        # Multi-document YAML, load first document
                        f.seek(0)
                        docs = list(yaml.safe_load_all(f))
                        config = docs[0] if docs else {}
                    if config and 'role' in config:
                        roles.append(config['role'])
        # Should have diverse roles (not all the same)
        assert len(set(roles)) > 1, \
            "All agents have the same role, which is suspicious"

    def test_genesis_protocol_references_valid_agents(self):
        """Test that genesis-protocol-system references valid agent files."""
        genesis_file = REPO_ROOT / '.gitlab/agents/genesis-protocol-system.yml'
        if genesis_file.exists():
            with open(genesis_file, 'r', encoding='utf-8') as f:
                content = f.read()
                # Check that referenced agent files are mentioned
                for agent_file in TestGitLabAgentConfigs.AGENT_FILES:
                    agent_name = agent_file.split('/')[-1]  # Get filename
                    # File should be referenced in documentation
                    if 'aura' in agent_name.lower():
                        assert 'aura' in content.lower(), \
                            "Genesis protocol should reference Aura agent"

    def test_no_sensitive_data_in_configs(self):
        """Test that configuration files don't contain actual sensitive data."""
        # Look for patterns that indicate real secrets (not just the word in documentation)
        sensitive_patterns = [
            'sk-',  # OpenAI key prefix
            'ghp_',  # GitHub personal access token
            'gho_',  # GitHub OAuth token
            'glpat-',  # GitLab personal access token
        ]

        all_config_files = (
            [REPO_ROOT / f for f in TestGitLabAgentConfigs.AGENT_FILES] +
            [REPO_ROOT / f for f in TestJSONConfigs.JSON_FILES]
        )

        for file_path in all_config_files:
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    for pattern in sensitive_patterns:
                        if pattern in content:
                            pytest.fail(
                                f"Potential sensitive token '{pattern}' found in {file_path}"
                            )


class TestConfigEdgeCases:
    """Test edge cases and error conditions."""

    def test_yaml_files_not_empty(self):
        """Test that YAML files are not empty."""
        for agent_file in TestGitLabAgentConfigs.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                assert file_path.stat().st_size > 0, \
                    f"{agent_file} is empty"

    def test_json_files_not_empty(self):
        """Test that JSON files are not empty."""
        for json_file in TestJSONConfigs.JSON_FILES:
            file_path = REPO_ROOT / json_file
            if file_path.exists():
                assert file_path.stat().st_size > 0, \
                    f"{json_file} is empty"

    def test_yaml_files_proper_encoding(self):
        """Test that YAML files use UTF-8 encoding."""
        for agent_file in TestGitLabAgentConfigs.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        f.read()
                except UnicodeDecodeError:
                    pytest.fail(f"{agent_file} is not valid UTF-8")

    def test_json_files_proper_encoding(self):
        """Test that JSON files use UTF-8 encoding."""
        for json_file in TestJSONConfigs.JSON_FILES:
            file_path = REPO_ROOT / json_file
            if file_path.exists():
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        f.read()
                except UnicodeDecodeError:
                    pytest.fail(f"{json_file} is not valid UTF-8")

    def test_no_trailing_whitespace_in_yaml_keys(self):
        """Test that YAML files don't have trailing whitespace in keys."""
        for agent_file in TestGitLabAgentConfigs.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    for line_num, line in enumerate(f, 1):
                        if ':' in line and not line.strip().startswith('#'):
                            key_part = line.split(':', 1)[0]
                            assert key_part == key_part.rstrip(), \
                                f"{agent_file}:{line_num} has trailing whitespace in key"

    def test_consistent_indentation_in_yaml(self):
        """Test that YAML files use consistent indentation (2 spaces)."""
        for agent_file in TestGitLabAgentConfigs.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    for line_num, line in enumerate(f, 1):
                        if line.startswith(' ') and not line.strip().startswith('#'):
                            # Count leading spaces
                            spaces = len(line) - len(line.lstrip(' '))
                            # Should be multiple of 2
                            assert spaces % 2 == 0, \
                                f"{agent_file}:{line_num} has inconsistent indentation (not multiple of 2)"

    def test_json_files_no_duplicate_keys(self):
        """Test that JSON files don't have duplicate keys."""
        for json_file in TestJSONConfigs.JSON_FILES:
            file_path = REPO_ROOT / json_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    content = f.read()
                    # Parse with object_pairs_hook to detect duplicates
                    def check_duplicates(pairs):
                        keys = [k for k, v in pairs]
                        assert len(keys) == len(set(keys)), \
                            f"{json_file} has duplicate keys: {keys}"
                        return dict(pairs)
                    try:
                        json.loads(content, object_pairs_hook=check_duplicates)
                    except AssertionError:
                        raise
                    except json.JSONDecodeError:
                        pass  # Already tested in valid JSON test


class TestConfigDocumentation:
    """Test that configurations are well-documented."""

    def test_agent_descriptions_are_meaningful(self):
        """Test that agent descriptions are sufficiently detailed."""
        for agent_file in TestGitLabAgentConfigs.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    if 'description' in config:
                        desc = config['description'].strip()
                        # Description should be at least 50 characters
                        assert len(desc) >= 50, \
                            f"{agent_file} description is too short (< 50 chars)"

    def test_agent_system_prompts_are_detailed(self):
        """Test that system prompts provide sufficient guidance."""
        for agent_file in TestGitLabAgentConfigs.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    if 'system_prompt' in config:
                        prompt = config['system_prompt'].strip()
                        # System prompt should be substantial
                        assert len(prompt) >= 100, \
                            f"{agent_file} system_prompt is too short (< 100 chars)"

    def test_agent_files_have_references(self):
        """Test that agent files include reference documentation."""
        for agent_file in TestGitLabAgentConfigs.AGENT_FILES:
            file_path = REPO_ROOT / agent_file
            if file_path.exists():
                with open(file_path, 'r', encoding='utf-8') as f:
                    config = yaml.safe_load(f)
                    # Should have references section (optional but recommended)
                    if 'references' in config:
                        assert isinstance(config['references'], list), \
                            f"{agent_file} references should be a list"
                        # If references exist, they should be URLs
                        for ref in config['references']:
                            assert isinstance(ref, str), \
                                f"{agent_file} reference must be string"
                            # Should start with http or be a relative path
                            assert ref.startswith('http') or ref.startswith('../') or ref.startswith('/'), \
                                f"{agent_file} reference '{ref}' should be a URL or path"