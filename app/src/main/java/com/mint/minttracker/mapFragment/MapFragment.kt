package com.mint.minttracker.mapFragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.mint.minttracker.App
import com.mint.minttracker.R
import com.mint.minttracker.databinding.FragmentMapBinding
import com.mint.minttracker.domain.buttonControl.ButtonState
import com.mint.minttracker.historyFragment.round
import com.mint.minttracker.mapFragment.MapViewModel.Companion.STATUS_FINISHED
import com.mint.minttracker.mapFragment.MapViewModel.Companion.STATUS_PAUSED
import com.mint.minttracker.mapFragment.MapViewModel.Companion.STATUS_RESUMED
import com.mint.minttracker.mapFragment.MapViewModel.Companion.STATUS_STARTED
import com.mint.minttracker.models.MintLocation
import java.text.DateFormat
import javax.inject.Inject

class MapFragment : Fragment() {

    @Inject
    lateinit var factory: MapViewModel.MapViewModelFactory

    private val viewModel: MapViewModel by viewModels { factory }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var polyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate Nata")
        App.instance.appComponent.injectMapFragment(this)

        viewModel.message.observe(this, { navigateToHistoryFragment() }) //todo nata: как по другому делать переходы на другие фрагменты

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.permissionGranted(true)
            } else {
                viewModel.permissionGranted(false)
            }
        }
        requireLocationPermission()
    }

    private fun addSettingsToMap() {
        binding.mapView.getMapAsync { googleMap ->
            googleMap.apply {
                isMyLocationEnabled = !(ActivityCompat.checkSelfPermission(requireContext().applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext().applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                uiSettings.isMyLocationButtonEnabled = true
                uiSettings.isZoomControlsEnabled = true
                uiSettings.isCompassEnabled = true
                setOnMyLocationButtonClickListener {
                    viewModel.myLocationButtonIsClicked()
                    false
                }
                setOnCameraMoveStartedListener {
                    if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                        viewModel.cameraIsMovedByGesture()
                    }
                }
                polyline = addPolyline()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        println("onStart Nata")
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        println("onResume Nata")
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        println("onPause Nata")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.mapView.onCreate(savedInstanceState)

        viewModel.pointsLiveData.observe(this.viewLifecycleOwner, { points -> updatePolyline(points) })
        viewModel.mintLocation.observe(this.viewLifecycleOwner, { location ->
            showData(location)
            binding.mapView.getMapAsync { googleMap ->
                googleMap.apply {
                    animateCamera(CameraUpdateFactory.newLatLngZoom(location.latLng, 17.0f))
                }
            }
        })
        viewModel.buttonState.observe(this.viewLifecycleOwner, { buttonState -> setButtonState(buttonState) })
        viewModel.grantedPerm.observe(this.viewLifecycleOwner, { granted ->
            if (granted) {
                addSettingsToMap()
            }
        })

        binding.timeText.text = getString(R.string.time)
        binding.latitudeText.text = getString(R.string.latitude)
        binding.longitudeText.text = getString(R.string.longitude)
        binding.altitudeText.text = getString(R.string.altitude)
        binding.speedText.text = getString(R.string.speed)
        binding.bearingText.text = getString(R.string.bearing)
        binding.accuracyText.text = getString(R.string.accuracy)

        binding.start.setOnClickListener {
            viewModel.controlButtonPressed(STATUS_STARTED)

        }
        binding.pause.setOnClickListener {
            viewModel.controlButtonPressed(STATUS_PAUSED)

        }
        binding.resume.setOnClickListener {
            viewModel.controlButtonPressed(STATUS_RESUMED)

        }
        binding.stop.setOnClickListener {
            viewModel.controlButtonPressed(STATUS_FINISHED)

        }
        binding.history.setOnClickListener {
            viewModel.historyButtonPressed()
        }
        println("onCreateView Nata")

        return binding.root
    }

    private fun requireLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                viewModel.permissionGranted(true)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Данному приложению требуется разрешение на местоположения")
                    .setMessage("Показать диалог с запросом разрешения?")
                    .setPositiveButton("Да") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("Нет, спасибо") { _, _ ->
                        viewModel.permissionGranted(false)
                    }
                    .create()
                    .show()
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun GoogleMap.addPolyline(): Polyline {
        return addPolyline(
            PolylineOptions()
                .color(Color.RED)
                .width(10.0F)
        )
    }

    private fun updatePolyline(points: List<LatLng>) {
        polyline?.points = points
    }

    private fun View.changeVisibility(enabled: Boolean) {
        this.isEnabled = enabled
    }

    private fun visibilityStartButton(visibility: Boolean) {
        binding.start.changeVisibility(visibility)
    }

    private fun visibilityPauseButton(visibility: Boolean) {
        binding.pause.changeVisibility(visibility)
    }

    private fun visibilityResumeButton(visibility: Boolean) {
        binding.resume.changeVisibility(visibility)
    }

    private fun visibilityStopButton(visibility: Boolean) {
        binding.stop.changeVisibility(visibility)
    }

    private fun setButtonState(buttonState: ButtonState) {
        visibilityStartButton(buttonState.start)
        visibilityPauseButton(buttonState.pause)
        visibilityResumeButton(buttonState.resume)
        visibilityStopButton(buttonState.stop)
    }

    private fun showData(mintLocation: MintLocation) {
        binding.timeData.text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(mintLocation.time)
        binding.latitudeData.text = "${(mintLocation.lat)}"
        binding.longitudeData.text = "${(mintLocation.lon)}"
        binding.altitudeData.text = "${(mintLocation.altitude).round()}"
        binding.speedData.text = "${(mintLocation.speedInKm).toDouble().round()}"
        binding.bearingData.text = "${(mintLocation.bearing.toDouble()).round()}"
        binding.accuracyData.text = "${(mintLocation.accuracy.toDouble()).round()}"
    }

    private fun navigateToHistoryFragment() {
        binding.root.findNavController().navigate(R.id.action_fragment_map_to_historyFragment)
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //todo check crash after rotate
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        println("onStop Nata")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
        println("onDestroy Nata")
    }
}