"""
Configuration for comprehensive GenesisConnector tests.
"""

# Test configuration constants
TEST_CONFIG = {
    'api_key': 'test_api_key_123',
    'base_url': 'https://api.genesis.test',
    'timeout': 30,
    'max_retries': 3
}

# Test data for various scenarios
TEST_PAYLOADS = {
    'simple': {'message': 'Hello, World!'},
    'complex': {
        'nested': {
            'data': [1, 2, 3],
            'metadata': {'created': '2023-01-01'}
        }
    },
    'unicode': {'message': 'Hello, 世界! 🌍'},
    'large': {'content': 'A' * 10000},
    'empty': {},
    'null_values': {'field': None, 'empty': ''},
    'boolean': {'enabled': True, 'disabled': False},
    'mixed_types': {
        'string': 'text',
        'number': 42,
        'boolean': True,
        'null': None,
        'array': [1, 2, 3],
        'object': {'nested': 'value'}
    }
}

# HTTP status codes for testing
HTTP_STATUS_CODES = {
    'success': [200, 201, 202, 204],
    'client_error': [400, 401, 403, 404, 409, 422, 429],
    'server_error': [500, 501, 502, 503, 504]
}

# Error scenarios for testing
ERROR_SCENARIOS = [
    ('connection_error', 'Connection failed'),
    ('timeout_error', 'Request timed out'),
    ('ssl_error', 'SSL certificate verification failed'),
    ('dns_error', 'Name or service not known'),
    ('proxy_error', 'Proxy connection failed'),
    ('redirect_error', 'Too many redirects'),
    ('json_decode_error', 'Invalid JSON response'),
    ('encoding_error', 'Encoding error'),
]

# Content types for testing
CONTENT_TYPES = [
    'application/json',
    'application/json; charset=utf-8',
    'text/plain',
    'application/xml',
    'multipart/form-data',
    'application/octet-stream',
    'text/html',
    'application/x-www-form-urlencoded'
]

# API key formats for testing
API_KEY_FORMATS = [
    'simple_key',
    'key-with-dashes',
    'key_with_underscores',
    'key.with.dots',
    'key123with456numbers',
    'UPPERCASE_KEY',
    'MixedCase_Key',
    'very_long_' + 'x' * 100 + '_key',
    'key_with_special_chars!@#$%^&*()',
]

# URL formats for testing
URL_FORMATS = [
    'https://api.test.com',
    'https://api.test.com:8080',
    'https://api.test.com/v1',
    'https://api.test.com/v1/',
    'https://subdomain.api.test.com',
    'https://api-test.com',
    'http://localhost:8000',
    'https://127.0.0.1:8000',
]


# Test helpers
def create_mock_response(status_code=200, json_data=None, headers=None, text=None):
    """
    Create a mock HTTP response object for testing with customizable status code, headers, text, and JSON data.
    
    Parameters:
        status_code (int, optional): The HTTP status code to simulate. Defaults to 200.
        json_data (optional): The value returned by the mock's `json()` method. If not provided, calling `json()` raises a ValueError.
        headers (dict, optional): Headers to include in the mock response. Defaults to an empty dictionary.
        text (str, optional): The response text content. Defaults to an empty string.
    
    Returns:
        Mock: A mock object simulating an HTTP response, including `status_code`, `headers`, `text`, a `json()` method, and a `raise_for_status()` method that raises an exception for error status codes.
    """
    from unittest.mock import Mock

    mock_response = Mock()
    mock_response.status_code = status_code
    mock_response.headers = headers or {}
    mock_response.text = text or ''

    if json_data:
        mock_response.json.return_value = json_data
    else:
        mock_response.json.side_effect = ValueError("No JSON object could be decoded")

    if status_code >= 400:
        mock_response.raise_for_status.side_effect = Exception(f"{status_code} Error")
    else:
        mock_response.raise_for_status.return_value = None

    return mock_response


def create_test_connector(config_overrides=None):
    """
    Create and return a GenesisConnector instance for testing, using default test configuration merged with any provided overrides.
    
    Parameters:
        config_overrides (dict, optional): Configuration values to override the defaults for the test connector.
    
    Returns:
        GenesisConnector: A test instance initialized with the merged configuration.
    """
    config = TEST_CONFIG.copy()
    if config_overrides:
        config.update(config_overrides)

    try:
        from app.ai_backend.genesis_connector import GenesisConnector
    except ImportError:
        from genesis_connector import GenesisConnector

    return GenesisConnector(config)
