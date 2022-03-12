package com.mint.minttracker.historyFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mint.minttracker.databinding.ViewRecordBinding
import com.mint.minttracker.models.Record
import com.mint.minttracker.msToUiString
import com.mint.minttracker.toUiString
import java.text.DateFormat

class RecordsAdapter : ListAdapter<Record, RecordsAdapter.RecordViewHolder>(RecordsDiffUtil()) {

    var recordListener: OnRecordClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ViewRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecordViewHolder(private val binding: ViewRecordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(record: Record) {
            binding.date.text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(record.date)
            binding.timeText.text = record.totalTimeMs.msToUiString()
            binding.distanceText.text = "${(record.distance).toUiString()}м"
            binding.speedAveText.text = "${(record.aveSpeedInKm).toUiString()}км/ч"
            binding.speedMaxText.text = "${(record.maxSpeedInKm).toUiString()}км/ч"
            binding.root.setOnClickListener {
                recordListener?.onItemClick(record)
            }
            binding.root.setOnLongClickListener {
                recordListener?.onItemLongClick(record)
                true
            }
        }
    }

    interface OnRecordClickListener {
        fun onItemClick(record: Record)
        fun onItemLongClick(record: Record): Boolean
    }
}