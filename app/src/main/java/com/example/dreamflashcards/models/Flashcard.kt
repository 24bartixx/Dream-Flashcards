package com.example.dreamflashcards.models

data class Flashcard (
    val flashcardId: String,
    var term: String,
    var definition: String,
    var learned: String
)