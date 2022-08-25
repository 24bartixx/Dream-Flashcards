package com.example.dreamflashcards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.dreamflashcards.databinding.FragmentSetOptionDownloadBinding
import com.example.dreamflashcards.viewmodels.AppViewModel

class SetOptionDownloadFragment : Fragment() {

    // view binding
    private var _binding: FragmentSetOptionDownloadBinding? = null
    private val binding get() = _binding!!

    // AppViewModel
    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSetOptionDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appViewModel = appViewModel
        binding.setOptionDownloadFragment = this

    }

    fun goToTheNextScreen(){

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}