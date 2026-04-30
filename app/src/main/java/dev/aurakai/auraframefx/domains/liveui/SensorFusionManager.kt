package dev.aurakai.auraframefx.domains.liveui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.PI

/**
 * Convert an angle in radians to degrees.
 *
 * @param radians Angle in radians.
 * @return Equivalent angle in degrees.
 */
private fun toDegrees(radians: Double): Double = radians * 180.0 / PI

class SensorFusionManager(private val context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val gameRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)

    private var currentRotationMatrix = FloatArray(9)
    private var currentOrientation = FloatArray(3)
    private var previewListener: ((xDeg: Float, yDeg: Float) -> Unit)? = null

    /**
     * Begin delivering clamped pitch and roll (in degrees) to the provided preview listener.
     *
     * Registers this manager as a listener for the device `TYPE_GAME_ROTATION_VECTOR` sensor and stores
     * the provided callback. While registered, the callback will be invoked with `xDeg` and `yDeg`
     * representing pitch and roll in degrees, each coerced into the range [-3f, 3f]. Call `unregister()`
     * to stop updates.
     *
     * @param listener Callback receiving `xDeg` (pitch in degrees) and `yDeg` (roll in degrees), both clamped to [-3f, 3f].
     */
    fun registerForPreview(listener: (xDeg: Float, yDeg: Float) -> Unit) {
        sensorManager.registerListener(this, gameRotationVector, SensorManager.SENSOR_DELAY_GAME)
        previewListener = listener
    }

    /**
     * Processes game rotation vector sensor updates and forwards clamped pitch and roll angles to the preview listener.
     *
     * Converts the incoming rotation vector to orientation angles, converts pitch and roll to degrees, clamps each to the range [-3f, 3f], and invokes the registered preview listener with (xDeg, yDeg) when the event is of type `TYPE_GAME_ROTATION_VECTOR`.
     *
     * @param event The sensor event received from the system; only `TYPE_GAME_ROTATION_VECTOR` events are processed. 
     */
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(currentRotationMatrix, event.values)
            SensorManager.getOrientation(currentRotationMatrix, currentOrientation)

            val pitchDeg = toDegrees(currentOrientation[1].toDouble()).toFloat()
            val rollDeg = toDegrees(currentOrientation[2].toDouble()).toFloat()

            val clampedX = pitchDeg.coerceIn(-3f, 3f)
            val clampedY = rollDeg.coerceIn(-3f, 3f)

            previewListener?.invoke(clampedX, clampedY)
        }
    }

    /**
 * Ignores sensor accuracy change notifications.
 *
 * @param sensor The sensor reporting the accuracy change, or `null` if unknown.
 * @param accuracy The new accuracy level, one of the `SensorManager.SENSOR_STATUS_*` constants.
 */
override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    /**
 * Stops receiving sensor updates by unregistering this SensorEventListener from the SensorManager.
 *
 * This does not clear the registered preview listener callback.
 */
fun unregister() = sensorManager.unregisterListener(this)
}
