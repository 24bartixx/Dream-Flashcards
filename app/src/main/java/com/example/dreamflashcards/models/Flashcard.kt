package com.example.dreamflashcards.models

data class Flashcard (
    val flashcardId: String,
    val term: String,
    val definition: String,
    val learned: String,
    val created: String
)