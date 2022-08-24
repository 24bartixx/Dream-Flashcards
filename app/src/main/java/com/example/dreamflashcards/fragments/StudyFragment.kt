package com.example.dreamflashcards.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    // state of the fragment variable
    private lateinit var fragmentState: String

    // available states of the fragment
    enum class StudyState(val state: String) {
        WITHOUT_DEF("without"),
        WITH_DEF ("with")
    }

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
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Preparing flashcards")
        progressDialog.setCanceledOnTouchOutside(false)

        // set onClick listeners
        binding.apply {
            // onClickListeners
            showDefinitionButton.setOnClickListener{
                changeVisibility("Show definition")
            }
            yesButton.setOnClickListener{
                appViewModel.incrementStudiedWordsCount()
                changeVisibility("Yes")
                if(appViewModel.studiedWordsCount.value!! < appViewModel.studyFlashcards.value!!.size) bindToTheNextFlashcard()
            }
            noButton.setOnClickListener {
                appViewModel.incrementStudiedWordsCount()
                changeVisibility("No")
                if(appViewModel.studiedWordsCount.value!! < appViewModel.studyFlashcards.value!!.size) bindToTheNextFlashcard()
            }
            mediumButton.setOnClickListener {
                appViewModel.incrementStudiedWordsCount()
                changeVisibility("Medium")
                if(appViewModel.studiedWordsCount.value!! < appViewModel.studyFlashcards.value!!.size) bindToTheNextFlashcard()
            }
        }

        // set observer of list  of flashcards in ViewModel
        appViewModel.studyFlashcards.observe(this.viewLifecycleOwner) { studyFlashcards ->

            // if list of flashcards is not populated
            if (appViewModel.studyFlashcards.value.isNullOrEmpty()) {

                if (appViewModel.studySetStudied.value!!) {

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

                } else {
                    Log.d(TAG, "Progress dialog appears")
                    showProgressDialog()
                }

            } else {

                Log.d(TAG, "List of flashcards to study changed")

                // set visibility
                if (appViewModel.studiedWordsCount.value!! < appViewModel.studyFlashcards.value!!.size) {

                    Log.d(TAG, "studiedWordsCount: ${appViewModel.studiedWordsCount.value!!}")
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Studying program updated...", Toast.LENGTH_SHORT).show()
                    bindToTheNextFlashcard()

                } else {

                    showProgressDialog()

                }

            }

        }
    }

    /** visibility on click configuration function */
    private fun changeVisibility(option: String){

        if (option == "Show definition"){

            binding.apply {
                definitionCard.visibility = View.VISIBLE
                showDefinitionButton.visibility = View.INVISIBLE
                yesButton.visibility = View.VISIBLE
                mediumButton.visibility = View.VISIBLE
                noButton.visibility = View.VISIBLE
            }

        } else {

            if(option == "Yes") {

                // update the flashcard in Firestore
                appViewModel.setFlashcardLearnedToFirestore(appViewModel.studiedWordsCount.value!! - 1)

            }

            // if there is no more flashcards to learn left, get some, shuffle, set studiedWordsCount to 0
            if(appViewModel.studiedWordsCount.value!! == appViewModel.studyFlashcards.value!!.size) {

                if(appViewModel.studyFlashcards.value!!.size == AppViewModel.MAX_STUDY) {
                    appViewModel.removeLearned()
                    appViewModel.getMoreStudyFlashcards()
                }
                // if there is no more flashcard to get from Firebase
                else {
                    appViewModel.removeLearned()
                    appViewModel.resetStudiedWordsCount()
                    appViewModel.shuffleFlashcards()
                    if(appViewModel.studyFlashcards.value!!.size == 0){
                        appViewModel.setStatusLearned()
                    }
                    if(appViewModel.studyFlashcards.value!!.size == 1){
                        bindToTheNextFlashcard()
                    }
                }

            }

        }

    }

    private fun bindToTheNextFlashcard(){

        Log.d(TAG, "Size of list to study: ${appViewModel.studyFlashcards.value!!.size}")
        Log.d(TAG, "Getting flashcard with index: ${appViewModel.studiedWordsCount.value!!}")

        binding.apply {

            binding.termCard.visibility = View.VISIBLE
            definitionCard.visibility = View.INVISIBLE
            yesButton.visibility = View.INVISIBLE
            mediumButton.visibility = View.INVISIBLE
            noButton.visibility = View.INVISIBLE
            showDefinitionButton.visibility = View.VISIBLE
            binding.congratulationsText.visibility = View.INVISIBLE
            binding.happyIcon.visibility = View.INVISIBLE

            term.text = appViewModel.studyFlashcards.value!![appViewModel.studiedWordsCount.value!!].term
            definition.text = appViewModel.studyFlashcards.value!![appViewModel.studiedWordsCount.value!!].definition

        }
    }

    fun showProgressDialog(){

        Log.d(TAG, "showing progress dialog")

        progressDialog.show()

        binding.apply {
            binding.termCard.visibility = View.INVISIBLE
            binding.definitionCard.visibility = View.INVISIBLE
            binding.showDefinitionButton.visibility = View.INVISIBLE
            binding.yesButton.visibility = View.INVISIBLE
            binding.mediumButton.visibility = View.INVISIBLE
            binding.noButton.visibility = View.INVISIBLE
            binding.congratulationsText.visibility = View.INVISIBLE
            binding.happyIcon.visibility = View.INVISIBLE
        }

    }

    override fun onDestroy() {

        super.onDestroy()
        _binding = null

    }

}