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
import com.example.dreamflashcards.databinding.FragmentCreateBinding
import com.example.dreamflashcards.viewmodels.AppViewModel

class CreateFragment : Fragment() {

    // view binding
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!

    // AppViewModel
    private val appViewModel: AppViewModel by activityViewModels()


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

        /** create set button click listener */
        binding.createSetButtom.setOnClickListener {

            val name = binding.setNameTextInputLayout.editText?.text.toString()

            if(name.isNullOrEmpty()){

                // if input string is empty, trigger an error
                Log.e(TAG, "set name input string empty")
                binding.setNameEditText.error = "Please enter the name"

            } else {
                try {

                    // add the set to Firebase
                    appViewModel.addSet(name)

                    Toast.makeText(requireContext(), "New set created", Toast.LENGTH_SHORT).show()

                    // crete a set and go to the next screen
                    Log.d(TAG, "Moving to AddFlashcardsFragment")
                    val action = CreateFragmentDirections.actionCreateFragmentToAddFlashcardsFragment("CreateFragment")
                    findNavController().navigate(action)

                } catch (e: Exception) {
                    // an error occurred
                    Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT)
                }

            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}