package dev.aurakai.regenesis.liveui

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.PI

private fun toDegrees(radians: Double): Double = radians * 180.0 / PI

class SensorFusionManager(private val context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val gameRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)

    private var currentRotationMatrix = FloatArray(9)
    private var currentOrientation = FloatArray(3)
    private var previewListener: ((xDeg: Float, yDeg: Float) -> Unit)? = null

    fun registerForPreview(listener: (xDeg: Float, yDeg: Float) -> Unit) {
        sensorManager.registerListener(this, gameRotationVector, SensorManager.SENSOR_DELAY_GAME)
        previewListener = listener
    }

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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    fun unregister() = sensorManager.unregisterListener(this)
}
