package com.example.dreamflashcards.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dreamflashcards.databinding.FragmentStudyBinding
import com.example.dreamflashcards.viewmodels.AppViewModel

class StudyFragment : Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!

    // AppViewModel
    private val appViewModel: AppViewModel by activityViewModels()

    // Progress Dialog
    private lateinit var progressDialog: ProgressDialog

    companion object {
        private const val TAG = "StudyFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // configure progressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Shuffling flashcards")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //if(appViewModel.studySetStudied.value!!){ showCongratulationsText() }

        // set onClick listeners
        binding.apply {
            // onClickListeners
            showDefinitionButton.setOnClickListener{ showDefinition() }
            yesButton.setOnClickListener{ optionButtonClicked("Yes") }
            noButton.setOnClickListener { optionButtonClicked("No") }
            mediumButton.setOnClickListener { optionButtonClicked("Medium") }
        }

        // set observer of list  of flashcards in ViewModel
        appViewModel.studyShuffled.observe(this.viewLifecycleOwner) { studyFlashcards ->

            if(appViewModel.studyShuffled.value!!){
                showTermAndButton()
                progressDialog.dismiss()
            } else {
                hideEverything()
                progressDialog.show()
            }

        }
    }

    /** hide everything on the screen */
    private fun hideEverything(){

        binding.apply {
            termCard.visibility = View.INVISIBLE
            definitionCard.visibility = View.INVISIBLE
            showDefinitionButton.visibility = View.INVISIBLE
            yesButton.visibility = View.INVISIBLE
            mediumButton.visibility = View.INVISIBLE
            noButton.visibility = View.INVISIBLE
            congratulationsText.visibility = View.INVISIBLE
            happyIcon.visibility = View.INVISIBLE
        }

    }

    /** show congratulationsText */
    private fun showCongratulationsText() {
        Log.d(TAG, "Showing congratulations text")

        progressDialog.dismiss()
        binding.apply {
            binding.termCard.visibility = View.INVISIBLE
            binding.definitionCard.visibility = View.INVISIBLE
            binding.showDefinitionButton.visibility = View.INVISIBLE
            binding.yesButton.visibility = View.INVISIBLE
            binding.mediumButton.visibility = View.INVISIBLE
            binding.noButton.visibility = View.INVISIBLE
            binding.congratulationsText.visibility = View.VISIBLE
            binding.happyIcon.visibility = View.VISIBLE
        }
    }

    /** handle click on option button */
    private fun optionButtonClicked(option: String){

        appViewModel.incrementStudyIndex()

        if(option == "Yes"){
            appViewModel.updateFlashcardLearned(appViewModel.studyFlashcards.value!![appViewModel.studyIndex.value!! - 1])
        }

        if(appViewModel.studyIndex.value!! == appViewModel.studyFlashcards.value!!.size){
            Log.d(TAG, "Resetting studyIndex and shuffling study flashcards")
            appViewModel.resetStudyIndex()
            appViewModel.shuffleStudyFlashcards()
        }

        showTermAndButton()

    }

    /** show term and button function */
    private fun showTermAndButton(){

        if(appViewModel.studyFlashcards.value!!.size == 0){
            showCongratulationsText()
        } else {
            Log.d(TAG, "Size of list to study: ${appViewModel.studyFlashcards.value!!.size}")
            Log.d(TAG, "Getting flashcard with index: ${appViewModel.studyIndex.value!!}")

            binding.apply {
                noButton.visibility = View.INVISIBLE
                mediumButton.visibility = View.INVISIBLE
                yesButton.visibility = View.INVISIBLE
                definitionCard.visibility = View.INVISIBLE
                congratulationsText.visibility = View.INVISIBLE
                happyIcon.visibility = View.INVISIBLE
                termCard.visibility = View.VISIBLE
                showDefinitionButton.visibility = View.VISIBLE

                term.text = appViewModel.studyFlashcards.value!![appViewModel.studyIndex.value!!].term
                definition.text = appViewModel.studyFlashcards.value!![appViewModel.studyIndex.value!!].definition
            }
        }
    }

    /** show definition function */
    private fun showDefinition(){
        binding.apply {
            definitionCard.visibility = View.VISIBLE
            showDefinitionButton.visibility = View.INVISIBLE
            yesButton.visibility = View.VISIBLE
            noButton.visibility = View.VISIBLE
            mediumButton.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {

        super.onDestroy()
        _binding = null

    }

}