package com.example.dreamflashcards.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dreamflashcards.R
import com.example.dreamflashcards.models.FlashcardsSet

class SetsAdapter(private val context: Context, private val sets: List<FlashcardsSet>):
    RecyclerView.Adapter<SetsAdapter.SetViewHolder>() {

   class SetViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.set_name)
        val wordsCount = view.findViewById<TextView>(R.id.words_count)
   }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.set_recyclerview_item, parent, false)
        return SetViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: SetViewHolder, position: Int) {
        holder.name.text = context.getString(R.string.set_name, sets[position].name)
        holder.wordsCount.text = context.getString(R.string.word_count, 5, sets[position].wordsCount)
    }

    override fun getItemCount(): Int {
        return sets.size
    }


}