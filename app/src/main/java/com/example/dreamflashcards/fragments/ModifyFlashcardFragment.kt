package com.example.dreamflashcards.fragments

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.dreamflashcards.databinding.FragmentModifyFlashcardBinding
import com.example.dreamflashcards.models.Flashcard
import com.example.dreamflashcards.viewmodels.AppViewModel

class ModifyFlashcardFragment : Fragment() {

    // view binding
    private var _binding: FragmentModifyFlashcardBinding? = null
    private val binding get() = _binding!!

    private val args: ModifyFlashcardFragmentArgs by navArgs()

    // AppViewModel
    private val appViewModel: AppViewModel by activityViewModels()

    companion object {
        private const val TAG = "ModifyFlashcardFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentModifyFlashcardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = args.position

        val flashcard = appViewModel.flashcards.value!![position]

        Log.d(TAG, "Flashcard term: ${flashcard.term}")

        binding.termTextInput.editText?.text = SpannableStringBuilder(flashcard.term)
        binding.definitionTextInput.editText?.text = SpannableStringBuilder(flashcard.definition)

        if(appViewModel.currentSet.value!!.type == "defined"){
            binding.termTextInput.editText?.isFocusable = false
            binding.definitionTextInput.editText?.isFocusable = false
        }

        binding.learnedSwitch.isChecked = flashcard.learned != "no"

        binding.saveButton.setOnClickListener {
            saveFlashcard(flashcard)
        }

        binding.deleteButton.setOnClickListener {
            deleteFlashcard(flashcard)
            appViewModel.flashcards.observe(this.viewLifecycleOwner){
                findNavController().popBackStack()
            }
        }

    }

    private fun saveFlashcard(flashcard: Flashcard){

        var newFlashcard: Flashcard = flashcard
        var isChanged = false
        var learnedChanged = false

        if(flashcard.term != binding.termTextInput.editText?.text.toString()){
            newFlashcard.term = binding.termTextInput.editText?.text.toString()
            isChanged = true
        }

        if(flashcard.definition != binding.definitionTextInput.editText?.text.toString()){
            newFlashcard.definition = binding.definitionTextInput.editText?.text.toString()
            isChanged = true
        }

        if(flashcard.learned == "yes" && !binding.learnedSwitch.isChecked){
            newFlashcard.learned = "no"
            isChanged = true
            learnedChanged = true
        }

        if(flashcard.learned == "no" && binding.learnedSwitch.isChecked){
            newFlashcard.learned = "yes"
            isChanged = true
            learnedChanged = true
        }

        if(isChanged){
            appViewModel.modifyFlashcard(newFlashcard, learnedChanged)
        }

        findNavController().popBackStack()

    }

    private fun deleteFlashcard(flashcard: Flashcard) {
        appViewModel.deleteFlashcard(flashcard)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}