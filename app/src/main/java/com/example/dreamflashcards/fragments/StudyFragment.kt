package com.example.dreamflashcards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dreamflashcards.databinding.FragmentStudyBinding

class StudyFragment : Fragment() {

    private var _binding: FragmentStudyBinding? = null
    private val binding get() = _binding!!

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

        binding.apply{
            // initial visibility configuration
            yesButton.visibility = View.INVISIBLE
            noButton.visibility = View.INVISIBLE
            mediumButton.visibility = View.INVISIBLE
            definitionCard.visibility = View.INVISIBLE

            // onClickListeners
            showDefinitionButton.setOnClickListener{
                changeVisibility("Show definition")
            }
            yesButton.setOnClickListener{
                changeVisibility("Yes")
            }
            noButton.setOnClickListener {
                changeVisibility("No")
            }
            mediumButton.setOnClickListener {
                changeVisibility("Medium")
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
            binding.apply {
                definitionCard.visibility = View.GONE
                yesButton.visibility = View.GONE
                mediumButton.visibility = View.GONE
                noButton.visibility = View.GONE
                showDefinitionButton.visibility = View.VISIBLE
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}