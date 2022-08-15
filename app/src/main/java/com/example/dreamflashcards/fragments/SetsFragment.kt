package com.example.dreamflashcards.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcards.adapters.SetsAdapter
import com.example.dreamflashcards.databinding.FragmentCreateBinding
import com.example.dreamflashcards.databinding.FragmentSetsBinding
import com.example.dreamflashcards.models.Flashcard
import com.example.dreamflashcards.models.FlashcardsSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SetsFragment : Fragment() {

    // view binding
    private var _binding: FragmentSetsBinding? = null
    private val binding get() = _binding!!

    // RecyclerView
    private lateinit var recyclerView: RecyclerView

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // Firestore Database
    private lateinit var firestoreDatabase: FirebaseFirestore

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

        auth = FirebaseAuth.getInstance()
        firestoreDatabase = Firebase.firestore

        /** get set documents from Firestore */
        getSets()

        /** recyclerView setup */
        recyclerView = binding.setsRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = SetsAdapter(requireContext(), listOf(FlashcardsSet("siuu", "Top G", auth.currentUser!!.uid, 201)))

        recyclerView.setHasFixedSize(true)

    }

    /** Get sets from Firestore function */
    private fun getSets(){
        firestoreDatabase.collectionGroup("Sets").whereEqualTo("creator", auth.currentUser!!.uid).get()
            .addOnSuccessListener { querySnapshot ->

                Log.d(TAG, "Data retrieved: ${querySnapshot.documents}")

            }
            .addOnFailureListener { e ->

                Log.e(TAG, "Could not retrieve data from Firestore due to: ${e.message}")
                Toast.makeText(requireContext(), "Could not retrieve data from database", Toast.LENGTH_SHORT).show()

            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}