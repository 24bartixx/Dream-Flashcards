package com.example.dreamflashcards.models

data class FlashcardsSet(
    val setID: String,
    val name: String,
    val creator: String,
    val wordsCount: String,
    var learned: String
)