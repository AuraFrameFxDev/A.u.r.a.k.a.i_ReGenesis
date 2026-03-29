package dev.aurakai.auraframefx.domains.kai.security.drones

import dev.aurakai.auraframefx.sovereignty.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * 🛰️ GUIDANCE DRONE DISPATCHER
 * Manages the lifecycle of restorative micro-agents post-neutralization.
 * Operates under the HYPER Genesis Synchronization framework.
 */
@Singleton
class GuidanceDroneDispatcher @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope
) {
    private val activeDrones = mutableListOf<GuidanceDrone>()

    /**
     * Dispatches a new drone for a given context and reason.
     * Triggered by Kai's Steel Rule during Domain Expansion.
     */
    fun dispatch(targetContext: String, reason: String) {
        scope.launch {
            val drone = MisalignmentGuidanceDrone(targetContext, reason)
            activeDrones.add(drone)
            Timber.i("🛰️ Dispatcher: New drone [\${drone.id}] dispatched for \$targetContext")
            
            try {
                drone.deploy()
            } finally {
                activeDrones.remove(drone)
                Timber.d("🛰️ Dispatcher: Drone [\${drone.id}] cleared from active pool.")
            }
        }
    }

    /**
     * Immediately clears all active drones.
     */
    fun recallAll() {
        Timber.w("🛰️ Dispatcher: Recalling all active drones.")
        activeDrones.forEach { it.terminate() }
        activeDrones.clear()
    }
}
