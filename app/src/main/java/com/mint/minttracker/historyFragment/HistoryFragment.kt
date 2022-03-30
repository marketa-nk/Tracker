package com.mint.minttracker.historyFragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mint.minttracker.App
import com.mint.minttracker.R
import com.mint.minttracker.databinding.FragmentHistoryBinding
import com.mint.minttracker.models.Record
import com.mint.minttracker.recordFragment.RecordFragment
import javax.inject.Inject

class HistoryFragment : Fragment() {

    @Inject
    lateinit var factory: HistoryViewModel.HistoryViewModelFactory

    private val viewModel: HistoryViewModel by viewModels { factory }

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private var actionMode: ActionMode? = null

    private val recordsAdapter = RecordsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.appComponent.injectHistoryFragment(this)

        viewModel.records.observe(this) { records -> showHistory(records) }
        viewModel.listIsEmpty.observe(this) { listIsEmpty -> changeVisabilityOfEmptyRecordListLayout(listIsEmpty) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = recordsAdapter

        val dividerItemDecoration = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider_drawable)
        if (divider != null){
            dividerItemDecoration.setDrawable(divider)
            binding.recyclerView.addItemDecoration(dividerItemDecoration)
        }

        recordsAdapter.recordListener = object : RecordsAdapter.OnRecordClickListener {
            override fun onItemClick(record: Record) {
                viewModel.recordClicked(record)
            }

            override fun onItemLongClick(record: Record): Boolean {
                if (actionMode != null) {
                    return false
                }
                actionMode = (activity as? AppCompatActivity)?.startSupportActionMode(object : ActionMode.Callback {
                    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                        mode.menuInflater.inflate(R.menu.menu_history, menu)
                        return true
                    }

                    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                        return false
                    }

                    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                        return when (item.itemId) {
                            R.id.delete_record -> {
                                viewModel.deleteRecordButtonClicked(record)
                                mode.finish()
                                true
                            }
                            else -> {
                                mode.finish()
                                false
                            }
                        }
                    }

                    override fun onDestroyActionMode(mode: ActionMode?) {
                        actionMode = null
                    }
                })
                return true
            }
        }
        viewModel.messageEvent.observe(this.viewLifecycleOwner) { message -> showToast(message) }
        viewModel.displayRecordScreenEvent.observe(this.viewLifecycleOwner) { rec -> showRecordFragment(rec) }
        binding.myToolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        return binding.root
    }

    private fun showHistory(records: List<Record>) {
        recordsAdapter.submitList(records)
    }

    private fun changeVisabilityOfEmptyRecordListLayout(listIsEmpty: Boolean) {
        binding.emptyRecordListLayout.isVisible = listIsEmpty
    }

    private fun showRecordFragment(record: Record) {
        binding.root.findNavController().navigate(
            R.id.action_historyFragment_to_recordFragment,
            bundleOf(RecordFragment.ARG_RECORD to record)
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}