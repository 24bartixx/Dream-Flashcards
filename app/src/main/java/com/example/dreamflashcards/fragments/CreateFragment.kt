package com.example.dreamflashcards.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.dreamflashcards.R
import com.example.dreamflashcards.databinding.FragmentCreateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateFragment : Fragment() {

    // view binding
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    // Firestore Database
    private lateinit var firestoreDatabase: FirebaseFirestore

    // Firebase Authorization
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "CreateFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestoreDatabase = Firebase.firestore
        auth = FirebaseAuth.getInstance()

        // create set button click listener
        binding.createSetButtom.setOnClickListener {

            val name = binding.setNameTextInputLayout.editText?.text.toString()

            if(name.isNullOrEmpty()){

                // if input string is empty, trigger an error
                Log.e(TAG, "set name input string empty")
                binding.setNameEditText.error = "Please enter the name"

            } else {

                addSetToFirebase(name)

            }

        }
    }

    /** Add set to Firebase */
    private fun addSetToFirebase(name: String){

        // HashMap of the set
        val set = hashMapOf(
            "name" to name,
            "order" to 1,
            "creator" to "${auth.currentUser!!.uid}",
            "words_count" to 0
        )

        firestoreDatabase.collection("Sets")
            .add(set)
            .addOnSuccessListener { documentReference ->

                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

                // crete a set and go to the next screen
                Log.d(TAG, "Moving to AddFlashcardsFragment")
                Toast.makeText(requireContext(), "New set created", Toast.LENGTH_SHORT).show()

                val action = CreateFragmentDirections.actionCreateFragmentToAddFlashcardsFragment(documentReference.id)
                findNavController().navigate(action)

            }
            .addOnFailureListener { e ->

                Log.e(TAG, "Creation of the set in the Firestore went wrong due to: ${e.message}")
                Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT)

            }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}