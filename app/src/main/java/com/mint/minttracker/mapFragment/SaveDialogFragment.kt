package com.mint.minttracker.mapFragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.mint.minttracker.R

class SaveDialogFragment : DialogFragment() {
    private lateinit var listener: SaveDialogListener

    interface SaveDialogListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.choose_an_action))
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.save) { _, _ ->
                    listener.onDialogPositiveClick()
                }
                .setNeutralButton(R.string.delete) { _, _ ->
                    listener.onDialogNegativeClick()
                }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            if (parentFragment != null) {
                parentFragment as SaveDialogListener
            } else {
                context as SaveDialogListener
            }
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement SaveDialogListener"))
        }
    }
}