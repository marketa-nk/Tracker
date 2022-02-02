package com.mint.minttracker.historyFragment

import androidx.recyclerview.widget.DiffUtil
import com.mint.minttracker.models.Record

class RecordsDiffUtil : DiffUtil.ItemCallback<Record>() {
    override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem.idTrack == newItem.idTrack //todo почему date а не id, лучше id всегда использовать, то чтоне может поменяться - done
    }

    override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem == newItem
    }

}
