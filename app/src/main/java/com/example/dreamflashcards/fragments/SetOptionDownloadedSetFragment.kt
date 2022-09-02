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
import com.example.dreamflashcards.databinding.FragmentSetOptionDownloadedSetBinding
import com.example.dreamflashcards.viewmodels.AppViewModel

class SetOptionDownloadedSetFragment : Fragment() {

    private var _binding: FragmentSetOptionDownloadedSetBinding? = null
    private val binding get() = _binding!!

    // AppViewModel
    private val appViewModel: AppViewModel by activityViewModels()

    companion object{
        private const val TAG = "SetOptionDownloaded"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSetOptionDownloadedSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragment = this

        binding.setName.text = appViewModel.currentSet.value!!.name
        binding.setImage.setImageResource(appViewModel.currentSet.value!!.picture.toInt())
    }

    /** perform when study button clicked (data binding) */
    fun studyOption(){

        appViewModel.resetStudiedWordsCount()

        val action = SetOptionDownloadedSetFragmentDirections.actionSetOptionDownloadedSetFragmentToStudyFragment()
        findNavController().navigate(action)

    }

    /** perform when revise button clicked (data binding) */
    fun revisesOption(){
        if(appViewModel.currentSet.value!!.wordsCount.toInt() > 0) {
            val action = SetOptionDownloadedSetFragmentDirections.actionSetOptionDownloadedSetFragmentToReviseFragment()
            findNavController().navigate(action)
        } else {
            Log.d(TAG, "No flashcards to revise!")
            Toast.makeText(requireContext(), "No flashcards to revise...", Toast.LENGTH_SHORT).show()
        }
    }

    /** perform when browse button clicked */
    fun browseOption(){
        val action = SetOptionDownloadedSetFragmentDirections.actionSetOptionDownloadedSetFragmentToAddFlashcardsFragment("SetOptionFragment")
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}