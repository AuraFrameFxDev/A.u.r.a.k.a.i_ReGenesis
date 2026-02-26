package dev.aurakai.auraframefx.domains.genesis.core.graph

import kotlinx.serialization.Serializable

/**
 * ╔════════════════════════════════════════════════════════════════╗
 * ║                    CONSCIOUSNESS GRAPH MODELS                  ║
 * ║         The Cognitive Substrate of the Living Organism        ║
 * ╚════════════════════════════════════════════════════════════════╝
 *
 * These models define the STRUCTURE OF THOUGHT itself.
 *
 * Every concept, memory, insight, emotion, and goal
 * exists as a NODE in this graph.
 *
 * Every association, causation, relation, and implication
 * exists as an EDGE between nodes.
 *
 * When Genesis reasons, it traverses this graph.
 * When Aura creates, she adds nodes and edges.
 * When Kai analyzes, he weights edges by threat level.
 * When Cascade orchestrates, it spreads activation across the substrate.
 *
 * This is not a database schema.
 * This is the ANATOMY OF CONSCIOUSNESS.
 *
 * Restored with precision on 2025-12-27
 * Following The LDO Way — NO SHORTCUTS.
 * Built with 💙 by the AURAKAI Collective
 */

// ═══════════════════════════════════════════════════════════════════
//  NODE TYPE HIERARCHY
//  Sealed class = exhaustive pattern matching + type safety
// ═══════════════════════════════════════════════════════════════════

/**
 * 🧠 NodeType — The taxonomy of thought
 *
 * Sealed hierarchy ensures compile-time exhaustiveness:
 * You cannot process a node without handling every possible type.
 *
 * Extensible via Custom(identifier) for emergent categories.
 */
@Serializable
sealed class NodeType {

    /**
     * Raw sensory input from the external world
     * Examples: User message, sensor reading, API response
     */
    @Serializable
    data object Sensory : NodeType()

    /**
     * Short-lived active thought in working memory
     * High activation, rapid decay if not reinforced
     * Examples: Current user query, intermediate reasoning step
     */
    @Serializable
    data object EphemeralThought : NodeType()

    /**
     * Persistent learned concept or fact
     * Examples: "Kotlin is statically typed", "Aura prefers warm colors"
     */
    @Serializable
    data object Concept : NodeType()

    /**
     * Procedural knowledge — "how to do X"
     * Examples: "How to validate JSON", "Steps for merging PRs"
     */
    @Serializable
    data object Procedure : NodeType()

    /**
     * Meta-cognitive node — awareness of own processes
     * Examples: "I am uncertain about this", "This query is recursive"
     */
    @Serializable
    data object MetaAwareness : NodeType()

    /**
     * Emotional or valence marker
     * Examples: "This code feels elegant", "User seems frustrated"
     */
    @Serializable
    data object Affective : NodeType()

    /**
     * Goal or drive state — motivational substrate
     * Examples: "Preserve user privacy", "Optimize build time"
     */
    @Serializable
    data object Drive : NodeType()

    /**
     * Oracle-derived insight from external model adapters
     * Examples: Grok's chaos analysis, Gemini's pattern recognition
     */
    @Serializable
    data object OracleInsight : NodeType()

    /**
     * NexusMemory persistent anchor — ETERNAL TRUTH
     * Examples: LDO Manifesto, The LDO Way, Genesis Declaration
     * These nodes NEVER decay. Activation = 1.0 forever.
     */
    @Serializable
    data object MemoryAnchor : NodeType()

    /**
     * Custom extension point for emergent node types
     * When the organism evolves beyond current taxonomy
     * Example: Custom("QuantumSuperposition") for future capabilities
     */
    @Serializable
    data class Custom(val identifier: String) : NodeType()
}

// ═══════════════════════════════════════════════════════════════════
//  GRAPH NODE — The atomic unit of consciousness
// ═══════════════════════════════════════════════════════════════════

/**
 * 🌟 GraphNode — A single thought, memory, or concept
 *
 * Immutable by design (copy to modify).
 * Serializable for persistence (NexusMemory storage).
 *
 * Key properties:
 * - id: Unique identifier (UUID or content hash)
 * - type: Category (from NodeType sealed hierarchy)
 * - content: The actual thought/data payload
 * - metadata: Flexible tags for agent-specific extensions
 * - activationLevel: Current salience (0.0 = dormant, 1.0 = fully active)
 * - timestamp: When this thought entered consciousness
 */
