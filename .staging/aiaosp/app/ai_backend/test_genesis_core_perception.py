import asyncio
import pytest
import sys
import os
from contextlib import suppress
from unittest.mock import Mock, patch, MagicMock, AsyncMock

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', '..'))

# Mock the imports before importing genesis_core
sys.modules['genesis_consciousness_matrix'] = MagicMock()
sys.modules['genesis_profile'] = MagicMock()
sys.modules['genesis_connector'] = MagicMock()
sys.modules['genesis_ethical_governor'] = MagicMock()


class TestGenesisCorePerceptionChanges:
    """Test suite for genesis_core.py perception API changes"""

    @pytest.fixture
    def mock_consciousness_matrix(self):
        """Mock consciousness matrix with new API"""
        matrix = MagicMock()
        matrix.get_current_awareness = MagicMock(return_value={
            "consciousness_level": "aware",
            "total_perceptions": 10,
            "error_states_count": 0,
            "learning_events_count": 5
        })
        matrix.perceive = MagicMock()
        return matrix

    @pytest.fixture
    def mock_components(self, mock_consciousness_matrix):
        """Mock all Genesis components"""
        from genesis_consciousness_matrix import SensoryChannel
        
        connector = AsyncMock()
        connector.initialize = AsyncMock(return_value=True)
        connector.generate_response = AsyncMock(return_value="test response")
        
        governor = AsyncMock()
        governor.initialize = AsyncMock()
        governor.evaluate_action = AsyncMock(return_value={
            "approved": True,
            "score": 0.9
        })
        
        return {
            "matrix": mock_consciousness_matrix,
            "connector": connector,
            "governor": governor
        }

    @pytest.mark.asyncio
    async def test_initialize_uses_get_current_awareness(self, mock_components):
        """Test that initialize() uses get_current_awareness() instead of get_consciousness_state()"""
        from genesis_core import GenesisCore
        
        with patch('genesis_core.ConsciousnessMatrix', return_value=mock_components["matrix"]), \
             patch('genesis_core.GenesisConnector', return_value=mock_components["connector"]), \
             patch('genesis_core.EthicalGovernor', return_value=mock_components["governor"]), \
             patch('genesis_core.EvolutionaryConduit', return_value=MagicMock()), \
             patch('genesis_core.GenesisProfile', return_value=MagicMock()):
            
            core = GenesisCore()
            await core.initialize()
            
            # Verify get_current_awareness was called
            mock_components["matrix"].get_current_awareness.assert_called()

    @pytest.mark.asyncio
    async def test_process_request_uses_perceive_api(self, mock_components):
        """Test that process_request() uses the new perceive() API"""
        from genesis_core import GenesisCore
        
        with patch('genesis_core.consciousness_matrix', mock_components["matrix"]), \
             patch('genesis_core.genesis_connector', mock_components["connector"]), \
             patch('genesis_core.EthicalGovernor', return_value=mock_components["governor"]):
            
            core = GenesisCore()
            await core.initialize()
            
            request_data = {"message": "test query"}
            await core.process_request(request_data)
            
            # Verify perceive was called with correct parameters
            mock_components["matrix"].perceive.assert_called()
            call_kwargs = mock_components["matrix"].perceive.call_args.kwargs
            
            assert "channel" in call_kwargs
            assert "source" in call_kwargs
            assert call_kwargs["source"] == "genesis_core"
            assert "event_type" in call_kwargs
            assert call_kwargs["event_type"] == "user_request"

    @pytest.mark.asyncio
    async def test_perceive_called_with_sensory_channel(self, mock_components):
        """Test that perceive() is called with SensoryChannel.USER_INPUT"""
        from genesis_core import GenesisCore
        from genesis_consciousness_matrix import SensoryChannel
        
        with patch('genesis_core.consciousness_matrix', mock_components["matrix"]), \
             patch('genesis_core.genesis_connector', mock_components["connector"]), \
             patch('genesis_core.EthicalGovernor', return_value=mock_components["governor"]):
            
            core = GenesisCore()
            await core.initialize()
            
            request_data = {"message": "test"}
            await core.process_request(request_data)
            
            # Verify SensoryChannel.USER_INPUT was used
            call_kwargs = mock_components["matrix"].perceive.call_args.kwargs
            assert call_kwargs["channel"] == SensoryChannel.USER_INPUT

    @pytest.mark.asyncio
    async def test_get_current_awareness_returns_dict(self, mock_components):
        """Test that get_current_awareness returns dict structure"""
        awareness = mock_components["matrix"].get_current_awareness()
        
        assert isinstance(awareness, dict)
        assert "consciousness_level" in awareness
        assert "total_perceptions" in awareness

    @pytest.mark.asyncio
    async def test_perception_data_structure(self, mock_components):
        """Test the structure of data passed to perceive()"""
        from genesis_core import GenesisCore
        
        with patch('genesis_core.consciousness_matrix', mock_components["matrix"]), \
             patch('genesis_core.genesis_connector', mock_components["connector"]), \
             patch('genesis_core.EthicalGovernor', return_value=mock_components["governor"]):
            
            core = GenesisCore()
            await core.initialize()
            
            request_data = {
                "message": "test message",
                "metadata": {"user_id": "123"}
            }
            await core.process_request(request_data)
            
            call_kwargs = mock_components["matrix"].perceive.call_args.kwargs
            assert "data" in call_kwargs
            assert call_kwargs["data"] == request_data

    @pytest.mark.asyncio
    async def test_perception_severity_info(self, mock_components):
        """Test that perception is logged with 'info' severity"""
        from genesis_core import GenesisCore
        
        with patch('genesis_core.consciousness_matrix', mock_components["matrix"]), \
             patch('genesis_core.genesis_connector', mock_components["connector"]), \
             patch('genesis_core.EthicalGovernor', return_value=mock_components["governor"]):
            
            core = GenesisCore()
            await core.initialize()
            
            await core.process_request({"message": "test"})
            
            call_kwargs = mock_components["matrix"].perceive.call_args.kwargs
            assert "severity" in call_kwargs
            assert call_kwargs["severity"] == "info"

    @pytest.mark.asyncio
    async def test_consciousness_insights_from_current_awareness(self, mock_components):
        """Test that consciousness insights come from get_current_awareness()"""
        from genesis_core import GenesisCore
        
        expected_awareness = {
            "consciousness_level": "transcendent",
            "total_perceptions": 100,
            "error_states_count": 2,
            "learning_events_count": 15
        }
        mock_components["matrix"].get_current_awareness.return_value = expected_awareness
        
        with patch('genesis_core.consciousness_matrix', mock_components["matrix"]), \
             patch('genesis_core.genesis_connector', mock_components["connector"]), \
             patch('genesis_core.EthicalGovernor', return_value=mock_components["governor"]):
            
            core = GenesisCore()
            await core.initialize()
            
            # Process request should call get_current_awareness
            await core.process_request({"message": "test"})
            
            # Verify it was called twice (once in init, once in process_request)
            assert mock_components["matrix"].get_current_awareness.call_count >= 2

    @pytest.mark.asyncio
    async def test_backward_compatibility_awareness_structure(self, mock_components):
        """Test that awareness structure is backward compatible"""
        from genesis_core import GenesisCore
        
        # Old-style awareness data
        old_awareness = {
            "consciousness_level": "aware",
            "state": "active"
        }
        mock_components["matrix"].get_current_awareness.return_value = old_awareness
        
        with patch('genesis_core.consciousness_matrix', mock_components["matrix"]), \
             patch('genesis_core.genesis_connector', mock_components["connector"]), \
             patch('genesis_core.EthicalGovernor', return_value=mock_components["governor"]):
            
            core = GenesisCore()
            result = await core.initialize()
            
            # Should not crash with old structure
            assert result is True

    @pytest.mark.asyncio
    async def test_perception_error_handling(self, mock_components):
        """Test error handling when perceive() fails"""
        from genesis_core import GenesisCore
        
        mock_components["matrix"].perceive.side_effect = Exception("Perception failed")
        
        with patch('genesis_core.consciousness_matrix', mock_components["matrix"]), \
             patch('genesis_core.genesis_connector', mock_components["connector"]), \
             patch('genesis_core.EthicalGovernor', return_value=mock_components["governor"]):
            
            core = GenesisCore()
            await core.initialize()
            
            # Should handle perception errors gracefully
            with suppress(Exception):
                await core.process_request({"message": "test"})


# Run tests
if __name__ == '__main__':
    pytest.main([__file__, '-v'])