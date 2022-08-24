package com.example.dreamflashcards.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dreamflashcards.databinding.FragmentReviseBinding
import com.example.dreamflashcards.viewmodels.AppViewModel

class ReviseFragment : Fragment() {

    private var _binding: FragmentReviseBinding? = null
    private val binding get() = _binding!!

    private val appViewModel: AppViewModel by activityViewModels()

    // Progress Dialog
    private lateinit var progressDialog: ProgressDialog

    companion object {
        private const val TAG = "ReviseFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReviseBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // configure progressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Preparing flashcards")
        progressDialog.setCanceledOnTouchOutside(false)

        // buttons listeners
        binding.showDefinitionButton.setOnClickListener{
            showDefinition()
        }

        binding.nextFlashcardButton.setOnClickListener {
            appViewModel.incrementReviseIndex()
            showTermAndButton()
        }

        appViewModel.reviseFlashcards.observe(this.viewLifecycleOwner) { reviseFlashcards ->

            if(appViewModel.reviseFlashcards.value.isNullOrEmpty()){
                Log.d(TAG, "Revise flashcards is empty")
                hideEverything()
                progressDialog.show()
            } else {
                Log.d(TAG, "Revise flashcards list changed")
                showTermAndButton()
            }

        }

    }

    private fun hideEverything(){
        binding.apply {
            termCard.visibility = View.INVISIBLE
            definitionCard.visibility = View.INVISIBLE
            showDefinitionButton.visibility = View.INVISIBLE
            nextFlashcardButton.visibility = View.INVISIBLE
        }
    }

    private fun showTermAndButton() {
        binding.apply {

            if(appViewModel.reviseIndex.value == appViewModel.reviseFlashcards.value!!.size) {
                progressDialog.show()
                appViewModel.getMoreReviseFlashcards()
            } else {

                Log.d(TAG, "Term at index: ${appViewModel.reviseIndex.value!!}")

                progressDialog.dismiss()

                termCard.visibility = View.VISIBLE
                definitionCard.visibility = View.INVISIBLE
                showDefinitionButton.visibility = View.VISIBLE
                nextFlashcardButton.visibility = View.INVISIBLE

                term.text = appViewModel.reviseFlashcards.value!![appViewModel.reviseIndex.value!!].term
                definition.text = appViewModel.reviseFlashcards.value!![appViewModel.reviseIndex.value!!].definition

            }

        }
    }

    private fun showDefinition(){

        binding.apply {

            definitionCard.visibility = View.VISIBLE
            showDefinitionButton.visibility = View.INVISIBLE
            nextFlashcardButton.visibility = View.VISIBLE

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}