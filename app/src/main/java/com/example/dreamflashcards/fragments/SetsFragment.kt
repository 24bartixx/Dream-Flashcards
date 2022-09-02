package com.example.dreamflashcards.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcards.adapters.SetsAdapter
import com.example.dreamflashcards.databinding.FragmentSetsBinding
import com.example.dreamflashcards.viewmodels.AppViewModel

class SetsFragment : Fragment() {

    // view binding
    private var _binding: FragmentSetsBinding? = null
    private val binding get() = _binding!!

    // RecyclerView
    private lateinit var recyclerView: RecyclerView

    // AppViewModel
    private val appViewModel: AppViewModel by activityViewModels()

    companion object {
        private const val TAG = "SetsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** get set documents from Firestore */
        appViewModel.getSets()

        /** recyclerView setup */
        recyclerView = binding.setsRecyclerview

        val adapter = SetsAdapter{ flashcardSet ->

            var action: NavDirections

            if(flashcardSet.type == "user") {
                action = SetsFragmentDirections.actionSetsFragmentToSetOptionFragment()
            } else {
                action = SetsFragmentDirections.actionSetsFragmentToSetOptionDownloadedSetFragment()
            }

            if (flashcardSet != appViewModel.currentSet.value) {
                try {

                    // set currentSet and retrieve flashcards from Firebase
                    appViewModel.setCurrentSet(flashcardSet)

                    Log.i(TAG, "Current set id: ${appViewModel.getCurrentSet().setID}")

                    // got to the next screen
                    Log.d(TAG, "Moving to the next screen")

                    findNavController().navigate(action)

                } catch (e: Exception) {
                    Log.e(TAG, "Cannot set current set in viewModel due to: ${e.message}")
                    Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()
                }
            } else {

                Log.d(TAG, "Proper currentSet already set!")

                // got to the next screen
                Log.d(TAG, "Moving to the next screen")
                findNavController().navigate(action)

            }

        }

        recyclerView.adapter = adapter
        appViewModel.sets.observe(this.viewLifecycleOwner){ sets ->
            sets.let{

                if(!appViewModel.sets.value.isNullOrEmpty()) {
                    Log.d(TAG, "Sets list: ${appViewModel.sets.value}")
                    adapter.submitList(appViewModel.sets.value)
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