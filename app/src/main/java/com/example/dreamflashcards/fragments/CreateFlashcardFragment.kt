
package com.example.dreamflashcards.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dreamflashcards.R
import com.example.dreamflashcards.databinding.FragmentCreateFlashcardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateFlashcardFragment : Fragment() {

    // view binding
    private var _binding: FragmentCreateFlashcardBinding? = null
    private val binding get() = _binding!!

    // Firestore Database
    private lateinit var firestoreDatabase: FirebaseFirestore

    private val args: CreateFlashcardFragmentArgs by navArgs()

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
            addFlashcardToFirebase()
        }

    }

    /** Add flashcard to firebase */
    private fun addFlashcardToFirebase(){

        // HashMap pof the flashcard
        val flashcard = hashMapOf(
            "term" to term,
            "definition" to definition,
            "order" to 1
        )

        firestoreDatabase.collection("Sets").document(args.setID).collection("Flashcards")
            .add(flashcard)
            .addOnSuccessListener { documentReference ->

                // flashcard created
                Log.i(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(requireContext(), "New flashcard created", Toast.LENGTH_SHORT).show()

                binding.termTextInput.editText?.setText("")
                binding.definitionTextInput.editText?.setText("")

            }
            .addOnFailureListener { e ->

                // something went wrong creating a new flashcard
                Log.e(TAG, "Creation of the flashcard in the Firestore went wrong due to: ${e.message}")
                Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()

            }

    }

}