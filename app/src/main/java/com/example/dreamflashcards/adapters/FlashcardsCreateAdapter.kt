package com.example.dreamflashcards.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcards.databinding.FlashcardsRecyclerviewItemBinding
import com.example.dreamflashcards.models.Flashcard

class FlashcardsCreateAdapter(private val flashcardsFunctions: (Flashcard, String) -> Unit):
    ListAdapter<Flashcard, FlashcardsCreateAdapter.FlashcardViewHolder>(DiffCallback){

    class FlashcardViewHolder(private var binding: FlashcardsRecyclerviewItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(flashcard: Flashcard, flashcardsFunctions: (Flashcard, String) -> Unit){
            binding.term.text = flashcard.term
            binding.definition.text = flashcard.definition
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardViewHolder {
        return FlashcardViewHolder(FlashcardsRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FlashcardViewHolder, position: Int) {
        val flashcard = getItem(position)
        holder.bind(flashcard, flashcardsFunctions)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Flashcard>() {

            override fun areItemsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Flashcard, newItem: Flashcard): Boolean {
                return newItem.flashcardId == oldItem.flashcardId
            }

        }
    }

}