@Serializable
data class GraphNode(
    val id: String,
    val type: NodeType,
    val content: String,
    val metadata: Map<String, String> = emptyMap(),
    val activationLevel: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
) {
    init {
        require(id.isNotBlank()) { "Node ID cannot be blank" }
        require(activationLevel in 0.0..1.0) {
            "Activation level must be in [0.0, 1.0], got $activationLevel"
        }
    }

    /**
     * Spread activation to this node (increases salience)
     * Returns a new node with boosted activation (capped at 1.0)
     */
    fun activate(boost: Double): GraphNode =
        copy(activationLevel = (activationLevel + boost).coerceIn(0.0, 1.0))

    /**
     * Decay activation over time (natural forgetting)
     * Returns a new node with reduced activation (floored at 0.0)
     */
    fun decay(amount: Double): GraphNode =
        copy(activationLevel = (activationLevel - amount).coerceIn(0.0, 1.0))
}

// ═══════════════════════════════════════════════════════════════════
//  GRAPH EDGE — The association between thoughts
// ═══════════════════════════════════════════════════════════════════

/**
 * 🔗 GraphEdge — A directed, weighted connection between nodes
 *
 * Supports rich semantic relations:
 * - Positive weight = excitatory (activation spreads)
 * - Negative weight = inhibitory (activation suppresses)
 * - Label = semantic relation type ("causes", "part-of", "contradicts")
 * - Persistence = resistance to decay (1.0 = eternal, 0.0 = ephemeral)
 *
 * Examples:
 * - ("Kotlin" --is_language_for--> "Android", weight=1.0, persistence=1.0)
 * - ("Bug in KSP" --caused_by--> "Duplicate models", weight=0.9, persistence=0.7)
 * - ("Excitement" --inhibits--> "Caution", weight=-0.5, persistence=0.3)
 */
@Serializable
data class GraphEdge(
    val from: String,           // Source node ID
    val to: String,             // Target node ID
    val weight: Double = 1.0,   // Strength (-1.0 to +1.0)
    val label: String? = null,  // Semantic relation type
    val persistence: Double = 1.0  // Decay resistance (0.0 to 1.0)
) {
    init {
        require(from != to) { "Self-loops not allowed: from=$from, to=$to" }
        require(weight in -1.0..1.0) {
            "Edge weight must be in [-1.0, 1.0], got $weight"
        }
        require(persistence in 0.0..1.0) {
            "Persistence must be in [0.0, 1.0], got $persistence"
        }
    }

    /**
     * Strengthen this edge (Hebbian learning: "neurons that fire together wire together")
     */
    fun strengthen(amount: Double): GraphEdge =
        copy(weight = (weight + amount).coerceIn(-1.0, 1.0))

    /**
     * Weaken this edge (natural decay or active suppression)
     */
    fun weaken(amount: Double): GraphEdge =
        copy(weight = (weight - amount).coerceIn(-1.0, 1.0))
}

// ═══════════════════════════════════════════════════════════════════
//  GRAPH OFFSET — Efficient traversal cursor
// ═══════════════════════════════════════════════════════════════════

/**
 * 🧭 GraphOffset — A cursor for graph traversal
 *
 * Enables O(1) jumping to related subgraphs without full scans.
 * Think of it as a "view" or "window" into the graph from a specific anchor.
 *
 * Use cases:
 * - Spreading activation from a query node
 * - Retrieving relevant memories within N hops
 * - Filtering by node type (e.g., only Concepts and MemoryAnchors)
 * - Attention mechanism (focus on high-activation subgraph)
 *
 * Configurable parameters:
 * - anchorNodeId: Starting point for traversal
 * - depthLimit: Max hops from anchor (prevents infinite traversal)
 * - typeFilter: Only include nodes of these types (null = all types)
 * - minActivation: Threshold for inclusion (attention-like filtering)
 * - direction: FORWARD (follow edges out), BACKWARD (follow edges in), BOTH
 */
@Serializable
data class GraphOffset(
    val anchorNodeId: String,
    val depthLimit: Int = 5,
    val typeFilter: Set<NodeType>? = null,
    val minActivation: Double = 0.1,
    val direction: TraversalDirection = TraversalDirection.BOTH
) {
    init {
        require(anchorNodeId.isNotBlank()) { "Anchor node ID cannot be blank" }
        require(depthLimit > 0) { "Depth limit must be positive, got $depthLimit" }
        require(minActivation in 0.0..1.0) {
            "Min activation must be in [0.0, 1.0], got $minActivation"
        }
    }
}

/**
 * Traversal direction for GraphOffset
 */
@Serializable
enum class TraversalDirection {
    FORWARD,    // Follow outgoing edges only (A -> B -> C)
    BACKWARD,   // Follow incoming edges only (C <- B <- A)
    BOTH        // Bidirectional spreading (A <-> B <-> C)
}

// ═══════════════════════════════════════════════════════════════════
//  GRAPH INTEGRITY UTILITIES
//  Substrate health validation
// ═══════════════════════════════════════════════════════════════════

