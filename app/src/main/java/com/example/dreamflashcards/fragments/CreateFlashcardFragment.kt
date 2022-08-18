
package com.example.dreamflashcards.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dreamflashcards.databinding.FragmentCreateFlashcardBinding
import com.example.dreamflashcards.viewmodels.AppViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateFlashcardFragment : Fragment() {

    // view binding
    private var _binding: FragmentCreateFlashcardBinding? = null
    private val binding get() = _binding!!

    // Firestore Database
    private lateinit var firestoreDatabase: FirebaseFirestore

    // AppViewModel
    private val appViewModel: AppViewModel by activityViewModels()

    private var term = ""
    private var definition = ""

    companion object {
        private const val TAG ="CreateFlashcardFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCreateFlashcardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestoreDatabase = Firebase.firestore

        /** set create flashcard button listener */
        binding.addFlashcardButton.setOnClickListener {
            addNewFlashcard()
        }

    }

    private fun addNewFlashcard(){

        term = binding.termTextInput.editText?.text.toString()
        definition = binding.definitionTextInput.editText?.text.toString()

        if(term.isNullOrEmpty()){

            Log.e(TAG, "Empty term text input")
            binding.termTextInput.editText!!.error = "Please enter the term"

        } else if(definition.isNullOrEmpty()) {

            Log.e(TAG, "Empty definition text input")
            binding.definitionTextInput.editText!!.error = "Please enter the definition"

        } else {
            try {

                // add Flashcard to Firestore
                appViewModel.addFlashcard(term, definition)

                // set edit texts to empty string
                binding.termTextInput.editText?.setText("")
                binding.definitionTextInput.editText?.setText("")

                Toast.makeText(requireContext(), "New flashcard created", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Log.d(TAG, "Error adding an flashcard dur to: ${e.message}")
                Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
        }

    }

}