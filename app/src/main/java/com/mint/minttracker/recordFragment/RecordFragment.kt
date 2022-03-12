package com.mint.minttracker.recordFragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.mint.minttracker.App
import com.mint.minttracker.databinding.FragmentRecordBinding
import com.mint.minttracker.models.Record
import com.mint.minttracker.msToUiString
import com.mint.minttracker.toUiString
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RecordFragment : Fragment() {

    @Inject
    lateinit var factory: RecordViewModelFactory.Factory

    private val viewModel: RecordViewModel by viewModels {
        factory.create(requireArguments().getParcelable(ARG_RECORD)!!)
    }

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectRecordFragment(this)

        viewModel.data.observe(this, { record -> showRecordInfo(record) })
        viewModel.points.observe(this, { points -> showPolyline(points) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync { googleMap ->
            googleMap.apply {
                uiSettings.isZoomControlsEnabled = true
            }
        }

        binding.myToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        println("onStart RecordFragment Nata")
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
        println("onResume RecordFragment Nata")
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        println("onPause RecordFragment Nata")
    }

    private fun showPolyline(list: List<LatLng>) {
        val bounds = LatLngBounds.Builder()
            .also { builder ->
                list.forEach { builder.include(it) }
            }.build()

        binding.mapView.getMapAsync { map ->
            map.addPolyline(list)
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        }
    }

    private fun GoogleMap.addPolyline(list: List<LatLng>): Polyline {
        return addPolyline(
            PolylineOptions()
                .color(Color.RED)
                .width(10.0F)
                .addAll(list)
        )
    }

    private fun showRecordInfo(record: Record) {
        binding.date.text = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(record.date)
        binding.timeText.text = record.totalTimeMs.msToUiString()
        binding.distanceText.text = "${(record.distance).toUiString()}м"
        binding.speedAveText.text = "${(record.aveSpeedInKm).toUiString()}км/ч"
        binding.speedMaxText.text = "${(record.maxSpeedInKm).toUiString()}км/ч"
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
        println("onStop RecordFragment Nata")
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
        println("onDestroy RecordFragment Nata")
    }

    companion object {
        const val ARG_RECORD = "ARG_RECORD"
    }
}