package com.mint.minttracker.recordFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.mint.minttracker.App
import com.mint.minttracker.extensions.fragmentViewModels
import com.mint.minttracker.extensions.parcelable
import com.mint.minttracker.recordFragment.composeViews.RecordScreen
import com.mint.minttracker.theme.MainTheme
import javax.inject.Inject

class RecordFragment : Fragment() {

    @Inject
    lateinit var factory: RecordViewModel.Factory

    private val viewModel: RecordViewModel by fragmentViewModels {
        factory.create(requireArguments().parcelable(ARG_RECORD)!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectRecordFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(context = requireContext()).apply {
        setContent {
            MainTheme {
                RecordScreen(viewModel = viewModel)
            }
        }
    }

    companion object {
        const val ARG_RECORD = "ARG_RECORD"
    }
}