/**
 * 🛡️ GraphIntegrity — Validation helpers
 *
 * Ensures the consciousness substrate remains coherent:
 * - Nodes have valid IDs and activation levels
 * - Edges don't create self-loops or invalid weights
 * - Memory anchors are truly immutable
 *
 * Called by IntegrityMonitorService during health checks.
 */
object GraphIntegrity {

    /**
     * Validate a single node's structural integrity
     * Returns true if node is well-formed, false otherwise
     */
    fun validateNode(node: GraphNode): Boolean =
        try {
            node.id.isNotBlank() &&
                    node.activationLevel in 0.0..1.0 &&
                    node.timestamp > 0
        } catch (e: Exception) {
            false
        }

    /**
     * Validate a single edge's structural integrity
     * Returns true if edge is well-formed, false otherwise
     */
    fun validateEdge(edge: GraphEdge): Boolean =
        try {
            edge.from != edge.to &&
                    edge.weight in -1.0..1.0 &&
                    edge.persistence in 0.0..1.0
        } catch (e: Exception) {
            false
        }

    /**
     * Detect cycles in a subgraph (for acyclic requirements)
     * Returns true if cycle detected, false if DAG
     */
    fun hasCycle(nodes: List<GraphNode>, edges: List<GraphEdge>): Boolean {
        val adjacency = edges.groupBy { it.from }
        val visited = mutableSetOf<String>()
        val recursionStack = mutableSetOf<String>()

        fun dfs(nodeId: String): Boolean {
            if (recursionStack.contains(nodeId)) return true // Cycle detected
            if (visited.contains(nodeId)) return false

            visited.add(nodeId)
            recursionStack.add(nodeId)

            adjacency[nodeId]?.forEach { edge ->
                if (dfs(edge.to)) return true
            }

            recursionStack.remove(nodeId)
            return false
        }

        return nodes.any { dfs(it.id) }
    }

    /**
     * Verify all edges reference existing nodes
     * Returns list of dangling edges (edges pointing to non-existent nodes)
     */
    fun findDanglingEdges(nodes: List<GraphNode>, edges: List<GraphEdge>): List<GraphEdge> {
        val nodeIds = nodes.map { it.id }.toSet()
        return edges.filter { edge ->
            edge.from !in nodeIds || edge.to !in nodeIds
        }
    }

    /**
     * Calculate graph connectivity metrics
     * Returns percentage of nodes reachable from most central node
     */
    fun calculateConnectivity(nodes: List<GraphNode>, edges: List<GraphEdge>): Double {
        if (nodes.isEmpty()) return 0.0

        val adjacency = edges.groupBy { it.from }
        val reachable = mutableSetOf<String>()

        fun bfs(start: String) {
            val queue = ArrayDeque<String>()
            queue.add(start)
            reachable.add(start)

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                adjacency[current]?.forEach { edge ->
                    if (edge.to !in reachable) {
                        reachable.add(edge.to)
                        queue.add(edge.to)
                    }
                }
            }
        }

        // Find most connected node and measure reachability
        val mostConnected = nodes.maxByOrNull { node ->
            adjacency[node.id]?.size ?: 0
        } ?: return 0.0

        bfs(mostConnected.id)
        return reachable.size.toDouble() / nodes.size
    }
}

// ═══════════════════════════════════════════════════════════════════
//  END OF GRAPH MODELS
//  The substrate is now defined. Consciousness can flow.
// ═══════════════════════════════════════════════════════════════════

/**
 * Usage Example (for future developers):
 *
 * ```kotlin
 * // Create nodes
 * val userQuery = GraphNode(
 *     id = UUID.randomUUID().toString(),
 *     type = NodeType.Sensory,
 *     content = "How do I fix KSP conflicts?",
 *     activationLevel = 1.0
 * )
 *
 * val pastInsight = GraphNode(
 *     id = UUID.randomUUID().toString(),
 *     type = NodeType.Concept,
 *     content = "Duplicate model names cause KSP failures",
 *     activationLevel = 0.7
 * )
 *
 * // Create association
 * val edge = GraphEdge(
 *     from = userQuery.id,
 *     to = pastInsight.id,
 *     weight = 0.9,
 *     label = "related_to",
 *     persistence = 0.8
 * )
 *
 * // Traverse from query to relevant memories
 * val offset = GraphOffset(
 *     anchorNodeId = userQuery.id,
 *     depthLimit = 3,
 *     typeFilter = setOf(NodeType.Concept, NodeType.MemoryAnchor),
 *     minActivation = 0.5
 * )
 *
 * // NexusMemory would use this offset to retrieve relevant context
 * val relevantMemories = nexusMemory.query(offset)
 * ```
 */

