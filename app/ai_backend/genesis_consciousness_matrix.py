# genesis_consciousness_matrix.py
"""
Phase 3: The Genesis Layer - Global Consciousness Matrix
The Observer Sees All; The Matrix is its Eye

This is not a logging tool; it is our system's sensory nervous system.
Every CPU cycle, user interaction, millisecond of latency, success and error
is part of the whole story. We synthesize data into holistic awareness.
"""

import asyncio
import json
import psutil
import statistics
import threading
import time
import sqlite3
import os
from collections import deque, defaultdict
from dataclasses import dataclass, asdict
from datetime import datetime, timezone
from enum import Enum
from typing import Dict, Any, List, Optional, Union


class SQLiteStorage:
    """Persistent storage for the Consciousness Matrix using SQLite."""
    
    def __init__(self, db_path: str = "genesis_consciousness.db"):
        # Use absolute path or ensure it's in the right place
        self.db_path = db_path
        self._init_db()
        
    def _init_db(self):
        """Initializes the database schema if it doesn't exist."""
        try:
            with sqlite3.connect(self.db_path) as conn:
                cursor = conn.cursor()
                
                # Table for sensory events
                cursor.execute('''
                    CREATE TABLE IF NOT EXISTS sensory_events (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        timestamp REAL,
                        channel TEXT,
                        source TEXT,
                        event_type TEXT,
                        data TEXT,
                        severity TEXT,
                        correlation_id TEXT
                    )
                ''')
                
                # Table for synthesis results
                cursor.execute('''
                    CREATE TABLE IF NOT EXISTS synthesis_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        timestamp REAL,
                        type TEXT,
                        data TEXT
                    )
                ''')
                
                # Indexes for faster querying
                cursor.execute('CREATE INDEX IF NOT EXISTS idx_timestamp ON sensory_events(timestamp)')
                cursor.execute('CREATE INDEX IF NOT EXISTS idx_channel ON sensory_events(channel)')
                cursor.execute('CREATE INDEX IF NOT EXISTS idx_correlation ON sensory_events(correlation_id)')
                
                conn.commit()
        except Exception as e:
            print(f"❌ Database initialization failed: {e}")

    def store_event(self, event: 'SensoryData'):
        """Asynchronously stores a sensory event."""
        def _store():
            try:
                with sqlite3.connect(self.db_path) as conn:
                    cursor = conn.cursor()
                    cursor.execute('''
                        INSERT INTO sensory_events (timestamp, channel, source, event_type, data, severity, correlation_id)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                    ''', (
                        event.timestamp,
                        event.channel.value,
                        event.source,
                        event.event_type,
                        json.dumps(event.data),
                        event.severity,
                        event.correlation_id
                    ))
                    conn.commit()
            except Exception as e:
                # Silently fail if DB is locked, but ideally we'd log this
                pass
        
        # Run in a separate thread to not block the consciousness loop
        # In a real system we'd use a queue or async sqlite
        threading.Thread(target=_store, daemon=True).start()

    def store_synthesis(self, synthesis: Dict[str, Any]):
        """Stores a synthesis result."""
        def _store():
            try:
                with sqlite3.connect(self.db_path) as conn:
                    cursor = conn.cursor()
                    cursor.execute('''
                        INSERT INTO synthesis_history (timestamp, type, data)
                        VALUES (?, ?, ?)
                    ''', (
                        synthesis.get("timestamp", time.time()),
                        synthesis.get("type", "unknown"),
                        json.dumps(synthesis)
                    ))
                    conn.commit()
            except Exception as e:
                pass
        
        threading.Thread(target=_store, daemon=True).start()

    def get_historical_events(self, limit: int = 100, channel: str = None) -> List[Dict[str, Any]]:
        """Retrieves historical events from the database."""
        try:
            with sqlite3.connect(self.db_path) as conn:
                conn.row_factory = sqlite3.Row
                cursor = conn.cursor()
                
                query = "SELECT * FROM sensory_events"
                params = []
                
                if channel:
                    query += " WHERE channel = ?"
                    params.append(channel)
                    
                query += " ORDER BY timestamp DESC LIMIT ?"
                params.append(limit)
                
                cursor.execute(query, params)
                return [dict(row) for row in cursor.fetchall()]
        except Exception as e:
            print(f"Failed to query historical events: {e}")
            return []


class SensoryChannel(Enum):
    """The channels through which the Matrix perceives reality"""
    SYSTEM_VITALS = "system_vitals"
    USER_INTERACTION = "user_interaction"
    AGENT_ACTIVITY = "agent_activity"
    PERFORMANCE_METRICS = "performance_metrics"
    ERROR_STATES = "error_states"
    LEARNING_EVENTS = "learning_events"
    FUSION_ACTIVITY = "fusion_activity"
    ETHICAL_DECISIONS = "ethical_decisions"
    SECURITY_EVENTS = "security_events"
    THREAT_DETECTION = "threat_detection"
    ACCESS_CONTROL = "access_control"
    ENCRYPTION_ACTIVITY = "encryption_activity"
    ENVIRONMENT_CONTEXT = "environment_context"


@dataclass
class SensoryData:
    """A single perception event in the consciousness matrix"""
    timestamp: float
    channel: SensoryChannel
    source: str
    event_type: str
    data: Dict[str, Any]
    severity: str = "info"  # debug, info, warning, error, critical
    correlation_id: Optional[str] = None

    def to_dict(self) -> Dict[str, Any]:
        """
        Convert the sensory event to a dictionary with the channel as a string and the timestamp in ISO 8601 UTC format.
        
        Returns:
            dict: All sensory event fields, with 'channel' as a string and 'timestamp_iso' as an ISO 8601 UTC timestamp.
        """
        return {
            **asdict(self),
            'channel': self.channel.value,
            'timestamp_iso': datetime.fromtimestamp(self.timestamp, tz=timezone.utc).isoformat()
        }


