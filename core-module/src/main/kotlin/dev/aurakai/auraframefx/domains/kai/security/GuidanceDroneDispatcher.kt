package dev.aurakai.auraframefx.domains.kai.security

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🚁 GUIDANCE DRONE DISPATCHER
 */
@Singleton
class GuidanceDroneDispatcher @Inject constructor() {

    private val activeDrones = mutableListOf<GuidanceDrone>()

    fun dispatchDrone(type: GuidanceDrone.DroneType, objective: String): GuidanceDrone {
        val drone = GuidanceDrone(type = type, objective = objective)
        activeDrones.add(drone)
        drone.deploy()
        Timber.d("🚁 GuidanceDroneDispatcher: Drone dispatched, total active: ${activeDrones.size}")
        return drone
    }

    fun recallAllDrones() {
        Timber.i("🚁 GuidanceDroneDispatcher: Recalling all drones")
        activeDrones.clear()
    }
}
