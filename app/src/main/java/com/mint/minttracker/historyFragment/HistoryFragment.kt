package com.mint.minttracker.historyFragment

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.mint.minttracker.R
import com.mint.minttracker.databinding.FragmentHistoryBinding
import com.mint.minttracker.models.Record
import com.mint.minttracker.recordFragment.RecordFragment

class HistoryFragment : MvpAppCompatFragment(), HistoryView {
    @InjectPresenter
    lateinit var historyPresenter: HistoryPresenter

    init {
        println("historyFragment created - Nata")
    }

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private var actionMode: ActionMode? = null

    private val recordsAdapter = RecordsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = recordsAdapter
        recordsAdapter.recordListener = object : RecordsAdapter.OnRecordClickListener {
            override fun onItemClick(record: Record) {
                historyPresenter.recordClicked(record)
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
                                historyPresenter.deleteRecordButtonClicked(record)
                                mode.finish()
                                true
                            }
                            else -> false
                        }
                    }

                    override fun onDestroyActionMode(mode: ActionMode?) {
                        actionMode = null
                    }

                })
                return true
            }
        }

        binding.myToolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        return binding.root
    }

    override fun showHistory(records: List<Record>) {
        recordsAdapter.submitList(records)
    }

    override fun showRecordFragment(record: Record) {
        binding.root.findNavController().navigate(R.id.action_historyFragment_to_recordFragment, bundleOf(
            RecordFragment.ARG_RECORD to record))
    }
}