import requests
import json

BASE = "http://localhost:5000"


def test_health():
    r = requests.get(f"{BASE}/health")
    assert r.status_code == 200
    data = r.json()
    assert "status" in data


def test_genesis_chat():
    payload = {"message": "Integration test ping", "user_id": "integration_test"}
    r = requests.post(f"{BASE}/genesis/chat", json=payload, timeout=10)
    assert r.status_code == 200
    data = r.json()
    # Expect either 'response' or 'message' key containing text
    assert isinstance(data, dict)
    assert ("response" in data) or ("message" in data)


if __name__ == "__main__":
    test_health()
    test_genesis_chat()
    print("Integration tests passed")

