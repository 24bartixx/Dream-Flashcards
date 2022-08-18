package com.example.dreamflashcards.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcards.databinding.SetRecyclerviewItemBinding
import com.example.dreamflashcards.models.FlashcardsSet

class SetsAdapter(private val context: Context, private val goToNextScreen: (FlashcardsSet) -> Unit):
    ListAdapter<FlashcardsSet, SetsAdapter.SetViewHolder>(DiffCallback) {

   class SetViewHolder(private var binding: SetRecyclerviewItemBinding): RecyclerView.ViewHolder(binding.root) {

       fun bind(flashcardsSet: FlashcardsSet, context: Context){
           binding.setName.text = flashcardsSet.name
           binding.wordsCount.text = "${flashcardsSet.wordsCount}/215"
       }

   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        return SetViewHolder(SetRecyclerviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {

        val flashcardsSet = getItem(position)
        holder.bind(flashcardsSet, context)

        holder.itemView.setOnClickListener{
            goToNextScreen(flashcardsSet)
        }

    }

    companion object {
        private val DiffCallback = object: DiffUtil.ItemCallback<FlashcardsSet>() {

            override fun areItemsTheSame(oldItem: FlashcardsSet, newItem: FlashcardsSet): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: FlashcardsSet, newItem: FlashcardsSet): Boolean {
                return oldItem.setID == newItem.setID
            }

        }
    }


}