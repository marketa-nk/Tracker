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
import androidx.navigation.findNavController
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.mint.minttracker.R
import com.mint.minttracker.databinding.FragmentMapBinding
import com.mint.minttracker.historyFragment.round
import com.mint.minttracker.models.MintLocation
import java.text.DateFormat
import java.text.DateFormat.getDateTimeInstance

class MapFragment : MvpAppCompatFragment(), MapView, OnMapReadyCallback {

    @InjectPresenter
    lateinit var mapPresenter: MapPresenter

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var map: GoogleMap? = null

    private var polyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate Nata")

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                mapPresenter.permissionGranted(true)
            } else {
                mapPresenter.permissionGranted(false)
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
        mapPresenter.appIsResumed()
        println("onResume Nata")
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        mapPresenter.appIsPaused()
        println("onPause Nata")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        binding.timeText.text = getString(R.string.time)
        binding.latitudeText.text = getString(R.string.latitude)
        binding.longitudeText.text = getString(R.string.longitude)
        binding.altitudeText.text = getString(R.string.altitude)
        binding.speedText.text = getString(R.string.speed)
        binding.bearingText.text = getString(R.string.bearing)
        binding.accuracyText.text = getString(R.string.accuracy)

        binding.start.text = getString(R.string.start)
        binding.start.setOnClickListener {
            mapPresenter.startButtonPressed()
        }
        binding.pause.text = getString(R.string.pause)
        binding.pause.setOnClickListener {
            mapPresenter.pauseButtonPressed()
        }
        binding.resume.text = getString(R.string.resume)
        binding.resume.setOnClickListener {
            mapPresenter.resumeButtonPressed()
        }
        binding.stop.text = getString(R.string.stop)
        binding.stop.setOnClickListener {
            mapPresenter.stopButtonPressed()
        }
        binding.history.setOnClickListener {
            mapPresenter.historyButtonPressed()
        }
        println("onCreateView Nata")

        return binding.root
    }

    override fun requireLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                mapPresenter.permissionGranted(true)
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Данному приложению требуется разрешение на местоположения")
                    .setMessage("Показать диалог с запросом разрешения?")
                    .setPositiveButton("Да") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("Нет, спасибо") { _, _ ->
                        mapPresenter.permissionGranted(false)
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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap.apply {
            isMyLocationEnabled = !(ActivityCompat.checkSelfPermission(requireContext().applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext().applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            uiSettings.isZoomControlsEnabled = true
        }
        addPolyline()

    }

    //todo название метода не соотвествует коду - done
    private fun addPolyline() {
        polyline = map?.addPolyline(
            PolylineOptions()
                .color(Color.RED)
                .width(10.0F)
        )
    }

    override fun updatePolyline(points: List<LatLng>) {
        polyline?.points = points
    }

    private fun View.changeVisibility(visibility: Boolean) {
        this.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }

    override fun visibilityStartButton(visibility: Boolean) {
        binding.start.changeVisibility(visibility)
    }

    override fun visibilityPauseButton(visibility: Boolean) {
        binding.pause.changeVisibility(visibility)
    }

    override fun visibilityResumeButton(visibility: Boolean) {
        binding.resume.changeVisibility(visibility)
    }

    override fun visibilityStopButton(visibility: Boolean) {
        binding.stop.changeVisibility(visibility)
    }

    override fun showData(mintLocation: MintLocation) {
        binding.timeData.text = getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(mintLocation.time)
//        binding.timeData.text = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(mintLocation.time)
        binding.latitudeData.text = "${(mintLocation.lat)}"
        binding.longitudeData.text ="${(mintLocation.lon)}"
        binding.altitudeData.text ="${(mintLocation.altitude).round()}"
        binding.speedData.text = "${(mintLocation.speed * 3.6).round()}"
        binding.bearingData.text = "${(mintLocation.bearing.toDouble()).round()}"
        binding.accuracyData.text ="${(mintLocation.accuracy.toDouble()).round()}"
    }

    override fun navigateToHistoryFragment() {
        binding.root.findNavController().navigate(R.id.action_fragment_map_to_historyFragment)
    }

    override fun showLocation(location: Pair<Double, Double>) {
        val loc = LatLng(location.first, location.second)
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 17.0f))
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