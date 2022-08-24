package com.example.dreamflashcards.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcards.databinding.SetDownloadRecyclerviewItemBinding
import com.example.dreamflashcards.models.FlashcardsSet
import com.google.common.collect.Sets

class DownloadsAdapter(): ListAdapter<FlashcardsSet, DownloadsAdapter.DownloadsViewHolder>(DiffCallback) {

    class DownloadsViewHolder(private var binding: SetDownloadRecyclerviewItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.setName.text = flashcardsSet.name
            binding.wordsCount.text = "${flashcardsSet.learned}/${flashcardsSet.wordsCount}"
        }
    }

    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<FlashcardsSet>{

            override fun areItemsTheSame(oldItem: FlashcardsSet, newItem: FlashcardsSet): Boolean {
                return newItem == oldItem
            }

            override fun areContentsTheSame(oldItem: FlashcardsSet, newItem: FlashcardsSet): Boolean {
                return newItem.setID == oldItem.setID
            }

        }
    }

}