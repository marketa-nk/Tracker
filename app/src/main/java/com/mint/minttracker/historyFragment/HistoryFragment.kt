package com.mint.minttracker.historyFragment

import android.os.Bundle
import android.view.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.mint.minttracker.App
import com.mint.minttracker.historyFragment.composeViews.HistoryScreen
import com.mint.minttracker.theme.MainTheme
import javax.inject.Inject

class HistoryFragment : Fragment() {

    @Inject
    lateinit var factory: HistoryViewModel.HistoryViewModelFactory

    private val viewModel: HistoryViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectHistoryFragment(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = ComposeView(context = requireContext()).apply {
        setContent {
            MainTheme {
                HistoryScreen(viewModel = viewModel, navController = findNavController())
            }
        }
    }
}
