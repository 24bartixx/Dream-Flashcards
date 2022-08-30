package com.example.dreamflashcards.fragments

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

        /** show definition button listener */
        binding.showDefinitionButton.setOnClickListener{
            showDefinition()
        }

        /** next flashcard button listener */
        binding.nextFlashcardButton.setOnClickListener {
            appViewModel.incrementReviseIndex()
            showTermAndButton()
        }

        /** start revise cycle */
        appViewModel.resetReviseIndex()
        showTermAndButton()

    }

    private fun showTermAndButton() {

        if(appViewModel.reviseIndex.value == appViewModel.flashcards.value!!.size){
            appViewModel.resetReviseIndex()
        }

        Log.d(TAG, "Term at index: ${appViewModel.reviseIndex.value!!}")

        binding.apply {
            termCard.visibility = View.VISIBLE
            definitionCard.visibility = View.INVISIBLE
            showDefinitionButton.visibility = View.VISIBLE
            nextFlashcardButton.visibility = View.INVISIBLE

            term.text = appViewModel.flashcards.value!![appViewModel.reviseIndex.value!!].term
            definition.text = appViewModel.flashcards.value!![appViewModel.reviseIndex.value!!].definition
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