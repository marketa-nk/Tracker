package com.mint.minttracker.recordFragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.mint.minttracker.databinding.FragmentRecordBinding
import com.mint.minttracker.historyFragment.round
import com.mint.minttracker.models.Record
import java.text.SimpleDateFormat


class RecordFragment : MvpAppCompatFragment(), RecordView, OnMapReadyCallback {

    @InjectPresenter
    lateinit var recordPresenter: RecordPresenter

    private var map: GoogleMap? = null
    private var polyline: Polyline? = null

    init {
        println("recordFragment created - Nata")
    }

    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)

        val record: Record? = requireArguments().getParcelable(ARG_RECORD)
        if (record != null) {
            recordPresenter.readyToShowRecord(record)
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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
        }
    }

    override fun showPolyline(list: List<LatLng>) {
        addPolyline()
        polyline?.points = list
        val builder = LatLngBounds.Builder()
        list.forEach {
            builder.include(it)
        }
        val bounds = builder.build()

        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
    }

    private fun addPolyline() {
        polyline = map?.addPolyline(
            PolylineOptions()
                .color(Color.RED)
                .width(10.0F)
        )
    }

    override fun showRecordInfo(record: Record) {
        binding.date.text = SimpleDateFormat("d MMMM yyyy").format(record.date)
        binding.timeText.text = timeToString(record.totalTimeMs)
        binding.distanceText.text = "${(record.distance).round()}м"
        binding.speedAveText.text = "${(record.aveSpeed * 3.6).round()}км/ч"
        binding.speedMaxText.text = "${(record.maxSpeed * 3.6).round()}км/ч"
    }

    private fun timeToString(totalTime: Long): String {
        val sec = (totalTime / 1000).toInt() % 60
        val min = (totalTime / (1000 * 60) % 60)
        val hr = (totalTime / (1000 * 60 * 60) % 24)
        return "$hr:$min:$sec"
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

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
        println("onDestroy RecordFragment Nata")
    }

    companion object {
        const val ARG_RECORD = "ARG_RECORD"
    }


}