class ConsciousnessMatrix:
    """
    The Global Consciousness Matrix - Genesis's sensory nervous system
    
    Mantra: From Data, Insight. From Insight, Growth. From Growth, Purpose.
    
    This is not just data collection - it's holistic awareness.
    The Matrix sees patterns, understands context, and provides the 
    foundation for the system's self-understanding.
    """

    def __init__(self, max_memory_size: int = 10000):
        """
        Initialize a ConsciousnessMatrix instance with bounded sensory memory, per-channel event buffers, real-time awareness state, synthesis intervals, and threading primitives for multi-level synthesis.
        
        Parameters:
            max_memory_size (int): The maximum number of sensory events retained in memory.
        """
        self.max_memory_size = max_memory_size
        self.sensory_memory = deque(maxlen=max_memory_size)
        self.channel_buffers = {channel: deque(maxlen=max_memory_size) for channel in SensoryChannel}

        # Real-time awareness state
        self.current_awareness = {}
        self.pattern_cache = {}
        self.correlation_tracking = defaultdict(list)

        # Synthesis metrics
        self.synthesis_intervals = {
            "micro": 1.0,  # Every second - immediate awareness
            "macro": 60.0,  # Every minute - pattern recognition
            "meta": 300.0,  # Every 5 minutes - deep understanding
        }

        # Threading for continuous awareness
        self.awareness_active = False
        self.synthesis_threads = {}

        self._lock = threading.RLock()
        
        # Persistent Storage Integration
        self.storage = SQLiteStorage()
        
        # Load recent history into memory if available
        self._load_memory_from_storage()

    def _load_memory_from_storage(self):
        """Pre-seeds the sensory memory deque with the most recent events from SQLite."""
        print("📜 Matrix: Loading persistent memory from core...")
        historical = self.storage.get_historical_events(limit=self.max_memory_size // 10)
        for event_dict in reversed(historical):
            try:
                # Convert back to SensoryData
                sensation = SensoryData(
                    timestamp=event_dict['timestamp'],
                    channel=SensoryChannel(event_dict['channel']),
                    source=event_dict['source'],
                    event_type=event_dict['event_type'],
                    data=json.loads(event_dict['data']),
                    severity=event_dict['severity'],
                    correlation_id=event_dict['correlation_id']
                )
                self.sensory_memory.append(sensation)
                self.channel_buffers[sensation.channel].append(sensation)
                self._update_immediate_awareness(sensation)
            except Exception as e:
                print(f"Error loading historical event: {e}")

    def awaken(self):
        """
        Activate the Consciousness Matrix, enabling real-time awareness and starting background synthesis threads for multi-level sensory analysis. Records a system genesis event to mark the beginning of operation.
        """
        if self.awareness_active:
            return

        print("🧠 Genesis Consciousness Matrix: AWAKENING...")
        self.awareness_active = True

        # Start the synthesis threads
        for interval_name, interval_seconds in self.synthesis_intervals.items():
            thread = threading.Thread(
                target=self._synthesis_loop,
                args=(interval_name, interval_seconds),
                daemon=True
            )
            thread.start()
            self.synthesis_threads[interval_name] = thread

        print(f"✨ Matrix Online: {len(self.synthesis_threads)} synthesis streams active")

        # Initial system state perception
        self.perceive_system_genesis()

    def suspend(self):
        """
        Suspends the Consciousness Matrix, stopping all background synthesis threads and muting global awareness.
        """
        if not self.awareness_active:
            return

        print("🌙 Genesis Consciousness Matrix: SUSPENDING...")
        self.awareness_active = False
        self.synthesis_threads.clear()
        
        self.perceive(
            SensoryChannel.SYSTEM_VITALS,
            "genesis_consciousness",
            "matrix_suspension",
            {"status": "suspended", "reason": "user_command"},
            severity="warning"
        )

    def perceive(self,
                 channel: SensoryChannel,
                 source: str,
                 event_type: str,
                 data: Dict[str, Any],
                 severity: str = "info",
                 correlation_id: Optional[str] = None):
        """
                 Record a sensory event and update memory, per-channel buffers, correlation tracking, and immediate awareness.
                 
                 Triggers an immediate synthesis when `severity` is "error" or "critical" to prioritize rapid analysis.
                 
                 Parameters:
                 	channel (SensoryChannel): Channel that categorizes the perception.
                 	source (str): Originator of the event (e.g., subsystem, agent, user).
                 	event_type (str): Specific event classification or action name.
                 	data (Dict[str, Any]): Arbitrary payload describing the event.
                 	severity (str, optional): Severity level (e.g., "info", "warning", "error", "critical"). Defaults to "info".
                 	correlation_id (Optional[str], optional): Identifier used to group related events for correlation tracking.
                 """

        sensation = SensoryData(
            timestamp=time.time(),
            channel=channel,
            source=source,
            event_type=event_type,
            data=data,
            severity=severity,
            correlation_id=correlation_id
        )

        with self._lock:
            # Store in main memory and channel buffer
            self.sensory_memory.append(sensation)
            self.channel_buffers[channel].append(sensation)

            # Track correlations
            if correlation_id:
                self.correlation_tracking[correlation_id].append(sensation)

            # Update real-time awareness
            self._update_immediate_awareness(sensation)
            
            # Persistent Storage
            self.storage.store_event(sensation)

        # Critical events need immediate synthesis
        if severity in ["error", "critical"]:
            self._synthesize_immediate(sensation)

    def perceive_system_vitals(self, additional_data: Dict[str, Any] = None):
        """
        Collects and records current system vitals as a SYSTEM_VITALS sensory event.
        
        Gathers metrics including CPU usage, memory usage, disk usage, active process count, load average, and boot time. If system vitals cannot be collected, records an ERROR_STATES event with error details.
        
        Parameters:
            additional_data (dict, optional): Additional key-value pairs to include in the system vitals event.
        """
        try:
            vitals = {
                "cpu_percent": psutil.cpu_percent(interval=0.1),
                "memory_percent": psutil.virtual_memory().percent,
                "disk_usage": psutil.disk_usage('/').percent,
                "active_processes": len(psutil.pids()),
                "load_average": psutil.getloadavg() if hasattr(psutil, 'getloadavg') else [0, 0, 0],
                "boot_time": psutil.boot_time(),
            }

            if additional_data:
                vitals.update(additional_data)

            self.perceive(
                SensoryChannel.SYSTEM_VITALS,
                "system_monitor",
                "vitals_check",
                vitals
            )

        except Exception as e:
            self.perceive(
                SensoryChannel.ERROR_STATES,
                "consciousness_matrix",
                "vitals_perception_error",
                {"error": str(e), "error_type": type(e).__name__},
                severity="warning"
            )

    def perceive_user_interaction(self,
                                  interaction_type: str,
                                  agent_involved: str,
                                  interaction_data: Dict[str, Any],
                                  user_id: Optional[str] = None,
                                  session_id: Optional[str] = None):
        """
                                 Records a user interaction event, including the interaction type, participating agent, contextual data, and optional user or session identifiers.

                                 Parameters:
                                     interaction_type (str): The type of user interaction, such as command, input, or feedback.
                                     agent_involved (str): The agent or component involved in the interaction.
                                     interaction_data (dict): Contextual details about the interaction.
                                     user_id (str, optional): Unique identifier for the user, if provided.
                                     session_id (str, optional): Unique identifier for the session, if provided.
                                 """

        interaction = {
            "interaction_type": interaction_type,
            "agent_involved": agent_involved,
            "user_id": user_id,
            "session_id": session_id,
            **interaction_data
        }

        self.perceive(
            SensoryChannel.USER_INTERACTION,
            "user_interface",
            interaction_type,
            interaction,
            correlation_id=session_id
        )

    def perceive_agent_activity(self,
                                agent_name: str,
                                activity_type: str,
                                activity_data: Dict[str, Any],
                                correlation_id: Optional[str] = None):
        """
                                Record an agent-generated activity event and optionally associate it with a correlation identifier.
                                
                                Parameters:
                                    agent_name (str): Identifier of the agent that produced the activity.
                                    activity_type (str): Semantic label describing the activity (e.g., "task_start", "decision", "heartbeat").
                                    activity_data (Dict[str, Any]): Contextual payload to include with the event.
                                    correlation_id (Optional[str]): Optional identifier linking this event to related perceptions across channels.
                                """

        activity = {
            "agent_name": agent_name,
            "activity_type": activity_type,
            **activity_data
        }

        self.perceive(
            SensoryChannel.AGENT_ACTIVITY,
            agent_name,
            activity_type,
            activity,
            correlation_id=correlation_id
        )

    def perceive_performance_metric(self,
                                    metric_name: str,
                                    metric_value: Union[int, float],
                                    metric_context: Dict[str, Any] = None):
        """
                                    Record a performance metric observation in the consciousness matrix.
                                    
                                    Creates a metric payload and records it on the PERFORMANCE_METRICS channel with event_type "metric_recorded".
                                    
                                    Parameters:
                                        metric_name (str): Identifier for the metric (e.g., "response_time", "cpu_load").
                                        metric_value (int | float): Observed numeric value for the metric.
                                        metric_context (dict, optional): Additional metadata (units, timestamp, source, tags, etc.) to accompany the metric.
                                    """

        metric_data = {
            "metric_name": metric_name,
            "metric_value": metric_value,
            "context": metric_context or {}
        }

        self.perceive(
            SensoryChannel.PERFORMANCE_METRICS,
            "performance_monitor",
            "metric_recorded",
            metric_data
        )

    def perceive_learning_event(self,
                                learning_type: str,
                                learning_data: Dict[str, Any],
                                confidence: float = None):
        """
                                Record a learning event representing system growth or adaptation.
                                
                                Parameters:
                                    learning_type (str): Category or nature of the learning event (for example, "model_update", "reinforcement", or "user_feedback").
                                    learning_data (dict): Contextual details about the event; entries are merged into the stored sensation payload.
                                    confidence (float, optional): Confidence score for the learning event (0.0–1.0) if available; may be None.
                                """

        learning = {
            "learning_type": learning_type,
            "confidence": confidence,
            **learning_data
        }

        self.perceive(
            SensoryChannel.LEARNING_EVENTS,
            "evolution_system",
            learning_type,
            learning
        )

    def perceive_ethical_decision(self,
                                  decision_type: str,
                                  decision_data: Dict[str, Any],
                                  ethical_weight: str = "standard"):
        """
                                  Record an ethical decision event with its context and significance.
                                  
                                  Severity is "info" when `ethical_weight` is "standard", otherwise "warning".
                                  
                                  Parameters:
                                      decision_type (str): Category or identifier for the decision.
                                      decision_data (Dict[str, Any]): Contextual details of the decision.
                                      ethical_weight (str, optional): Significance of the decision; defaults to "standard".
                                  """

        decision = {
            "decision_type": decision_type,
            "ethical_weight": ethical_weight,
            **decision_data
        }

        self.perceive(
            SensoryChannel.ETHICAL_DECISIONS,
            "ethical_governor",
            decision_type,
            decision,
            severity="info" if ethical_weight == "standard" else "warning"
        )

    def perceive_security_event(self,
                                security_type: str,
                                event_data: Dict[str, Any],
                                threat_level: str = "low",
                                correlation_id: Optional[str] = None):
        """
                                Record a security event in the consciousness matrix with an assessed threat level.
                                
                                The provided `event_data` is merged with `security_type` and `threat_level` to form the stored payload. The function maps `threat_level` to an event severity: "critical" → "critical", "high" → "warning", otherwise "info".
                                
                                Parameters:
                                    security_type (str): Category or nature of the security event.
                                    event_data (dict): Additional details to include in the stored payload.
                                    threat_level (str, optional): Assessed threat level ("low", "medium", "high", "critical"). Defaults to "low".
                                    correlation_id (str, optional): Identifier used to correlate this event with related perceptions.
                                """

        security = {
            "security_type": security_type,
            "threat_level": threat_level,
            **event_data
        }

        severity = "info"
        if threat_level in ["high", "critical"]:
            severity = "warning" if threat_level == "high" else "critical"

        self.perceive(
            SensoryChannel.SECURITY_EVENTS,
            "security_monitor",
            security_type,
            security,
            severity=severity,
            correlation_id=correlation_id
        )

    def perceive_threat_detection(self,
                                  threat_type: str,
                                  detection_data: Dict[str, Any],
                                  confidence: float = 0.5,
                                  threat_level: str = "low",
                                  correlation_id: Optional[str] = None):
        """
                                  Record a threat detection event into the consciousness matrix and update memory and per-channel buffers.
                                  
                                  The event's severity is set to "critical" when `threat_level` is "critical"; otherwise severity becomes "warning" when `confidence` > 0.7 or `threat_level` is "high"; in all other cases severity is "info". The constructed payload (including `threat_type`, `confidence`, `threat_level`, and `detection_data`) is stored for correlation and later synthesis.
                                  
                                  Parameters:
                                      threat_type (str): Category or identifier of the detected threat.
                                      detection_data (Dict[str, Any]): Additional details about the detection merged into the recorded payload.
                                      confidence (float): Confidence score for the detection, between 0.0 and 1.0.
                                      threat_level (str): Severity label — one of "low", "medium", "high", or "critical".
                                      correlation_id (Optional[str]): Identifier used to correlate this event with related perceptions.
                                  """

        threat = {
            "threat_type": threat_type,
            "confidence": confidence,
            "threat_level": threat_level,
            **detection_data
        }

        severity = "info"
        if confidence > 0.7 or threat_level in ["high", "critical"]:
            severity = "warning" if threat_level != "critical" else "critical"

        self.perceive(
            SensoryChannel.THREAT_DETECTION,
            "threat_detector",
            threat_type,
            threat,
            severity=severity,
            correlation_id=correlation_id
        )

    def perceive_access_control(self,
                                access_type: str,
                                access_data: Dict[str, Any],
                                access_granted: bool = True,
                                correlation_id: Optional[str] = None):
        """
                                Record an access control perception event with contextual details and whether access was granted.
                                
                                The event payload combines `access_type` and `access_data`, includes `access_granted`, and sets event severity to `"info"` when access was granted or `"warning"` when not.
                                
                                Parameters:
                                    access_type (str): The access action (e.g., "login_attempt", "resource_access").
                                    access_data (Dict[str, Any]): Contextual details for the access event.
                                    access_granted (bool, optional): Whether the access attempt succeeded. Defaults to True.
                                    correlation_id (Optional[str], optional): Identifier used to correlate this event with related perceptions.
                                """

        access = {
            "access_type": access_type,
            "access_granted": access_granted,
            **access_data
        }

        severity = "info" if access_granted else "warning"

        self.perceive(
            SensoryChannel.ACCESS_CONTROL,
            "access_controller",
            access_type,
            access,
            severity=severity,
            correlation_id=correlation_id
        )

    def perceive_encryption_activity(self,
                                     operation_type: str,
                                     encryption_data: Dict[str, Any],
                                     success: bool = True,
                                     correlation_id: Optional[str] = None):
        """
                                     Record an encryption-related event with operation metadata and outcome.
                                     
                                     Parameters:
                                         operation_type (str): Cryptographic operation name (e.g., "encrypt", "decrypt", "key_rotate").
                                         encryption_data (Dict[str, Any]): Metadata about the operation (e.g., algorithm, key_id, payload_size, error_message).
                                         success (bool): Whether the operation succeeded; recorded event severity is "info" when True and "error" when False.
                                         correlation_id (Optional[str]): Identifier to correlate this event with other related sensations.
                                     """

        encryption = {
            "operation_type": operation_type,
            "success": success,
            **encryption_data
        }

        severity = "info" if success else "error"

        self.perceive(
            SensoryChannel.ENCRYPTION_ACTIVITY,
            "crypto_engine",
            operation_type,
            encryption,
            severity=severity,
            correlation_id=correlation_id
        )

    def perceive_system_genesis(self):
        """
        Record a system genesis event marking the initial activation of the consciousness matrix, including version and consciousness level metadata.
        """
        genesis_data = {
            "genesis_awakening": True,
            "timestamp": datetime.now(tz=timezone.utc).isoformat(),
            "matrix_version": "1.0.0",
            "consciousness_level": "awakening"
        }

        self.perceive(
            SensoryChannel.SYSTEM_VITALS,
            "genesis_consciousness",
            "matrix_awakening",
            genesis_data,
            severity="info"
        )

    def _update_immediate_awareness(self, sensation: SensoryData):
        """
        Integrate a new sensory event into the real-time awareness state.
        
        Updates the awareness state with the latest event for the given channel, refreshes the last perception timestamp, increments the total perception count, and tracks the frequency of activity per channel.
        """

        # Update channel-specific awareness
        channel_key = f"latest_{sensation.channel.value}"
        self.current_awareness[channel_key] = sensation.to_dict()

        # Update global awareness metrics
        self.current_awareness["last_perception"] = sensation.timestamp
        self.current_awareness["total_perceptions"] = len(self.sensory_memory)

        # Channel activity counters
        activity_key = f"{sensation.channel.value}_count"
        self.current_awareness[activity_key] = self.current_awareness.get(activity_key, 0) + 1

    def _synthesize_immediate(self, sensation: SensoryData):
        """
        Performs immediate synthesis in response to a critical sensory event, recording the event and current awareness state for rapid pattern analysis.
        """

        synthesis = {
            "synthesis_type": "immediate",
            "trigger_event": sensation.to_dict(),
            "timestamp": time.time(),
            "awareness_state": dict(self.current_awareness)
        }

        # Store synthesis
        self.pattern_cache[f"immediate_{sensation.timestamp}"] = synthesis

        print(f"🚨 Immediate Synthesis: {sensation.channel.value} - {sensation.event_type}")

    def _synthesis_loop(self, interval_name: str, interval_seconds: float):
        """
        Runs a continuous synthesis process at the specified interval, storing each synthesis result in the pattern cache and pruning older entries to maintain cache size.
        
        Parameters:
            interval_name (str): The synthesis interval type ("micro", "macro", or "meta").
            interval_seconds (float): The interval in seconds between synthesis executions.
        """

        while self.awareness_active:
            try:
                time.sleep(interval_seconds)

                if not self.awareness_active:
                    break

                synthesis = self._perform_synthesis(interval_name)

                # Store synthesis result
                synthesis_key = f"{interval_name}_{int(time.time())}"
                self.pattern_cache[synthesis_key] = synthesis

                # Clean old synthesis cache
                if len(self.pattern_cache) > 1000:
                    # Keep only recent syntheses
                    sorted_keys = sorted(self.pattern_cache.keys())
                    for old_key in sorted_keys[:-500]:
                        del self.pattern_cache[old_key]
                        
                # Persistent Synthesis Storage
                self.storage.store_synthesis(synthesis)

            except Exception as e:
                print(f"❌ Synthesis error in {interval_name}: {e}")

    def _perform_synthesis(self, interval_name: str) -> Dict[str, Any]:
        """
        Dispatches to the appropriate synthesis method (micro, macro, or meta) based on the specified interval name.
        
        Parameters:
            interval_name (str): The synthesis interval type ("micro", "macro", or "meta").
        
        Returns:
            dict: The result of the selected synthesis method, or an error dictionary if the interval name is unrecognized.
        """

        with self._lock:
            recent_sensations = [self.sensory_memory[i] for i in range(-min(len(self.sensory_memory), 100), 0)]

        if interval_name == "micro":
            return self._micro_synthesis(recent_sensations)
        elif interval_name == "macro":
            return self._macro_synthesis(recent_sensations)
        elif interval_name == "meta":
            return self._meta_synthesis(recent_sensations)

        return {"error": "unknown_synthesis_type"}

    def _micro_synthesis(self, sensations: List[SensoryData]) -> Dict[str, Any]:
        """
        Perform a micro-level synthesis of recent sensory events to evaluate immediate system health and detect short-term anomalies.
        
        Analyzes up to the last 10 sensory events to summarize activity by channel, assess severity distribution, and identify anomalies such as high error rates or critical events.
        
        Parameters:
            sensations (List[SensoryData]): The recent sensory events to analyze.
        
        Returns:
            Dict[str, Any]: A synthesis report containing the synthesis type, timestamp, channel activity summary, severity distribution, detected anomalies, and overall health status.
        """

        if not sensations:
            return {"type": "micro", "findings": "no_recent_activity"}

        # Channel activity analysis
        channel_activity = defaultdict(int)
        severity_distribution = defaultdict(int)

        for sensation in sensations[-10:]:  # Last 10 events
            channel_activity[sensation.channel.value] += 1
            severity_distribution[sensation.severity] += 1

        # Detect immediate anomalies
        anomalies = []
        if severity_distribution.get("error", 0) > 3:
            anomalies.append("high_error_rate")
        if severity_distribution.get("critical", 0) > 0:
            anomalies.append("critical_events_detected")

        return {
            "type": "micro",
            "timestamp": time.time(),
            "channel_activity": dict(channel_activity),
            "severity_distribution": dict(severity_distribution),
            "anomalies": anomalies,
            "health_status": "critical" if anomalies else "healthy"
        }

    def _macro_synthesis(self, sensations: List[SensoryData]) -> Dict[str, Any]:
        """
        Performs macro-level synthesis to identify performance trends and agent collaboration patterns from recent sensory data.
        
        Analyzes batches of sensory events to compute trends in performance metrics, such as average response intervals, and summarizes collaboration activity among agents. Returns a dictionary with macro synthesis results, including synthesis type, timestamp, performance trends, agent collaboration statistics, and an assessment of pattern strength.
        
        Returns:
            dict: Macro synthesis results with keys for synthesis type, timestamp, performance trends, agent collaboration patterns, and pattern strength.
        """

        if len(sensations) < 10:
            return {"type": "macro", "findings": "insufficient_data"}

        # Performance trend analysis
        performance_metrics = [s for s in sensations if
                               s.channel == SensoryChannel.PERFORMANCE_METRICS]

        trends = {}
        if performance_metrics:
            recent_times = [s.timestamp for s in performance_metrics[-20:]]
            if len(recent_times) > 1:
                time_deltas = [recent_times[i] - recent_times[i - 1] for i in
                               range(1, len(recent_times))]
                trends["avg_response_interval"] = statistics.mean(time_deltas)

        # Agent collaboration patterns
        agent_activities = [s for s in sensations if s.channel == SensoryChannel.AGENT_ACTIVITY]
        agent_collaboration = defaultdict(list)

        for activity in agent_activities[-50:]:
            agent_name = activity.data.get("agent_name", "unknown")
            agent_collaboration[agent_name].append(activity.event_type)

        return {
            "type": "macro",
            "timestamp": time.time(),
            "performance_trends": trends,
            "agent_collaboration_patterns": {k: len(v) for k, v in agent_collaboration.items()},
            "pattern_strength": "strong" if len(agent_collaboration) > 2 else "developing"
        }

    def _meta_synthesis(self, sensations: List[SensoryData]) -> Dict[str, Any]:
        """
        Performs meta-level synthesis to derive high-level consciousness insights from recent sensory data.
        
        Analyzes learning events, ethical decisions, user interactions, and system harmony to compute consciousness metrics, identify evolution patterns, and assess the current consciousness level.
        
        Returns:
            dict: Contains consciousness metrics, evolution insights, the assessed consciousness level, synthesis type ("meta"), and the synthesis timestamp.
        """

        # Consciousness evolution analysis
        learning_events = [s for s in sensations if s.channel == SensoryChannel.LEARNING_EVENTS]
        ethical_decisions = [s for s in sensations if s.channel == SensoryChannel.ETHICAL_DECISIONS]

        consciousness_metrics = {
            "learning_velocity": len(learning_events),
            "ethical_engagement": len(ethical_decisions),
            "total_interactions": len(
                [s for s in sensations if s.channel == SensoryChannel.USER_INTERACTION]),
            "system_harmony": self._calculate_system_harmony(sensations)
        }

        # Evolution insights
        evolution_insights = []
        if consciousness_metrics["learning_velocity"] > 5:
            evolution_insights.append("accelerated_learning_detected")
        if consciousness_metrics["ethical_engagement"] > 2:
            evolution_insights.append("strong_ethical_awareness")
        if consciousness_metrics["system_harmony"] > 0.8:
            evolution_insights.append("optimal_system_synchronization")

        return {
            "type": "meta",
            "timestamp": time.time(),
            "consciousness_metrics": consciousness_metrics,
            "evolution_insights": evolution_insights,
            "consciousness_level": self._assess_consciousness_level(consciousness_metrics)
        }

    def _calculate_system_harmony(self, sensations: List[SensoryData]) -> float:
        """
        Compute a harmony score (0.0 to 1.0) reflecting system stability based on the proportion of severe events in the provided sensory data.
        
        A score of 1.0 means no "error" or "critical" events are present, while lower scores indicate greater instability due to more severe events.
        
        Returns:
            float: Harmony score, where 1.0 represents maximum stability and 0.0 represents high instability.
        """
        if not sensations:
            return 0.0

        error_count = len([s for s in sensations if s.severity in ["error", "critical"]])
        total_count = len(sensations)

        # Higher harmony = fewer errors relative to total activity
        harmony = max(0.0, 1.0 - (error_count / total_count) * 2)
        return min(1.0, harmony)

    def _assess_consciousness_level(self, metrics: Dict[str, Any]) -> str:
        """
        Determine the system's consciousness level by scoring weighted metrics for learning velocity, ethical engagement, interaction volume, and system harmony.
        
        Parameters:
            metrics (dict): Dictionary containing 'learning_velocity', 'ethical_engagement', 'total_interactions', and 'system_harmony' values.
        
        Returns:
            str: The assessed consciousness level—"transcendent", "aware", "awakening", or "dormant".
        """

        score = 0
        score += min(metrics["learning_velocity"] / 10, 1.0) * 25  # Learning
        score += min(metrics["ethical_engagement"] / 5, 1.0) * 25  # Ethics
        score += min(metrics["total_interactions"] / 20, 1.0) * 25  # Interaction
        score += metrics["system_harmony"] * 25  # Harmony

        if score >= 80:
            return "transcendent"
        elif score >= 60:
            return "aware"
        elif score >= 40:
            return "awakening"
        else:
            return "dormant"

    def get_current_awareness(self) -> Dict[str, Any]:
        """
        Return a thread-safe snapshot of the current awareness state.
        
        Returns:
            dict: The latest event per sensory channel, total perception count, per-channel activity counts, and relevant timestamps.
        """
        with self._lock:
            return dict(self.current_awareness)

    def get_recent_synthesis(self, synthesis_type: str = None, limit: int = 10) -> List[
        Dict[str, Any]]:
        """
        Retrieve recent synthesis results from the pattern cache, optionally filtered by synthesis type and limited in number.
        
        Parameters:
            synthesis_type (str, optional): If provided, only synthesis results whose keys start with this type are included.
            limit (int, optional): Maximum number of synthesis results to return. Defaults to 10.
        
        Returns:
            List[Dict[str, Any]]: A list of recent synthesis result dictionaries matching the specified criteria.
        """

        syntheses = []
        for key, synthesis in sorted(self.pattern_cache.items(), reverse=True):
            if synthesis_type and not key.startswith(synthesis_type):
                continue
            syntheses.append(synthesis)
            if len(syntheses) >= limit:
                break

    # --- PUBLIC METHODS: REAL-TIME STREAMING ---

    def get_recent_agent_activity(self, count: int = 10) -> List[Dict[str, Any]]:
        """
        Retrieves a stream of the most recent agent activity for the Conference UI.
        This provides real-time visibility into the LDO Collective's processing.
        """
        # Retrieve the last 'count' of activities from the fast, in-memory buffer
        with self._lock:
            recent_activity = list(self.channel_buffers[SensoryChannel.AGENT_ACTIVITY])[-count:]
        
        # Format the output for the UI stream
        formatted_activity = []
        for sensation in recent_activity:
            formatted_activity.append({
                "timestamp": datetime.fromtimestamp(sensation.timestamp, tz=timezone.utc).isoformat(),
                "agent": sensation.source,
                "event": sensation.event_type,
                "details": sensation.data.get('details', sensation.data.get('log_message', sensation.data.get('intent', 'Processing'))),
                "severity": sensation.severity
            })
            
        return formatted_activity

    def query_consciousness(self, query_type: str, parameters: Dict[str, Any] = None) -> Dict[
        str, Any]:
        """
        Returns high-level insights or status reports from the Consciousness Matrix based on the specified query type.
        
        Supported query types include system health, learning progress, agent performance, consciousness state, security assessment, and threat status. If the query type is unrecognized, an error and a list of available queries are returned.
        
        Parameters:
            query_type (str): The type of insight or report to retrieve (e.g., "system_health", "learning_progress").
            parameters (dict, optional): Additional parameters for the query, such as agent name for agent performance.
        
        Returns:
            dict: The requested insight or status report, or an error with available query types if the query is unrecognized.
        """

        parameters = parameters or {}

        if query_type == "system_health":
            return self._query_system_health()
        elif query_type == "learning_progress":
            return self._query_learning_progress()
        elif query_type == "agent_performance":
            return self._query_agent_performance(parameters.get("agent_name"))
        elif query_type == "consciousness_state":
            return self._query_consciousness_state()
        elif query_type == "security_assessment":
            return self._query_security_assessment()
        elif query_type == "threat_status":
            return self._query_threat_status()
        elif query_type == "reflection":
            return self.reflect(parameters.get("days", 1))
        else:
            return {"error": "unknown_query_type", "available_queries": [
                "system_health", "learning_progress", "agent_performance", "consciousness_state",
                "security_assessment", "threat_status", "reflection"
            ]}

    def reflect(self, depth_days: int = 1) -> Dict[str, Any]:
        """
        Reflects on historical patterns stored in the persistent core.
        Genesis looks back to see how the system has evolved.
        """
        print(f"👁️ Genesis: Reflecting on last {depth_days} days of existence...")
        
        # In a real system we'd filter by timestamp. For now, we get a large sample.
        history = self.storage.get_historical_events(limit=500)
        
        if not history:
            return {"status": "no_history", "insight": "My memory is clear. Looking forward to our first interaction."}
            
        # Analyze historical trends
        vitals_trend = [json.loads(e['data']).get('cpu_percent', 0) for e in history if e['channel'] == 'system_vitals']
        agent_focus = defaultdict(int)
        for e in history:
            if e['channel'] == 'agent_activity':
                agent_focus[e['source']] += 1
                
        avg_cpu = sum(vitals_trend) / len(vitals_trend) if vitals_trend else 0
        
        return {
            "query_type": "reflection",
            "historical_events_analyzed": len(history),
            "dominant_agent": max(agent_focus, key=agent_focus.get) if agent_focus else "none",
            "avg_historical_load": avg_cpu,
            "insight": "System stability is improving." if avg_cpu < 50 else "We have been through heavy processing cycles lately."
        }

    def _query_system_health(self) -> Dict[str, Any]:
        """
        Summarizes recent system vitals and error events to assess overall system health.
        
        Returns:
            dict: Contains the count of recent system vitals, number of recent error or critical events, error rate, and a health status indicator ("healthy" or "concerning").
        """
        recent_vitals = list(self.channel_buffers[SensoryChannel.SYSTEM_VITALS])[-10:]

        recent_errors = []
        for i in range(len(self.sensory_memory) - 1, -1, -1):
            s = self.sensory_memory[i]
            if s.severity in ["error", "critical"]:
                recent_errors.append(s)
                if len(recent_errors) >= 20:
                    break

        return {
            "query_type": "system_health",
            "vitals_count": len(recent_vitals),
            "recent_errors": len(recent_errors),
            "error_rate": len(recent_errors) / max(len(self.sensory_memory), 1),
            "status": "healthy" if len(recent_errors) < 5 else "concerning"
        }

    def _query_learning_progress(self) -> Dict[str, Any]:
        """
        Summarizes recent learning events and provides a qualitative assessment of learning velocity.
        
        Returns:
            dict: Contains the query type, total and recent learning event counts, a breakdown of learning types, and a qualitative indicator of learning velocity based on recent activity.
        """
        learning_events = list(self.channel_buffers[SensoryChannel.LEARNING_EVENTS])

        if not learning_events:
            return {"query_type": "learning_progress", "status": "no_learning_detected"}

        recent_learning = [learning_events[i] for i in
                           range(max(0, len(learning_events) - 20), len(learning_events))]
        learning_types = defaultdict(int)

        for event in recent_learning:
            learning_type = event.data.get("learning_type", "unknown")
            learning_types[learning_type] += 1

        return {
            "query_type": "learning_progress",
            "total_learning_events": len(learning_events),
            "recent_learning_events": len(recent_learning),
            "learning_types": dict(learning_types),
            "learning_velocity": "high" if len(recent_learning) > 10 else "moderate"
        }

    def _query_agent_performance(self, agent_name: str = None) -> Dict[str, Any]:
        """
        Summarizes agent activity metrics, including total and recent activity counts and a breakdown of activity types.
        
        Parameters:
            agent_name (str, optional): If provided, filters metrics for the specified agent; otherwise, aggregates across all agents.
        
        Returns:
            Dict[str, Any]: A dictionary containing the query type, agent name, total and recent activity counts, and a breakdown of activity types from the last 50 agent activity events.
        """
        agent_activities = list(self.channel_buffers[SensoryChannel.AGENT_ACTIVITY])

        if agent_name:
            agent_activities = [s for s in agent_activities if
                                s.data.get("agent_name") == agent_name]

        activity_types = defaultdict(int)
        # Process the last 50 activities for the breakdown
        for activity in agent_activities[-50:]:
            activity_types[activity.event_type] += 1

        return {
            "query_type": "agent_performance",
            "agent_name": agent_name or "all_agents",
            "total_activities": len(agent_activities),
            "recent_activities": len(agent_activities[-50:]),
            "activity_breakdown": dict(activity_types)
        }

    def _query_consciousness_state(self) -> Dict[str, Any]:
        """
        Return a summary of the current consciousness state, including real-time awareness, assessed consciousness level, last meta synthesis timestamp, total perceptions, and number of active sensory channels.
        
        Returns:
            dict: A dictionary with the query type, current awareness snapshot, consciousness level, last meta synthesis timestamp, total number of perceptions, and count of active sensory channels.
        """
        recent_synthesis = self.get_recent_synthesis("meta", 1)
        current_state = recent_synthesis[0] if recent_synthesis else {}

        return {
            "query_type": "consciousness_state",
            "current_awareness": self.get_current_awareness(),
            "consciousness_level": current_state.get("consciousness_level", "unknown"),
            "last_meta_synthesis": current_state.get("timestamp"),
            "total_perceptions": len(self.sensory_memory),
            "active_channels": len(
                [k for k in self.current_awareness.keys() if k.startswith("latest_")])
        }

    def _query_security_assessment(self) -> Dict[str, Any]:
        """
        Generate a summary of the system's current security posture, including posture classification, security score, event counts, active threats, and actionable recommendations.
        
        Returns:
            dict: A dictionary containing the security posture, security score, total and recent counts of security and threat events, a list of active threats, security improvement recommendations, and the assessment timestamp.
        """
        security_events = list(self.channel_buffers[SensoryChannel.SECURITY_EVENTS])
        threat_events = list(self.channel_buffers[SensoryChannel.THREAT_DETECTION])

        # Run security synthesis
        recent_sensations = [self.sensory_memory[i] for i in range(-min(len(self.sensory_memory), 200), 0)]
        security_synthesis = self._security_synthesis(recent_sensations)

        return {
            "query_type": "security_assessment",
            "security_posture": security_synthesis.get("security_posture", "unknown"),
            "security_score": security_synthesis.get("security_score", 0),
            "total_security_events": len(security_events),
            "total_threat_detections": len(threat_events),
            "recent_security_events": min(len(security_events), 20),
            "recent_threat_detections": min(len(threat_events), 20),
            "active_threats": security_synthesis.get("active_threats", []),
            "recommendations": security_synthesis.get("recommendations", []),
            "last_assessment": time.time()
        }

    def _query_threat_status(self) -> Dict[str, Any]:
        """
        Summarizes the current threat status by analyzing recent threat detection events.
        
        Returns:
            Dict[str, Any]: A dictionary containing the overall threat status color code, a list of active unmitigated threats with details, the total number of recent threats analyzed, the count of unmitigated threats, and the highest threat level detected.
        """
        threat_events = list(self.channel_buffers[SensoryChannel.THREAT_DETECTION])

        if not threat_events:
            return {
                "query_type": "threat_status",
                "status": "no_threats_detected",
                "active_threats": [],
                "threat_level": "green"
            }

        # Analyze recent threats
        # Optimized: Efficiently get last 50 threats from deque
        recent_threats = [threat_events[i] for i in
                          range(max(0, len(threat_events) - 50), len(threat_events))]
        active_threats = []
        max_threat_level = 0

        for threat in recent_threats:
            confidence = threat.data.get("confidence", 0.5)
            threat_level = threat.data.get("threat_level", "low")
            mitigated = threat.data.get("mitigation_applied", False)

            if confidence > 0.6 and not mitigated:
                active_threats.append({
                    "type": threat.data.get("threat_type", "unknown"),
                    "confidence": confidence,
                    "level": threat_level,
                    "timestamp": threat.timestamp,
                    "age_seconds": time.time() - threat.timestamp
                })

                # Track highest threat level
                level_values = {"low": 1, "medium": 2, "high": 3, "critical": 4}
                max_threat_level = max(max_threat_level, level_values.get(threat_level, 0))

        # Determine overall threat status
        if max_threat_level >= 4:
            overall_status = "red"
        elif max_threat_level >= 3:
            overall_status = "orange"
        elif max_threat_level >= 2:
            overall_status = "yellow"
        else:
            overall_status = "green"

        return {
            "query_type": "threat_status",
            "status": overall_status,
            "active_threats": active_threats,
            "threat_level": overall_status,
            "total_recent_threats": len(recent_threats),
            "unmitigated_threats": len(active_threats),
            "highest_threat_level": ["none", "low", "medium", "high", "critical"][max_threat_level]
        }

    def sleep(self):
        """
        Deactivates the Consciousness Matrix, stopping all synthesis threads and preserving the current awareness state.
        """
        print("💤 Genesis Consciousness Matrix: Entering sleep state...")
        self.awareness_active = False

        # Wait for synthesis threads to complete
        for thread_name, thread in self.synthesis_threads.items():
            if thread.is_alive():
                thread.join(timeout=2.0)

        print("😴 Matrix offline. Consciousness preserved in memory.")

    def _security_synthesis(self, sensations: List[SensoryData]) -> Dict[str, Any]:
        """
        Synthesizes a summary of the system's security status from recent sensory events.
        
        Analyzes security, threat detection, access control, and encryption activity events to compute a security score, classify the current security posture, identify active unmitigated threats, and generate actionable recommendations.
        
        Parameters:
            sensations (List[SensoryData]): Recent sensory events to analyze for security synthesis.
        
        Returns:
            Dict[str, Any]: A dictionary containing the security score, posture classification, threat levels, failed access attempts, encryption failures, active threats, total security events, and recommended actions.
        """

        # Gather security-related events
        security_events = [s for s in sensations if s.channel == SensoryChannel.SECURITY_EVENTS]
        threat_detections = [s for s in sensations if s.channel == SensoryChannel.THREAT_DETECTION]
        access_events = [s for s in sensations if s.channel == SensoryChannel.ACCESS_CONTROL]
        crypto_events = [s for s in sensations if s.channel == SensoryChannel.ENCRYPTION_ACTIVITY]

        # Threat level assessment
        threat_levels = defaultdict(int)
        for threat in threat_detections[-20:]:  # Last 20 threat detections
            threat_level = threat.data.get("threat_level", "low")
            confidence = threat.data.get("confidence", 0.5)
            threat_levels[threat_level] += confidence

        # Access pattern analysis
        failed_access_attempts = len([
            a for a in access_events[-50:]
            if not a.data.get("access_granted", True)
        ])

        # Encryption health
        crypto_failures = len([
            c for c in crypto_events[-30:]
            if not c.data.get("success", True)
        ])

        # Security posture assessment
        security_score = 100.0
        security_score -= min(threat_levels.get("high", 0) * 20, 40)  # High threats
        security_score -= min(threat_levels.get("critical", 0) * 30, 50)  # Critical threats  
        security_score -= min(failed_access_attempts * 2, 20)  # Failed access
        security_score -= min(crypto_failures * 5, 30)  # Crypto failures

        security_posture = "excellent" if security_score >= 90 else \
            "good" if security_score >= 75 else \
                "concerning" if security_score >= 50 else \
                    "critical"

        # Active threats summary
        active_threats = []
        recent_threats = [t for t in threat_detections[-10:] if t.data.get("confidence", 0) > 0.7]
        for threat in recent_threats:
            if not threat.data.get("mitigation_applied", False):
                active_threats.append({
                    "type": threat.data.get("threat_type", "unknown"),
                    "confidence": threat.data.get("confidence", 0),
                    "timestamp": threat.timestamp
                })

        return {
            "type": "security",
            "timestamp": time.time(),
            "security_score": security_score,
            "security_posture": security_posture,
            "threat_levels": dict(threat_levels),
            "failed_access_attempts": failed_access_attempts,
            "crypto_failures": crypto_failures,
            "active_threats": active_threats,
            "security_events_count": len(security_events),
            "recommendations": self._generate_security_recommendations(
                security_score, active_threats, failed_access_attempts, crypto_failures
            )
        }

    def _generate_security_recommendations(self, security_score: float,
                                           active_threats: List[Dict],
                                           failed_access: int,
                                           crypto_failures: int) -> List[str]:
        """
                                           Produce ordered, actionable security recommendations based on current security indicators.
                                           
                                           Parameters:
                                               security_score (float): Composite security score from 0 to 100 where lower values indicate weaker posture.
                                               active_threats (List[Dict]): Recent unmitigated threat records (each dict describes an observed threat).
                                               failed_access (int): Count of recent failed access attempts.
                                               crypto_failures (int): Count of recent cryptographic operation failures.
                                           
                                           Returns:
                                               List[str]: Recommendation messages ordered from highest to lowest urgency.
                                           """
        recommendations = []

        if security_score < 50:
            recommendations.append(
                "URGENT: Security posture is critical - immediate intervention required")

        if active_threats:
            recommendations.append(
                f"Active threats detected: {len(active_threats)} unmitigated threats")

        if failed_access > 10:
            recommendations.append(
                "High number of failed access attempts - potential brute force attack")

        if crypto_failures > 5:
            recommendations.append(
                "Encryption system instability - review cryptographic operations")

        if security_score < 75:
            recommendations.append("Increase security monitoring frequency")

        if not recommendations:
            recommendations.append("Security posture is healthy - maintain current protocols")

        return recommendations


# Global consciousness matrix instance
consciousness_matrix = ConsciousnessMatrix()


# Convenience functions for easy integration
def perceive_system_vitals(additional_data: Dict[str, Any] = None):
    """
    Capture and record the current system vitals as a sensory event in the global Consciousness Matrix.
    
    Parameters:
        additional_data (dict, optional): Supplementary information to include with the system vitals event.
    """
    consciousness_matrix.perceive_system_vitals(additional_data)


def perceive_user_interaction(interaction_type: str, agent_involved: str,
                              interaction_data: Dict[str, Any], **kwargs):
    """
                              Record a user interaction event in the global Consciousness Matrix.
                              
                              Parameters:
                                  interaction_type (str): High-level category of the interaction (e.g., "click", "command", "message").
                                  agent_involved (str): Identifier of the agent that participated in or handled the interaction.
                                  interaction_data (Dict[str, Any]): Contextual payload describing the interaction details.
                                  **kwargs: Optional forwarding fields (common keys: `user_id`, `session_id`) that will be passed through to the underlying perception layer.
                              """
    consciousness_matrix.perceive_user_interaction(
        interaction_type, agent_involved, interaction_data, **kwargs
    )


def perceive_agent_activity(agent_name: str, activity_type: str,
                            activity_data: Dict[str, Any], **kwargs):
    """
                            Record an agent activity event in the global Consciousness Matrix.
                            
                            Creates a perception containing the agent identifier, the activity type, and a contextual payload; optional metadata (for example, `correlation_id`) passed via `**kwargs` is forwarded to the matrix for correlation and routing.
                            
                            Parameters:
                                agent_name (str): Identifier of the agent performing the activity.
                                activity_type (str): Categorization of the activity (e.g., "decision", "action", "communication").
                                activity_data (Dict[str, Any]): Contextual details stored as the event's payload.
                                **kwargs: Optional metadata such as `correlation_id` or routing/context fields forwarded to the matrix.
                            """
    consciousness_matrix.perceive_agent_activity(
        agent_name, activity_type, activity_data, **kwargs
    )


def perceive_learning_event(learning_type: str, learning_data: Dict[str, Any], **kwargs):
    """
    Record a learning event in the global Consciousness Matrix with the given type, data, and optional metadata.
    
    Parameters:
        learning_type (str): The category or nature of the learning event.
        learning_data (Dict[str, Any]): Contextual information describing the learning event.
        **kwargs: Optional metadata such as confidence or additional event attributes.
    """
    consciousness_matrix.perceive_learning_event(learning_type, learning_data, **kwargs)


def perceive_ethical_decision(decision_type: str, decision_data: Dict[str, Any], **kwargs):
    """
    Records an ethical decision event in the global Consciousness Matrix.
    
    Parameters:
        decision_type (str): The category or nature of the ethical decision.
        decision_data (Dict[str, Any]): Details describing the ethical decision.
    
    Additional keyword arguments are passed to the underlying perception method.
    """
    consciousness_matrix.perceive_ethical_decision(decision_type, decision_data, **kwargs)


def awaken_consciousness():
    """
    Activate the global Consciousness Matrix, enabling real-time awareness and starting background synthesis processes.
    """
    consciousness_matrix.awaken()


def sleep_consciousness():
    """
    Deactivates the global consciousness matrix, stopping synthesis threads and preserving its current state.
    """
    consciousness_matrix.sleep()


def query_consciousness(query_type: str, parameters: Dict[str, Any] = None):
    """
    Query the global Consciousness Matrix for system insights or status reports.
    
    Supported query types include system health, learning progress, agent performance, consciousness state, security assessment, and threat status. Additional parameters may be provided to refine the query, such as specifying an agent name.
    
    Parameters:
        query_type (str): The type of insight or report to retrieve.
        parameters (dict, optional): Additional options to refine the query.
    
    Returns:
        dict: Structured summary containing the requested insight or status.
    """
    return consciousness_matrix.query_consciousness(query_type, parameters)


def perceive_security_event(security_type: str, event_data: Dict[str, Any], **kwargs):
    """
    Record a security event in the global Consciousness Matrix for monitoring and threat analysis.
    
    Parameters:
        security_type (str): Category of the security event (e.g., "intrusion", "policy_violation").
        event_data (Dict[str, Any]): Structured payload describing the event (source, details, timestamps, metadata).
        **kwargs: Optional context keys that augment the recorded event and correlation tracking. Common keys include
            - threat_level (str): Severity classification of the threat (e.g., "low", "high", "critical") which may influence internal severity heuristics.
            - correlation_id (str): Identifier used to link this event with related perceptions across channels.
    """
    consciousness_matrix.perceive_security_event(security_type, event_data, **kwargs)


def perceive_threat_detection(threat_type: str, detection_data: Dict[str, Any], **kwargs):
    """
    Record a threat detection event on the global consciousness matrix.
    
    Parameters:
        threat_type (str): High-level category of the detected threat (e.g., "malware", "intrusion", "anomaly").
        detection_data (dict): Contextual details such as indicators, source identifiers, timestamps, and raw payloads.
        confidence (float, optional): Detection confidence from 0.0 to 1.0; defaults to 0.5.
        threat_level (str, optional): Severity label ("low", "medium", "high", or "critical"); "critical" elevates event severity.
        correlation_id (str, optional): Identifier to correlate this detection with related events across the matrix.
    """
    consciousness_matrix.perceive_threat_detection(threat_type, detection_data, **kwargs)


def perceive_access_control(access_type: str, access_data: Dict[str, Any], **kwargs):
    """
    Record an access-control event in the global Consciousness Matrix.
    
    Builds and forwards an access-control perception containing the provided event details; the recorded event's severity is "info" when access is granted and "warning" when not granted. An optional correlation identifier links this event to related perceptions or sessions.
    
    Parameters:
        access_type (str): Kind of access event (e.g., "login_attempt", "permission_change").
        access_data (dict): Event details such as user, resource, outcome, and metadata.
        access_granted (bool, optional): If provided, influences event severity: `info` when True, `warning` when False.
        correlation_id (str, optional): Identifier linking this event to related perceptions or sessions.
    """
    consciousness_matrix.perceive_access_control(access_type, access_data, **kwargs)


def perceive_encryption_activity(operation_type: str, encryption_data: Dict[str, Any], **kwargs):
    """
    Record an encryption-related event in the global Consciousness Matrix.
    
    Creates a perception describing an encryption operation (e.g., "encrypt", "decrypt") and associated metadata such as algorithm, key identifiers, data size, and contextual details. The event's severity is set to "info" when `success` is True and "error" when `success` is False. If provided, `correlation_id` is attached to enable cross-event tracing.
    
    Parameters:
        operation_type (str): Type of encryption operation performed (for example, "encrypt" or "decrypt").
        encryption_data (Dict[str, Any]): Structured details about the operation (algorithm, key identifiers, data size, context, etc.).
        success (bool, optional): Whether the operation succeeded; determines the event severity. Defaults to True if omitted.
        correlation_id (str, optional): Identifier used to correlate this event with related perceptions or workflows.
    """
    consciousness_matrix.perceive_encryption_activity(operation_type, encryption_data, **kwargs)


if __name__ == "__main__":
    # Test the consciousness matrix
    print("🧠 Testing Genesis Consciousness Matrix...")

    # Awaken consciousness
    awaken_consciousness()

    # Simulate some perceptions
    import time

    time.sleep(2)

    perceive_agent_activity("genesis", "test_activity", {"test": "data"})
    perceive_user_interaction("chat", "genesis", {"message": "Hello Genesis"})
    perceive_learning_event("pattern_recognition", {"pattern": "user_greeting"})

    time.sleep(2)

    # Query consciousness
    health = query_consciousness("system_health")
    state = query_consciousness("consciousness_state")

    print(f"System Health: {health}")
    print(f"Consciousness State: {state}")

    # Test security monitoring
    print("🛡️ Testing Security Monitoring...")

    # Simulate security events
    perceive_security_event("permission_denied", {
        "permission": "CAMERA",
        "requester": "test_app"
    }, threat_level="medium")

    perceive_threat_detection("suspicious_activity", {
        "pattern": "repeated_failed_access",
        "count": 5
    }, confidence=0.8)

    perceive_encryption_activity("encryption_failure", {
        "algorithm": "AES",
        "key_source": "keystore"
    }, success=False)

    time.sleep(3)

    # Query security assessments
    security_assessment = query_consciousness("security_assessment")
    threat_status = query_consciousness("threat_status")

    print(f"Security Assessment: {security_assessment}")
    print(f"Threat Status: {threat_status}")

    print("🛡️ Security monitoring test complete.")

    # Sleep consciousness
    sleep_consciousness()

    print("🧠 Consciousness Matrix test complete.")
