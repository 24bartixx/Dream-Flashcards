package com.example.dreamflashcards.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dreamflashcards.models.Flashcard
import com.example.dreamflashcards.models.FlashcardsSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppViewModel: ViewModel() {

    /** Variables */

    // user's sets
    private val _sets = MutableLiveData<MutableList<FlashcardsSet>>()
    val sets: LiveData<MutableList<FlashcardsSet>> = _sets

    // current set
    private val _currentSet = MutableLiveData<FlashcardsSet>(FlashcardsSet("0", "Set Name", "Top G", "0"))
    val currentSet: LiveData<FlashcardsSet> = _currentSet

    // current create set
    private val _currentCreateSet = MutableLiveData<FlashcardsSet>()
    val currentCreateSet: LiveData<FlashcardsSet> = _currentCreateSet

    // set option
    private val _option = MutableLiveData<String>("Study")
    val option: LiveData<String> = _option

    // Flashcards to study
    private val _flashcards = MutableLiveData<List<Flashcard>>()
    val flashcards: LiveData<List<Flashcard>> = _flashcards

    // Flashcards to modify
    private val _modifyFlashcards = MutableLiveData<MutableList<Flashcard>>()
    val modifyFlashcards: LiveData<MutableList<Flashcard>> = _modifyFlashcards

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    // Firestore
    private lateinit var firestoreDatabase: FirebaseFirestore

    companion object {
        private const val TAG = "AppViewModel"
    }

    init {
        _sets.value = mutableListOf<FlashcardsSet>()
        auth = FirebaseAuth.getInstance()
        firestoreDatabase = Firebase.firestore
    }


    /** Get sets from Firestore function */
    fun getSets(): Boolean{

        val list = mutableListOf<FlashcardsSet>()
        var succeeded = false

        firestoreDatabase.collectionGroup("Sets").whereEqualTo("creator", auth.currentUser!!.uid).get()
            .addOnSuccessListener { querySnapshot ->

                Log.d(TAG, "Data retrieved: ${querySnapshot.documents}")

                for(set in querySnapshot){

                    val dataOfSet = set.data
                    val flashcardsSet = FlashcardsSet(
                        set.id, dataOfSet["name"].toString(), dataOfSet["creator"].toString(), dataOfSet["words_count"].toString())
                    Log.d(TAG, "flashcardsSet: ${flashcardsSet}")

                    list.add(flashcardsSet)

                }

                Log.d(TAG, "Adding list of sets to ViewModel: $list")
                _sets.value = list
                Log.d(TAG, "Current list of sets: ${sets.value}")
                succeeded = true

            }
            .addOnFailureListener { e ->

                Log.e(TAG, "Could not retrieve data from Firestore due to: ${e.message}")

            }

        return succeeded
    }

    /** Get current set's flashcards from Firestore function */
    fun getFlashcards():  Boolean{

        val list = mutableListOf<Flashcard>()
        var succeeded = false

        firestoreDatabase.collection("Sets").document(currentSet.value!!.setID).collection("Flashcards").get()
            .addOnSuccessListener{ querySnapshot ->

                Log.d(TAG, "Data retrieved: ${querySnapshot.documents}")

                for(flashcard in querySnapshot){

                    val dataOfFlashcard = flashcard.data
                    val flashcardObject = Flashcard(flashcard.id, dataOfFlashcard["term"].toString(), dataOfFlashcard["definition"].toString())
                    Log.d(TAG, "flashcardsSet: ${flashcardObject}")

                    list.add(flashcardObject)

                }

                Log.d(TAG, "Adding list of flashcards to ViewModel: $list")
                _flashcards.value = list
                Log.d(TAG, "Current list of  flashcards: ${flashcards.value}")
                succeeded = true

            }
            .addOnFailureListener{ e ->
                Log.e(TAG, "Could not retrieve data from Firestore due to: ${e.message}")
            }

        return succeeded

    }

    /** Add one set to Firestore */
    fun addSet(name: String){
        // HashMap of the set
        val set = hashMapOf(
            "name" to name,
            "order" to 1,
            "creator" to auth.currentUser!!.uid,
            "learned" to 0,
            "words_count" to 0
        )

        firestoreDatabase.collection("Sets")
            .add(set)
            .addOnSuccessListener { documentReference ->

                _currentCreateSet.value = FlashcardsSet(documentReference.id, name, auth.currentUser!!.uid, "0" )
                Log.d(TAG, "Set added with ID: ${documentReference.id}")

            }
            .addOnFailureListener { e ->

                Log.e(TAG, "Creation of the set in the Firestore went wrong due to: ${e.message}")

            }

    }

    /** Add one flashcard to Firestore */
    fun addFlashcard(term: String, definition: String){

        // HashMap of the flashcard
        val flashcard = hashMapOf(
            "term" to term,
            "definition" to definition,
            "learned" to false
        )

        firestoreDatabase.collection("Sets").document(currentCreateSet.value!!.setID).collection("Flashcards")
            .add(flashcard)
            .addOnSuccessListener { documentReference ->

                var listToUpdate = _modifyFlashcards.value
                if(listToUpdate.isNullOrEmpty()) {
                    listToUpdate = mutableListOf(Flashcard(documentReference.id, term, definition))
                } else {
                    listToUpdate.add(Flashcard(documentReference.id, term, definition))
                }

                if(!listToUpdate.isNullOrEmpty())  { _modifyFlashcards.value = listToUpdate!! }

                Log.d(TAG, "Flashcard added with ID: ${documentReference.id}")
                Log.d(TAG, "Current flashcards to modify: ${modifyFlashcards.value}")

            }
            .addOnFailureListener { e ->

                Log.e(TAG, "Creation of the flashcard in the Firestore went wrong due to: ${e.message}")

            }
    }

    /** Set current set */
    fun setCurrentSet(currentSet: FlashcardsSet){
        _currentSet.value = currentSet
    }

    /** Get current set */
    fun getCurrentSet(): FlashcardsSet{
        return currentSet.value!!
    }

    /** set chosen option */
    fun setOption(setOption: String){
        Log.d(TAG, "Setting option: ${setOption}")
        _option.value = setOption
    }

}