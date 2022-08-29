package com.example.dreamflashcards.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcards.R
import com.example.dreamflashcards.adapters.DownloadsAdapter
import com.example.dreamflashcards.databinding.FragmentDownloadBinding
import com.example.dreamflashcards.viewmodels.AppViewModel

class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    // RecyclerView
    private lateinit var recyclerView: RecyclerView

    // AppViewModel
    private val appViewModel: AppViewModel by activityViewModels()

    companion object{
        private const val TAG = "DownloadFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "English imagee: ${R.drawable.english_icon}")

        /** get set documents from Firestore */
        appViewModel.getDownloadSets()

        recyclerView = binding.downloadSetsRecyclerview

        val adapter = DownloadsAdapter { flashcardSet ->

            try {

                appViewModel.setCurrentDownloadSet(flashcardSet)

                // got to the next screen
                Log.d(TAG, "Moving to the next screen")
                appViewModel.resetDownloadComplete()
                val action = DownloadFragmentDirections.actionDownloadFragmentToSetOptionDownloadFragment()
                findNavController().navigate(action)

            } catch(e: Exception) {
                Log.e(TAG, "Cannot set currentDownloadSet in viewModel due to: ${e.message}")
                Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()
            }

        }

        recyclerView.adapter = adapter

        appViewModel.downloadSets.observe(this.viewLifecycleOwner) { downloadSets ->
            Log.d(TAG, "Download sets list changed")
            downloadSets.let {

                if(!appViewModel.downloadSets.value.isNullOrEmpty()){
                    Log.d(TAG, "Download sets list: ${appViewModel.downloadSets.value}")
                    adapter.submitList(appViewModel.downloadSets.value)
                }

            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}