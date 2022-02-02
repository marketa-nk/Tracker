package com.mint.minttracker.historyFragment


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mint.minttracker.databinding.ViewRecordBinding
import com.mint.minttracker.models.Record
import java.text.SimpleDateFormat
import kotlin.math.roundToInt


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
            binding.date.text = SimpleDateFormat("d MMMM yyyy").format(record.date)
            binding.timeText.text = timeToString(record.totalTime)
            binding.distanceText.text = "${(record.distance).round()}м"
            binding.speedAveText.text = "${(record.aveSpeed * 3.6).round()}км/ч"
            binding.speedMaxText.text = "${(record.maxSpeed * 3.6).round()}км/ч"
            binding.root.setOnClickListener {
                recordListener?.onItemClick(record)
            }
            binding.root.setOnLongClickListener {
                recordListener?.onItemLongClick(record)
                true
            }
        }


        private fun timeToString(totalTime: Long): String {
            val sec = (totalTime / 1000).toInt() % 60
            val min = (totalTime / (1000 * 60) % 60)
            val hr = (totalTime / (1000 * 60 * 60) % 24)
            return "$hr:$min:$sec"
        }
    }

    interface OnRecordClickListener {
        fun onItemClick(record: Record)
        fun onItemLongClick(record: Record): Boolean
    }
}

fun Double.round(): Double {
    return (this * 100.0).roundToInt() / 100.0
}