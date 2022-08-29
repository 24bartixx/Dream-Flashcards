package com.example.dreamflashcards.models

data class FlashcardsSet(
    val setID: String,
    val name: String,
    val creator: String,
    var wordsCount: String,
    var learned: String,
    val type: String,
    val picture: String,
    var next: String
)