package com.mint.minttracker.mapFragment

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.mint.minttracker.R
import com.mint.minttracker.databinding.FragmentMapBinding
import com.mint.minttracker.models.MintLocation

class MapFragment : MvpAppCompatFragment(), MapView, OnMapReadyCallback {

    @InjectPresenter
    lateinit var mapPresenter: MapPresenter

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
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
        binding.stop.text = getString(R.string.stop)
        binding.stop.setOnClickListener {
            mapPresenter.stopButtonPressed()
        }

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
        map = googleMap
    }

    override fun drawPolyline(polylineOptions: PolylineOptions) {
//        googleMap.addPolyline(polylineOptions)//todo
    }

    override fun showData(mintLocation: MintLocation) {
        binding.timeData.text = mintLocation.time.toString()
        binding.latitudeData.text = mintLocation.lat.toString()
        binding.longitudeData.text = mintLocation.lon.toString()
        binding.altitudeData.text = mintLocation.altitude.toInt().toString()
        binding.speedData.text = mintLocation.speed.toString()
        binding.bearingData.text = mintLocation.bearing.toInt().toString()
        binding.accuracyData.text = mintLocation.accuracy.toString()
    }

    override fun showCurrentLocation(location: Pair<Double, Double>) {
        val loc = LatLng(location.first, location.second)
        map.addMarker(
            MarkerOptions()
                .position(loc)
                .title("Marker")
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f))
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
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }


}