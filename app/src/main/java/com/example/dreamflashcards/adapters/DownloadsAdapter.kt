package com.example.dreamflashcards.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcards.R
import com.example.dreamflashcards.databinding.SetDownloadRecyclerviewItemBinding
import com.example.dreamflashcards.models.FlashcardsSet

class DownloadsAdapter(private val goToNextScreen: (FlashcardsSet) -> Unit): ListAdapter<FlashcardsSet, DownloadsAdapter.DownloadsViewHolder>(DiffCallback) {

    class DownloadsViewHolder(private var binding: SetDownloadRecyclerviewItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(flashcardsSet: FlashcardsSet){
            binding.setName.text = flashcardsSet.name
            binding.wordsCount.text = flashcardsSet.wordsCount
            binding.setIcon.setImageResource(R.drawable.english_icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadsViewHolder {
        return DownloadsViewHolder(SetDownloadRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: DownloadsAdapter.DownloadsViewHolder, position: Int) {

        val flashcardsSet = getItem(position)
        holder.bind(flashcardsSet)

        holder.itemView.setOnClickListener {
            goToNextScreen(flashcardsSet)
        }

    }

    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<FlashcardsSet>() {

            override fun areItemsTheSame(oldItem: FlashcardsSet, newItem: FlashcardsSet): Boolean {
                return newItem == oldItem
            }

            override fun areContentsTheSame(oldItem: FlashcardsSet, newItem: FlashcardsSet): Boolean {
                return newItem.setID == oldItem.setID
            }

        }
    }

}