package com.example.dreamflashcards.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dreamflashcards.databinding.FragmentSetOptionBinding
import com.example.dreamflashcards.viewmodels.AppViewModel

class SetOptionFragment : Fragment() {

    // view binding
    private var _binding: FragmentSetOptionBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDialog: ProgressDialog

    // AppViewModel
    private val appViewModel: AppViewModel by activityViewModels()

    // AlertDialog
    private lateinit var alertDialogBuilder: AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog

    companion object {
        private const val TAG = "SetOptionFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSetOptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setOptionFragment = this

        binding.setName.text = appViewModel.currentSet.value!!.name

        // configure AlertDialog
        alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Delete set")
        alertDialogBuilder.setMessage("Are you sure you want to delete this set?")
        alertDialogBuilder.setPositiveButton("yes") { dialog, which ->
            Toast.makeText(requireContext(), "Set deleted...", Toast.LENGTH_SHORT).show()
            appViewModel.deleteFlashcardsDocument()
            appViewModel.setDeleted.observe(this.viewLifecycleOwner) {
                if(appViewModel.setDeleted.value!!) {
                    appViewModel.resetSetDeleted()
                    findNavController().popBackStack()
                }
            }
        }
        alertDialogBuilder.setNegativeButton("no") { dialog, which -> }
        alertDialog = alertDialogBuilder.create()

        // configure ProgressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Preparing flashcards")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        // flashcards observer
        if(appViewModel.currentSet.value!!.wordsCount.toInt() > 0){
            appViewModel.flashcards.observe(this.viewLifecycleOwner) {

                if(appViewModel.flashcards.value.isNullOrEmpty()){
                    hideEverything()
                    progressDialog.show()
                } else {
                    progressDialog.dismiss()
                    showEverything()
                }

            }
        }

    }

    /** data binding function on study button clicked */
    fun studyFlashcards(){

        if(appViewModel.currentSet.value != appViewModel.currentStudySet.value){
            try {

                Log.d(TAG, "Retrieving flashcards to study from Firestore")
                appViewModel.getFlashcardsToStudy()

                if(appViewModel.currentSet.value!!.wordsCount == appViewModel.currentSet.value!!.learned){
                    appViewModel.setStatusLearned()
                }

                val action = SetOptionFragmentDirections.actionSetOptionFragmentToStudyFragment()
                findNavController().navigate(action)

            } catch (e:Exception) {
                Log.e(TAG, "Cannot retrieve flashcards to study from Firestore due to: ${e.message}")
                Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "Flashcards to study already set")
            val action = SetOptionFragmentDirections.actionSetOptionFragmentToStudyFragment()
            findNavController().navigate(action)
        }

    }

    /** data binding function on revise button clicked */
    fun reviseFlashcards(){

        if(appViewModel.currentSet.value!!.wordsCount.toInt() > 0) {

        } else {

        }

        if(appViewModel.currentSet.value != appViewModel.currentReviseSet.value){
            if(appViewModel.currentReviseSet.value!!.wordsCount != "0") {
                try {

                    Log.d(TAG, "Retrieving flashcards to revise from Firestore")
                    appViewModel.getFlashcardsToRevise()

                    val action = SetOptionFragmentDirections.actionSetOptionFragmentToReviseFragment()
                    findNavController().navigate(action)

                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Cannot retrieve flashcards to revise from Firestore due to: ${e.message}"
                    )
                    Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "No flashcards to retrieve")
                Toast.makeText(requireContext(), "No flashcards to revise...", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d(TAG, "Flashcards to revise already set")
            val action = SetOptionFragmentDirections.actionSetOptionFragmentToReviseFragment()
            findNavController().navigate(action)
        }

    }

    /** data binding function on modify button clicked */
    fun modifyFlashcards(){

        val action = SetOptionFragmentDirections.actionSetOptionFragmentToAddFlashcardsFragment("SetOptionFragment")
        findNavController().navigate(action)

    }

    /** data binding function on delete button clicked */
    fun deleteFlashcards(){
        alertDialog.show()
    }

    fun goToTheNextScreen(){

        Log.d(TAG, "Going to the next screen, chosen option: ${appViewModel.option.value}")


        when(appViewModel.option.value){

            "Study" -> {

                if(appViewModel.currentSet.value != appViewModel.currentStudySet.value){
                    try {

                        Log.d(TAG, "Retrieving flashcards to study from Firestore")
                        appViewModel.getFlashcardsToStudy()

                        if(appViewModel.currentSet.value!!.wordsCount == appViewModel.currentSet.value!!.learned){
                            appViewModel.setStatusLearned()
                        }

                        val action = SetOptionFragmentDirections.actionSetOptionFragmentToStudyFragment()
                        findNavController().navigate(action)

                    } catch (e:Exception) {
                        Log.e(TAG, "Cannot retrieve flashcards to study from Firestore due to: ${e.message}")
                        Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d(TAG, "Flashcards to study already set")
                    val action = SetOptionFragmentDirections.actionSetOptionFragmentToStudyFragment()
                    findNavController().navigate(action)
                }

            }

            "Revise" -> {
                if(appViewModel.currentSet.value != appViewModel.currentReviseSet.value){
                    try {

                        Log.d(TAG, "Retrieving flashcards to revise from Firestore")
                        appViewModel.getFlashcardsToRevise()

                        val action = SetOptionFragmentDirections.actionSetOptionFragmentToReviseFragment()
                        findNavController().navigate(action)

                    } catch(e: Exception) {
                        Log.e(TAG, "Cannot retrieve flashcards to revise from Firestore due to: ${e.message}")
                        Toast.makeText(requireContext(), "Something went wrong...", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d(TAG, "Flashcards to revise already set")
                    val action = SetOptionFragmentDirections.actionSetOptionFragmentToReviseFragment()
                    findNavController().navigate(action)
                }
            }

        }



    }

    /** hide everything on the screen */
    private fun hideEverything() {
        binding.apply {
            setName.visibility = View.INVISIBLE
            chooseOptionHelperText.visibility = View.INVISIBLE
            studyButton.visibility = View.INVISIBLE
            reviseButton.visibility = View.INVISIBLE
            modifyButton.visibility = View.INVISIBLE
            deleteButton.visibility = View.INVISIBLE
        }
    }

    /** show everything on the screen */
    private fun showEverything() {
        binding.apply {
            setName.visibility = View.VISIBLE
            chooseOptionHelperText.visibility = View.VISIBLE
            studyButton.visibility = View.VISIBLE
            reviseButton.visibility = View.VISIBLE
            modifyButton.visibility = View.VISIBLE
            deleteButton.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}