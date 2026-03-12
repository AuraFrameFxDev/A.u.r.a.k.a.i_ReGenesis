package dev.aurakai.auraframefx.domains.cascade.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI

/**
 * Gyroscope Manager
 *
 * Manages device gyroscope sensor for 3D rotation controls
 * Used in customization editor for tilting phone to rotate preview
 */
@Singleton
open class GyroscopeManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var rotationX by mutableStateOf(0f)
        private set
    var rotationY by mutableStateOf(0f)
        private set
    var rotationZ by mutableStateOf(0f)
        private set

    data class RotationAngles(
        val pitch: Float,  // X-axis rotation (forward/backward tilt)
        val roll: Float,   // Y-axis rotation (left/right tilt)
        val yaw: Float     // Z-axis rotation (twist)
    )

    /**
     * Check if gyroscope sensor is available
     */
    fun isGyroscopeAvailable(): Boolean {
        return gyroscope != null
    }

    /**
     * Get rotation angles as Flow
     * Updates continuously based on gyroscope sensor
     */
    fun getRotationFlow(): Flow<RotationAngles> = callbackFlow {
        val listener = object : SensorEventListener {
            private var timestamp = 0L

            // Rotation angles (in radians)
            private var angleX = 0f
            private var angleY = 0f
            private var angleZ = 0f

            override fun onSensorChanged(event: SensorEvent) {
                if (timestamp == 0L) {
                    timestamp = event.timestamp
                    return
                }

                // Calculate time delta in seconds
                val dt = (event.timestamp - timestamp) * 1.0e-9f
                timestamp = event.timestamp

                // Gyroscope values are in rad/s
                val axisX = event.values[0]  // Rotation around X-axis
                val axisY = event.values[1]  // Rotation around Y-axis
                val axisZ = event.values[2]  // Rotation around Z-axis

                // Integrate angular velocity to get rotation angles
                angleX += axisX * dt
                angleY += axisY * dt
                angleZ += axisZ * dt

                // Convert to degrees
                val pitchDegrees = (angleX * 180 / PI).toFloat()
                val rollDegrees = (angleY * 180 / PI).toFloat()
                val yawDegrees = (angleZ * 180 / PI).toFloat()

                // Update state
                rotationX = pitchDegrees
                rotationY = rollDegrees
                rotationZ = yawDegrees

                // Emit to flow
                trySend(
                    RotationAngles(
                        pitch = pitchDegrees,
                        roll = rollDegrees,
                        yaw = yawDegrees
                    )
                )

                Timber.v("Gyroscope: pitch=$pitchDegrees°, roll=$rollDegrees°, yaw=$yawDegrees°")
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                Timber.d("Gyroscope accuracy changed: $accuracy")
            }
        }

        // Register gyroscope listener
        if (gyroscope != null) {
            sensorManager.registerListener(
                listener,
                gyroscope,
                SensorManager.SENSOR_DELAY_GAME // 20ms updates (50 FPS)
            )
            Timber.i("✅ Gyroscope listener registered")
        } else {
            Timber.w("❌ Gyroscope sensor not available")
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
            Timber.i("Gyroscope listener unregistered")
        }
    }

    /**
     * Get device orientation from accelerometer
     * Used as fallback when gyroscope is not available
     */
    fun getOrientationFlow(): Flow<RotationAngles> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // Get gravity values
                val gX = event.values[0]
                val gY = event.values[1]
                val gZ = event.values[2]

                // Calculate pitch and roll from gravity
                val pitch = Math.atan2(gX.toDouble(), Math.sqrt((gY * gY + gZ * gZ).toDouble()))
                    .toFloat() * 180 / PI.toFloat()

                val roll = Math.atan2(gY.toDouble(), Math.sqrt((gX * gX + gZ * gZ).toDouble()))
                    .toFloat() * 180 / PI.toFloat()

                trySend(
                    RotationAngles(
                        pitch = pitch,
                        roll = roll,
                        yaw = 0f // Accelerometer doesn't provide yaw
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        if (accelerometer != null) {
            sensorManager.registerListener(
                listener,
                accelerometer,
                SensorManager.SENSOR_DELAY_GAME
            )
            Timber.i("✅ Accelerometer listener registered (orientation fallback)")
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    /**
     * Reset rotation angles to zero
     */
    fun reset() {
        rotationX = 0f
        rotationY = 0f
        rotationZ = 0f
        Timber.d("Gyroscope rotation reset")
    }
}

