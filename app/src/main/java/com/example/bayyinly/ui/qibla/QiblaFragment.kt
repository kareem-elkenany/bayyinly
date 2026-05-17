package com.example.bayyinly.ui.qibla

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bayyinly.databinding.FragmentQiblaBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.tan

class QiblaFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentQiblaBinding? = null
    private val binding get() = _binding!!

    private lateinit var sensorManager: SensorManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val rotationMatrix = FloatArray(9)
    private val adjustedRotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private var hasAccelerometerReading = false
    private var hasMagnetometerReading = false
    private var usingRotationVector = false
    private var hasRegisteredSensors = false
    private var isCompassAccuracyLow = false

    private var currentHeading: Float? = null
    private var qiblaBearing: Float? = null
    private var magneticDeclination = 0f

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (_binding == null) return@registerForActivityResult

        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            requestDeviceLocation()
        } else {
            binding.tvQiblaStatus.text = "Location permission is needed to calculate the Qibla from where you are."
            binding.tvLocationDetails.text = "Location permission denied."
            refreshQiblaUi()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQiblaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.btnRequestLocation.setOnClickListener {
            checkLocationPermission()
        }

        checkLocationPermission()
        refreshQiblaUi()
    }

    override fun onResume() {
        super.onResume()
        registerCompassSensors()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        hasRegisteredSensors = false
    }

    private fun checkLocationPermission() {
        if (hasLocationPermission()) {
            requestDeviceLocation()
        } else {
            binding.btnRequestLocation.text = "Allow location"
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun hasLocationPermission(): Boolean {
        val context = context ?: return false
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun requestDeviceLocation() {
        if (!hasLocationPermission()) return

        binding.btnRequestLocation.isEnabled = false
        binding.btnRequestLocation.text = "Locating..."
        binding.tvQiblaStatus.text = "Getting your location for an accurate Qibla direction."

        val priority = if (
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
            .addOnSuccessListener { location ->
                if (location != null) {
                    updateLocation(location)
                } else {
                    requestLastKnownLocation()
                }
            }
            .addOnFailureListener {
                requestLastKnownLocation()
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestLastKnownLocation() {
        if (!hasLocationPermission()) return

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (!isAdded || _binding == null) return@addOnSuccessListener
                if (location != null) {
                    updateLocation(location)
                } else {
                    binding.tvLocationDetails.text = "Location unavailable. Try again near a window or make sure Location is enabled."
                    binding.tvQiblaStatus.text = "Location is needed before the Qibla arrow can be calculated."
                    binding.btnRequestLocation.isEnabled = true
                    binding.btnRequestLocation.text = "Retry location"
                    qiblaBearing = null
                    refreshQiblaUi()
                }
            }
            .addOnFailureListener {
                if (!isAdded || _binding == null) return@addOnFailureListener
                binding.tvLocationDetails.text = "Location unavailable. Try again after enabling Location."
                binding.tvQiblaStatus.text = "Location is needed before the Qibla arrow can be calculated."
                binding.btnRequestLocation.isEnabled = true
                binding.btnRequestLocation.text = "Retry location"
                qiblaBearing = null
                refreshQiblaUi()
            }
    }

    private fun updateLocation(location: Location) {
        if (!isAdded || _binding == null) return

        qiblaBearing = calculateQiblaBearing(location.latitude, location.longitude)
        magneticDeclination = GeomagneticField(
            location.latitude.toFloat(),
            location.longitude.toFloat(),
            location.altitude.toFloat(),
            System.currentTimeMillis()
        ).declination

        val accuracyText = if (location.hasAccuracy()) {
            "Accuracy: ${location.accuracy.roundToInt()} m"
        } else {
            "Accuracy unavailable"
        }

        binding.tvLocationDetails.text =
            "Location: %.4f, %.4f\n%s".format(location.latitude, location.longitude, accuracyText)
        binding.btnRequestLocation.isEnabled = true
        binding.btnRequestLocation.text = "Refresh location"
        refreshQiblaUi()
    }

    private fun registerCompassSensors() {
        if (!::sensorManager.isInitialized || hasRegisteredSensors) return

        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationVectorSensor != null) {
            usingRotationVector = true
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
            hasRegisteredSensors = true
            return
        }

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (accelerometer != null && magnetometer != null) {
            usingRotationVector = false
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
            hasRegisteredSensors = true
        } else if (_binding != null) {
            binding.tvQiblaStatus.text = "Compass hardware is unavailable on this device."
            refreshQiblaUi()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                updateHeading(rotationMatrix)
            }

            Sensor.TYPE_ACCELEROMETER -> {
                copySensorValues(event.values, accelerometerReading, hasAccelerometerReading)
                hasAccelerometerReading = true
                updateHeadingFromAccelerometerAndMagnetometer()
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                copySensorValues(event.values, magnetometerReading, hasMagnetometerReading)
                hasMagnetometerReading = true
                updateHeadingFromAccelerometerAndMagnetometer()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (sensor?.type == Sensor.TYPE_MAGNETIC_FIELD || sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            isCompassAccuracyLow = accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE ||
                accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW
            refreshQiblaUi()
        }
    }

    private fun copySensorValues(input: FloatArray, output: FloatArray, hasPreviousReading: Boolean) {
        for (index in output.indices) {
            output[index] = if (hasPreviousReading) {
                output[index] + SENSOR_SMOOTHING_ALPHA * (input[index] - output[index])
            } else {
                input[index]
            }
        }
    }

    private fun updateHeadingFromAccelerometerAndMagnetometer() {
        if (!hasAccelerometerReading || !hasMagnetometerReading) return
        val hasRotationMatrix = SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        if (hasRotationMatrix) {
            updateHeading(rotationMatrix)
        }
    }

    private fun updateHeading(sourceRotationMatrix: FloatArray) {
        val displayRotation = binding.root.display?.rotation ?: Surface.ROTATION_0
        val matrixForOrientation = when (displayRotation) {
            Surface.ROTATION_90 -> {
                SensorManager.remapCoordinateSystem(
                    sourceRotationMatrix,
                    SensorManager.AXIS_Y,
                    SensorManager.AXIS_MINUS_X,
                    adjustedRotationMatrix
                )
                adjustedRotationMatrix
            }

            Surface.ROTATION_180 -> {
                SensorManager.remapCoordinateSystem(
                    sourceRotationMatrix,
                    SensorManager.AXIS_MINUS_X,
                    SensorManager.AXIS_MINUS_Y,
                    adjustedRotationMatrix
                )
                adjustedRotationMatrix
            }

            Surface.ROTATION_270 -> {
                SensorManager.remapCoordinateSystem(
                    sourceRotationMatrix,
                    SensorManager.AXIS_MINUS_Y,
                    SensorManager.AXIS_X,
                    adjustedRotationMatrix
                )
                adjustedRotationMatrix
            }

            else -> sourceRotationMatrix
        }

        SensorManager.getOrientation(matrixForOrientation, orientationAngles)
        val magneticHeading = normalizeDegrees(Math.toDegrees(orientationAngles[0].toDouble()).toFloat())
        val trueHeading = normalizeDegrees(magneticHeading + magneticDeclination)
        currentHeading = smoothHeading(currentHeading, trueHeading)
        refreshQiblaUi()
    }

    private fun refreshQiblaUi() {
        if (_binding == null) return

        val heading = currentHeading
        val bearing = qiblaBearing
        binding.qiblaCompassView.updateDirections(heading, bearing)
        binding.tvCurrentHeading.text = heading?.let { "${it.roundToInt()}°" } ?: "--°"
        binding.tvQiblaBearing.text = bearing?.let { "${it.roundToInt()}°" } ?: "--°"

        when {
            bearing == null -> {
                binding.tvTurnInstruction.text = "Use location to calculate Qibla."
                if (binding.tvQiblaStatus.text.isBlank()) {
                    binding.tvQiblaStatus.text = "Location is needed before the Qibla arrow can be calculated."
                }
            }

            heading == null -> {
                binding.tvTurnInstruction.text = "Move your phone to start the compass."
                binding.tvQiblaStatus.text = "Keep the phone flat and away from magnets or metal."
            }

            else -> {
                val turnDegrees = signedDegreesBetween(heading, bearing)
                val absoluteTurn = abs(turnDegrees).roundToInt()
                binding.tvTurnInstruction.text = when {
                    absoluteTurn <= QIBLA_ALIGNMENT_TOLERANCE_DEGREES -> "You are facing the Qibla"
                    turnDegrees > 0 -> "Turn $absoluteTurn° right"
                    else -> "Turn $absoluteTurn° left"
                }

                binding.tvQiblaStatus.text = if (isCompassAccuracyLow) {
                    "Compass accuracy is low. Move your phone in a figure-eight to calibrate it."
                } else if (usingRotationVector) {
                    "Point the top of your phone toward the green arrow."
                } else {
                    "Using accelerometer and magnetometer. Keep the phone flat for best accuracy."
                }

                binding.qiblaCompassView.contentDescription =
                    "Qibla compass. ${binding.tvTurnInstruction.text}. Qibla bearing ${bearing.roundToInt()} degrees. Current heading ${heading.roundToInt()} degrees."
            }
        }
    }

    private fun calculateQiblaBearing(latitude: Double, longitude: Double): Float {
        val userLatitude = Math.toRadians(latitude)
        val userLongitude = Math.toRadians(longitude)
        val kaabaLatitude = Math.toRadians(KAABA_LATITUDE)
        val kaabaLongitude = Math.toRadians(KAABA_LONGITUDE)
        val deltaLongitude = kaabaLongitude - userLongitude

        val y = sin(deltaLongitude)
        val x = cos(userLatitude) * tan(kaabaLatitude) -
            sin(userLatitude) * cos(deltaLongitude)

        return normalizeDegrees(Math.toDegrees(atan2(y, x)).toFloat())
    }

    private fun smoothHeading(previous: Float?, next: Float): Float {
        if (previous == null) return next
        return normalizeDegrees(previous + signedDegreesBetween(previous, next) * HEADING_SMOOTHING_ALPHA)
    }

    private fun signedDegreesBetween(from: Float, to: Float): Float {
        return ((to - from + 540f) % 360f) - 180f
    }

    private fun normalizeDegrees(value: Float): Float {
        return ((value % 360f) + 360f) % 360f
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KAABA_LATITUDE = 21.422487
        private const val KAABA_LONGITUDE = 39.826206
        private const val SENSOR_SMOOTHING_ALPHA = 0.15f
        private const val HEADING_SMOOTHING_ALPHA = 0.18f
        private const val QIBLA_ALIGNMENT_TOLERANCE_DEGREES = 5
    }
}
