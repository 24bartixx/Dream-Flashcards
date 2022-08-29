package com.example.dreamflashcards.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcards.adapters.FlashcardsCreateAdapter
import com.example.dreamflashcards.databinding.FragmentAddFlashcardsBinding
import com.example.dreamflashcards.viewmodels.AppViewModel

class AddFlashcardsFragment : Fragment() {

    // view binding
    private var _binding: FragmentAddFlashcardsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView

    // AppViewModel
    private val appViewModel: AppViewModel by activityViewModels()

    // Progress Dialog
    private lateinit var progressDialog: ProgressDialog

    private val args: AddFlashcardsFragmentArgs by navArgs()

    companion object {
        private const val TAG = "AddFlashcardsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAddFlashcardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // configure ProgressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setTitle("Retrieving flashcards")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        /** RecyclerView setup */
        recyclerView = binding.flashcardsRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = FlashcardsCreateAdapter{ flashcard ->

        }

        recyclerView.adapter = adapter

        appViewModel.modifyFlashcards.observe(this.viewLifecycleOwner) { modifyFlashcardsList ->
            modifyFlashcardsList.let {
                if (!appViewModel.modifyFlashcards.value.isNullOrEmpty()) {

                    progressDialog.dismiss()

                    Log.d(TAG, "Flashcards list: ${appViewModel.modifyFlashcards.value}")
                    adapter.submitList(appViewModel.modifyFlashcards.value)
                    binding.addFlashcardsInfo.visibility = View.INVISIBLE

                } else if(args.fromWhere == "CreateFragment") {

                    Log.d(TAG, "Flashcards list empty")
                    binding.addFlashcardsInfo.visibility = View.VISIBLE

                } else if(appViewModel.currentSet.value!!.wordsCount == "0") {

                    progressDialog.dismiss()
                    binding.addFlashcardsInfo.visibility = View.VISIBLE

                } else if(args.fromWhere == "SetOptionFragment") {
                    binding.addFlashcardsInfo.visibility = View.INVISIBLE
                    progressDialog.show()
                }
            }
        }


        /** Add a new flashcard navigation */
        binding.addFloatingActionButton.setOnClickListener {
            val action = AddFlashcardsFragmentDirections.actionAddFlashcardsFragmentToCreateFlashcardFragment()
            findNavController().navigate(action)
        }

    }

    override fun onDestroy() {

        super.onDestroy()
        _binding = null

        // reset modification modifications in AppViewModel
        appViewModel.resetModifyVariables()

    }

}