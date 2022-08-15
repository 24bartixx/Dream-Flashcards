package com.example.dreamflashcards.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcards.R
import com.example.dreamflashcards.adapters.FlashcardsCreateAdapter
import com.example.dreamflashcards.databinding.FragmentAddFlashcardsBinding
import com.example.dreamflashcards.models.Flashcard

class AddFlashcardsFragment : Fragment() {

    // view binding
    private var _binding: FragmentAddFlashcardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    private val args: AddFlashcardsFragmentArgs by navArgs()

    companion object {
        private const val TAG = "AddFlashcardsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddFlashcardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** RecyclerView setup */
        recyclerView = binding.flashcardsRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = FlashcardsCreateAdapter{ flashcard, option ->

            if (option == "delete") {

                Log.d(TAG, "Delete button clicked")

            } else if (option == "modify") {

                Log.d(TAG, "Modify button clicked")

            }

        }

        recyclerView.adapter = adapter

        adapter.submitList(listOf(
            Flashcard("XDXD", "Some term", "Some definition"),
            Flashcard("XDXDXD", "Some term 2 Siuuu lecimy po swoje", "Lecimy po swoje, We are fucking top Gs, we never give up"),
            Flashcard("Siuu", "Lecimy", "Rob Gryn"),
            Flashcard("Sss", "League of Legends", "Literally the most annoying game that was ever created"),
            Flashcard("One more", "One more", "I need one more")))



        /** Add a new flashcard navigation */
        binding.addFloatingActionButton.setOnClickListener {
            val action = AddFlashcardsFragmentDirections.actionAddFlashcardsFragmentToCreateFlashcardFragment(args.setID)
            findNavController().navigate(action)
        }

    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}