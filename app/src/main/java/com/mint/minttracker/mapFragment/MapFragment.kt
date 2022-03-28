package com.mint.minttracker.mapFragment

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import com.mint.minttracker.App
import com.mint.minttracker.R
import com.mint.minttracker.databinding.FragmentMapBinding
import com.mint.minttracker.domain.buttonControl.ButtonState
import com.mint.minttracker.models.MintLocation
import com.mint.minttracker.models.Status
import com.mint.minttracker.saveDialogFragment.SaveDialogFragment
import com.mint.minttracker.secToUiString
import com.mint.minttracker.toUiString
import javax.inject.Inject

class MapFragment : Fragment(), SaveDialogFragment.SaveDialogListener {

    @Inject
    lateinit var factory: MapViewModel.MapViewModelFactory

    private val viewModel: MapViewModel by viewModels { factory }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var mapView: MapView? = null
    private var polyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate Nata")
        App.instance.appComponent.injectMapFragment(this)

        viewModel.showHistoryEvent.observe(this) {
            if (it == MapViewModel.SHOW_HISTORY_FRAGMENT) {
                navigateToHistoryFragment()
            }
        }
        viewModel.showSaveDialog.observe(this) {
            if (it == MapViewModel.SHOW_SAVE_DIALOG) {
                val dialog = SaveDialogFragment()
                dialog.show(childFragmentManager, "SaveDialogFragment")
            }
        }

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.permissionGranted(true)
            } else {
                viewModel.permissionGranted(false)
            }
        }
    }

    private fun addSettingsToMap() {
        mapView?.getMapAsync { googleMap ->
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
        mapView?.onStart()
        println("onStart Nata")
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
        println("onResume Nata")
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
        println("onPause Nata")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        if (mapView == null) {
            mapView = MapView(requireContext())
            mapView?.onCreate(savedInstanceState)
        }
        binding.mapViewContainer.addView(mapView!!)

        viewModel.pointsLiveData.observe(this.viewLifecycleOwner) { points ->
            updatePolyline(points)
        }
        viewModel.lastLocation.observe(this.viewLifecycleOwner) { location ->
            showData(location)
            mapView?.getMapAsync { googleMap ->
                googleMap.apply {
                    animateCamera(CameraUpdateFactory.newLatLngZoom(location.latLng, 17.0f))
                }
            }
        }
        viewModel.buttonState.observe(this.viewLifecycleOwner) { buttonState ->
            setButtonState(buttonState)
        }
        viewModel.grantedPerm.observe(this.viewLifecycleOwner) { granted ->
            if (granted) {
                addSettingsToMap()
                binding.fakeStart.isVisible = false
            } else {
                binding.fakeStart.isVisible = true
            }
        }
        viewModel.requireLocationPermissionEvent.observe(this.viewLifecycleOwner) {
            requireLocationPermission()
        }

        viewModel.time.observe(this.viewLifecycleOwner) {
            binding.totalTimeData.text = it.secToUiString()
        }
        viewModel.distance.observe(this.viewLifecycleOwner) {
            binding.distanceData.text = (it / 1000).toUiString()
        }
        viewModel.startBlinkingAnimation.observe(this.viewLifecycleOwner) {
            startBlinkingAnimation(it)
        }
        viewModel.vibrate.observe(this.viewLifecycleOwner) {
            vibrate()
        }
        viewModel.messageSaveEvent.observe(this.viewLifecycleOwner) { message -> showToast(message) }
        viewModel.messageDeleteEvent.observe(this.viewLifecycleOwner) { message -> showToast(message) }

        binding.start.setOnClickListener {
            viewModel.controlButtonPressed(Status.STATUS_STARTED)
        }

        binding.pause.setOnClickListener {
            viewModel.controlButtonPressed(Status.STATUS_PAUSED)
        }

        binding.resume.setOnClickListener {
            viewModel.controlButtonPressed(Status.STATUS_RESUMED)
        }

        binding.stop.setOnClickListener {
            viewModel.controlButtonPressed(Status.STATUS_FINISHED)

        }
        binding.history.setOnClickListener {
            viewModel.historyButtonPressed()
        }
        binding.fakeStart.setOnClickListener {
            viewModel.fakeStartPressed()
        }
        println("onCreateView Nata")

        return binding.root
    }

    private fun requireLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.permissionGranted(true)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionAlertDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun showPermissionAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("This app needs location permission.")
            .setMessage("Show location permission dialog?")
            .setPositiveButton("Yes") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .setNegativeButton("No, thanks") { _, _ ->
                viewModel.permissionGranted(false)
            }
            .create()
            .show()
    }

    private fun GoogleMap.addPolyline(): Polyline {
        return addPolyline(
            PolylineOptions()
                .color(Color.parseColor("#FF6D00"))
                .width(15.0F)
        )
    }

    private fun updatePolyline(points: List<LatLng>) {
        polyline?.points = points
    }

    private fun View.changeVisibility(enabled: Boolean) {
        this.isVisible = enabled
        if (enabled) {
            this.animate().alpha(1f).duration = 500
            this.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.bounce_up))
        } else {
            this.animate().alpha(0f).duration = 500
            this.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.bounce_down))
        }
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

    private fun startBlinkingAnimation(start: Boolean) {
        if (start) {
            binding.constraintLayoutMetrics.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.blink))
        } else {
            binding.constraintLayoutMetrics.clearAnimation()
        }
    }

    private fun vibrate() {
        val vibrator = getSystemService(requireContext(), Vibrator::class.java)
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= 26) {
                it.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(100)
            }
        }
    }

    private fun showData(mintLocation: MintLocation) {
        binding.speedData.text = (mintLocation.speedInKm).toDouble().toUiString()
    }

    private fun navigateToHistoryFragment() {
        binding.root.findNavController().navigate(R.id.action_fragment_map_to_historyFragment)
    }

    private fun showToast(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDialogPositiveClick() {
        viewModel.onDialogPositiveClick()
    }

    override fun onDialogNegativeClick() {
        viewModel.onDialogNegativeClick()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
        println("onStop Nata")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapViewContainer.removeAllViews()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        mapView = null
        println("onDestroy Nata")
    }
}