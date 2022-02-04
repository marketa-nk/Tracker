package com.mint.minttracker.historyFragment

import androidx.recyclerview.widget.DiffUtil
import com.mint.minttracker.models.Record

class RecordsDiffUtil : DiffUtil.ItemCallback<Record>() {
    override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem.idTrack == newItem.idTrack
    }

    override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem == newItem
    }

}
