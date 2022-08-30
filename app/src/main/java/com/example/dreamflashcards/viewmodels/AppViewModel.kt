package com.example.dreamflashcards.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dreamflashcards.models.Flashcard
import com.example.dreamflashcards.models.FlashcardsSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AppViewModel: ViewModel() {

    /** Variables */

    // user's sets
    private val _sets = MutableLiveData<MutableList<FlashcardsSet>>()
    val sets: LiveData<MutableList<FlashcardsSet>> = _sets

    // download sets
    private val _downloadSets = MutableLiveData<MutableList<FlashcardsSet>>()
    val downloadSets: LiveData<MutableList<FlashcardsSet>> = _downloadSets

    // current set
    private val _currentSet = MutableLiveData<FlashcardsSet>(FlashcardsSet("Empty Current set", "", "", "", "", "", "", ""))
    val currentSet: LiveData<FlashcardsSet> = _currentSet

    // current set to study
    private val _currentStudySet = MutableLiveData<FlashcardsSet>(FlashcardsSet("", "", "", "", "", "", "", ""))
    val currentStudySet: LiveData<FlashcardsSet> = _currentStudySet

    // current download set
    private val _currentDownloadSet = MutableLiveData<FlashcardsSet>(FlashcardsSet("", "", "", "", "", "", "", ""))
    val currentDownloadSet: LiveData<FlashcardsSet> = _currentDownloadSet

    // set id needed for downloading
    private val _downloadSetID = MutableLiveData<String>("")
    val downloadSetID: LiveData<String> = _downloadSetID

    // Flashcards
    private val _flashcards = MutableLiveData<MutableList<Flashcard>>()
    val flashcards: LiveData<MutableList<Flashcard>> = _flashcards

    // Flashcards to study
    private val _studyFlashcards = MutableLiveData<MutableList<Flashcard>>()
    val studyFlashcards: LiveData<MutableList<Flashcard>> = _studyFlashcards

    // how many words passed while studying
    private val _studiedWordsCount = MutableLiveData<Int>()
    val studiedWordsCount: LiveData<Int> = _studiedWordsCount

    // how many words passed while revising -> index
    private val _reviseIndex = MutableLiveData<Int>()
    val reviseIndex: LiveData<Int> = _reviseIndex

    // how many words passed while studying -> index
    private val _studyIndex = MutableLiveData<Int>()
    val studyIndex: LiveData<Int> = _studyIndex

    /** DO NOT DELETE */
    // how many words are there to be learned
    private val _studyUnlearnedNumber = MutableLiveData<Int>()
    val studyUnlearnedNumber: LiveData<Int> = _studyUnlearnedNumber

    // Flashcards to download
    private val _downloadFlashcards = MutableLiveData<MutableList<Flashcard>>()
    val downloadFlashcards: LiveData<MutableList<Flashcard>> = _downloadFlashcards

    // last flashcard to study used in shuffle method
    private val _lastStudyFlashcard = MutableLiveData<Flashcard>()
    val lastStudyFlashcard: LiveData<Flashcard> = _lastStudyFlashcard

    // indicates whether the set is learned or not
    private val _studySetStudied = MutableLiveData<Boolean>(false)
    val studySetStudied: LiveData<Boolean> = _studySetStudied

    private val _studyShuffled = MutableLiveData<Boolean>(false)
    val studyShuffled: LiveData<Boolean> = _studyShuffled

    // indicates whether set is deleted
    private val _setDeleted = MutableLiveData<Boolean>(false)
    val setDeleted: LiveData<Boolean> = _setDeleted

    // indicates whether the download set was added to Firestore
    private val _downloadSetAddedToFirestore = MutableLiveData<Boolean>(false)
    val downloadSetAddedToFirestore: LiveData<Boolean> = _downloadSetAddedToFirestore

    // indicates whether the download is completed
    private val _downloadComplete = MutableLiveData<Boolean>(false)
    val downloadComplete: LiveData<Boolean> = _downloadComplete

    // FirebaseAuth
    private var auth: FirebaseAuth

    // Firestore
    private var firestoreDatabase: FirebaseFirestore

    companion object {
        private const val TAG = "AppViewModel"
        const val MAX_STUDY = 3
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

                Log.d(TAG, "Sets retrieved: ${querySnapshot.documents}")

                for(set in querySnapshot){

                    val dataOfSet = set.data
                    val flashcardsSet = FlashcardsSet(
                        set.id, dataOfSet["name"].toString(),
                        dataOfSet["creator"].toString(),
                        dataOfSet["words_count"].toString(),
                        dataOfSet["learned"].toString(),
                        dataOfSet["type"].toString(),
                        dataOfSet["picture"].toString(),
                        dataOfSet["next"].toString()
                    )
                    Log.d(TAG, "flashcardsSet: ${flashcardsSet}")

                    list.add(flashcardsSet)

                }

                Log.d(TAG, "Adding list of sets to ViewModel: $list")
                _sets.value = list
                Log.d(TAG, "Current list of sets: ${sets.value}")
                succeeded = true

            }
            .addOnFailureListener { e ->

                Log.e(TAG, "Could not retrieve sets from Firestore due to: ${e.message}")

            }

        return succeeded
    }

    /** Get download sets from firestore function */
    fun getDownloadSets(): Boolean{

        val list = mutableListOf<FlashcardsSet>()
        var succeeded = false

        firestoreDatabase.collectionGroup("DownloadSets").get()
            .addOnSuccessListener { querySnapshot ->

                Log.d(TAG, "Download sets retrieved: ${querySnapshot.documents}")

                for(set in querySnapshot){

                    val dataOfSet = set.data
                    val flashcardsSet = FlashcardsSet(
                        set.id, dataOfSet["name"].toString(),
                        dataOfSet["creator"].toString(),
                        dataOfSet["words_count"].toString(),
                        dataOfSet["learned"].toString(),
                        dataOfSet["type"].toString(),
                        dataOfSet["picture"].toString(),
                        dataOfSet["next"].toString()
                    )
                    Log.d(TAG, "flashcardsSet: ${flashcardsSet}")

                    list.add(flashcardsSet)

                }

                Log.d(TAG, "Adding list of download sets to ViewModel: $list")
                _downloadSets.value = list
                Log.d(TAG, "Current list of download sets: ${downloadSets.value}")
                succeeded = true

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Could not retrieve download sets from Firestore due to: ${e.message}")
            }

        return succeeded

    }

    /** get set's flashcards from Firestore */
    private fun getFlashcards() {

        val list = mutableListOf<Flashcard>()
        val listToStudy = mutableListOf<Flashcard>()

        firestoreDatabase.collection("Sets").document(currentSet.value!!.setID)
            .collection("Flashcards").document("FlashcardsDocument").get()
            .addOnSuccessListener { documentSnapshot ->

                Log.d(TAG, "Flashcards retrieved: ${documentSnapshot.data}")

                for(index in 1..currentSet.value!!.next.toInt()) {

                    val termString = "term${index}"
                    val defString = "def${index}"
                    val termLearnedString = "term${index}learned"

                    val flashcardObject = Flashcard(
                        index.toString(),
                        documentSnapshot.data?.get(termString).toString(),
                        documentSnapshot.data?.get(defString).toString(),
                        documentSnapshot.data?.get(termLearnedString).toString()
                    )

                    if(flashcardObject.term != "null" && flashcardObject.definition != "null" && flashcardObject.learned != "null") {
                        list.add(flashcardObject)
                    }

                }

                Log.d(TAG, "Adding list of flashcards to ViewModel: $list")
                _flashcards.value = list
                Log.d(TAG, "Current list of flashcards: ${flashcards.value}")

                for (flashcard in flashcards.value!!){
                    if(flashcard.learned == "no"){
                        listToStudy.add(flashcard)
                    }
                }
                _studyFlashcards.value = listToStudy

                shuffleStudyFlashcards()

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Could not retrieve flashcards from Firestore due to: ${e.message}")
            }

    }

    /** Get current set's flashcards to download from Firestore function */
    fun getFlashcardsToDownload(){

        val list = mutableListOf<Flashcard>()
        _downloadFlashcards.value = mutableListOf()

        firestoreDatabase.collection("DownloadSets").document(currentDownloadSet.value!!.setID)
            .collection("DownloadFlashcards").document("FlashcardsToDownload").get()
            .addOnSuccessListener {  documentSnapshot ->

                Log.d(TAG, "Flashcards to download retrieved: ${documentSnapshot}")

                for(index in 1..currentDownloadSet.value!!.wordsCount.toInt()){

                    val termString = "term${index}"
                    val defString = "def${index}"

                    val flashcardObject = Flashcard(
                        documentSnapshot.id,
                        documentSnapshot.data?.get(termString).toString(),
                        documentSnapshot.data?.get(defString).toString(),
                        "no"
                    )

                    list.add(flashcardObject)

                }

                _downloadFlashcards.value = list
                Log.d(TAG, "Current list of download flashcards: ${downloadFlashcards.value}")

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Could not retrieve flashcards to download from Firestore due to: ${e.message}")
            }

    }

    /** Add one set to Firestore */
    fun addSet(name: String){

        _flashcards.value = mutableListOf()
        _currentSet.value = FlashcardsSet("", "", "", "", "", "", "", "")

        // HashMap of the set
        val set = hashMapOf(
            "name" to name,
            "creator" to auth.currentUser!!.uid,
            "learned" to 0,
            "words_count" to 0,
            "type" to "user",
            "picture" to "none",
            "next" to 1
        )

        firestoreDatabase.collection("Sets")
            .add(set)
            .addOnSuccessListener { documentReference ->

                _currentSet.value = FlashcardsSet(documentReference.id, name, auth.currentUser!!.uid, "0", "0", "user", "none", "1")
                Log.d(TAG, "Set added to Firestore with ID: ${documentReference.id}")
                Log.d(TAG, "New currentSet: ${currentSet.value}")

            }
            .addOnFailureListener { e ->

                Log.e(TAG, "Creation of the set in the Firestore went wrong due to: ${e.message}")

            }

    }

    /** Add one flashcard to Firestore */
    fun addFlashcard(term: String, definition: String){

        val flashcard = hashMapOf(
            "term${currentSet.value!!.next}" to term,
            "def${currentSet.value!!.next}" to definition,
            "term${currentSet.value!!.next}learned" to "no"
        )

        Log.d(TAG, "Adding flashcard")

        firestoreDatabase.collection("Sets").document(currentSet.value!!.setID)
            .collection("Flashcards").document("FlashcardsDocument").set(flashcard, SetOptions.merge())
            .addOnSuccessListener {

                var listToUpdate = flashcards.value
                if(listToUpdate.isNullOrEmpty()) {
                    listToUpdate = mutableListOf(Flashcard(currentSet.value!!.next, term, definition, "no"))
                } else {
                    listToUpdate.add(Flashcard(currentSet.value!!.next, term, definition, "no"))
                }

                if(!listToUpdate.isNullOrEmpty())  { _flashcards.value = listToUpdate!! }

                updateSetWordsCount()

                Log.d(TAG, "Flashcard added to document with id: \"FlashcardsDocument\"")
                Log.d(TAG, "Current flashcards: ${flashcards.value}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Adding flashcard in the Firestore went wrong due to: ${e.message}")
            }
    }

    /** Add one download set to Firestore */
    fun addDownloadSet(){

        // HashMap of the set
        val set = hashMapOf(
            "name" to currentDownloadSet.value!!.name,
            "creator" to auth.currentUser!!.uid,
            "learned" to 0,
            "words_count" to currentDownloadSet.value!!.wordsCount.toInt(),
            "next" to currentDownloadSet.value!!.next.toInt(),
            "type" to currentDownloadSet.value!!.type,
            "picture" to currentDownloadSet.value!!.picture
        )

        _downloadSetAddedToFirestore.value = false

        firestoreDatabase.collection("Sets").add(set)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Download set added to user's sets with id: ${documentReference.id}")
                _downloadSetID.value = documentReference.id
                _downloadSetAddedToFirestore.value = true
                _downloadFlashcards.value = downloadFlashcards.value
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to add download set to user's sets")
            }

    }

    /** Add download flashcards to Firestore */
    fun addDownloadFlashcards(){

        val flashcards = hashMapOf<String, String>()
        var index = 1

        for(downloadFlashcard in downloadFlashcards.value!!){
            flashcards["term${index}"] = downloadFlashcard.term
            flashcards["def${index}"] = downloadFlashcard.definition
            flashcards["term${index}learned"] = "no"
            index += 1
        }

        Log.d(TAG, "Adding download flashcards to Firebase: ${flashcards}")

        firestoreDatabase.collection("Sets").document(downloadSetID.value!!)
            .collection("Flashcards").document("FlashcardsDocument").set(flashcards)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Download flashcards saved in Firestore in a document with id: \"FlashcardsDocument\"")
                _downloadComplete.value = true
                _downloadFlashcards.value = mutableListOf()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Failed to save download flashcards due to: ${e.message}")
            }

    }

    /** Delete flashcards document from Firestore */
    fun deleteFlashcardsDocument(){

        firestoreDatabase.collection("Sets").document(currentSet.value!!.setID)
            .collection("Flashcards").document("FlashcardsDocument").delete()
            .addOnSuccessListener {
                Log.d(TAG, "Flashcards document successfully deleted!")
                deleteSet()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting flashcards document due to: ${e.message}") }

    }

    /** Delete set from Firestore */
    private fun deleteSet(){

        firestoreDatabase.collection("Sets").document(currentSet.value!!.setID).delete()
            .addOnSuccessListener {

                _setDeleted.value = true
                Log.d(TAG, "Set document successfully deleted!")

//                val listOfSets = sets.value!!
//                for(set in listOfSets){
//                    if(currentSet.value!!.setID == set.setID) {
//                        listOfSets.remove(set)
//                    }
//                }
//                _sets.value = listOfSets

            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting set document due to: ${e.message}") }

    }

    /** update "words_count" of the modified set */
    fun updateSetWordsCount(){

        val hashMapToUpdate = hashMapOf(
            "words_count" to currentSet.value!!.wordsCount.toInt() + 1,
            "next" to currentSet.value!!.next.toInt() + 1
        )

        firestoreDatabase.collection("Sets").document(currentSet.value!!.setID).set(hashMapToUpdate, SetOptions.merge())
            .addOnSuccessListener {
                _currentSet.value!!.wordsCount = (currentSet.value!!.wordsCount.toInt() + 1).toString()
                _currentSet.value!!.next = (currentSet.value!!.next.toInt() + 1).toString()
            }

    }

    /** update "learned" of the studied set */
    fun updateSetLearned(){

        val hashMapToUpdate = hashMapOf(
            "learned" to currentSet.value!!.learned.toInt() + 1
        )

        Log.d(TAG, "Updating \"learned\" of the studied set")
        firestoreDatabase.collection("Sets").document(currentStudySet.value!!.setID).set(hashMapToUpdate, SetOptions.merge())
            .addOnSuccessListener{

                var newCurrentStudySet =  currentStudySet.value!!
                newCurrentStudySet.learned = (currentStudySet.value!!.learned.toInt() + 1).toString()
                _currentStudySet.value = newCurrentStudySet

                Log.d(TAG, "Updated \"learned\" of the studied set")

            }
    }

    /** make flashcard learned */
    fun setFlashcardLearnedToFirestore(properIndex: Int){

        // flashcard to be changed to learned
        Log.d(TAG, "Updating flashcard with with index ${properIndex} to learned")
        val flashcard = studyFlashcards.value!![properIndex]
        val updatedFlashcard = Flashcard(flashcard.flashcardId, flashcard.term, flashcard.definition, "yes")

        // update viewModel
        val list = studyFlashcards.value!!
        list[properIndex] = updatedFlashcard
        _studyFlashcards.value = list

        Log.d(TAG, "ViewModel updated with learned flashcard")

        // hashMap to update Firebase
        val data = hashMapOf("learned" to "yes")

        // update Firestore
        firestoreDatabase.collection("Sets").document(currentStudySet.value!!.setID)
            .collection("Flashcards").document(flashcard.flashcardId).set(data, SetOptions.merge())
            .addOnSuccessListener {
                updateSetLearned()
            }

        Log.d(TAG, "Flashcard with ID ${flashcard.flashcardId} updated to learned")

    }

    /** Remove learned flashcards to study */
    fun removeLearned(){

        Log.d(TAG, "Removing most flashcards")

        var list = mutableListOf<Flashcard>()

        for(flashcard in studyFlashcards.value!!){
            if(flashcard.learned == "no") {
                Log.d(TAG, "Unlearned flashcard: ${flashcard.flashcardId}")
                list.add(flashcard)
            }
        }

        ///resetStudiedWordsCount()
        _studyFlashcards.value = list

        Log.d(TAG, "Removed all learned flashcards from list to study")

    }

    /** Shuffle list of flashcards to study */
    fun shuffleStudyFlashcards(){

        Log.d(TAG, "Shuffling flashcards to study")

        if(studyFlashcards.value!!.size > 0) {

            var list = studyFlashcards.value!!.shuffled()
            Log.d(TAG, "Shuffled list: $list")

            while (lastStudyFlashcard.value == list[0] && studyFlashcards.value!!.size != 1) {
                list = studyFlashcards.value!!.shuffled()
                Log.d(TAG, "Shuffled list: $list")
            }

            var mutableList: MutableList<Flashcard> = mutableListOf()
            mutableList.addAll(list)
            _studyFlashcards.value = mutableList
            Log.d(TAG, "Shuffled list of flashcards: ${studyFlashcards.value}")

            _lastStudyFlashcard.value = list[list.size - 1]

        }

        _studyShuffled.value = true

    }

    /** reset studiedWordsCount variable */
    fun resetStudiedWordsCount(){
        _studiedWordsCount.value = 0
        Log.d(TAG, "Studied words count reset to 0")
    }

    /** reset reviseIndex variable */
    fun resetReviseIndex(){
        _reviseIndex.value = 0
        Log.d(TAG, "Revise index reset to 0")
    }

    /** reset studyIndex variable */
    fun resetStudyIndex(){
        _studyIndex.value = 0
        Log.d(TAG, "Study index reset to 0")
    }

    /** reset setDeleted variable */
    fun resetSetDeleted(){
        _setDeleted.value = false
    }

    /** reset downloadComplete variable */
    fun resetDownloadComplete(){
        _downloadComplete.value = false
    }

    /** all flashcards to study are learned */
    fun setStatusLearned() {
        if(studyFlashcards.value!!.size == 0) {
            Log.d(TAG, "Setting current study set as learned")
            _studySetStudied.value = true
            _studyFlashcards.value = mutableListOf()
        }
    }

    /** Increment studied words count */
    fun incrementStudiedWordsCount(){
        _studiedWordsCount.value = studiedWordsCount.value!! + 1
        Log.d(TAG, "Studied words value: ${studiedWordsCount.value}")
    }

    /** Increment revise index */
    fun incrementReviseIndex(){
        _reviseIndex.value = reviseIndex.value!! + 1
    }

    /** Increment study index */
    fun incrementStudyIndex(){
        _studyIndex.value = studyIndex.value!! + 1
    }

    /** Set current set */
    fun setCurrentSet(newCurrentSet: FlashcardsSet){
        _flashcards.value = mutableListOf()
        _studyFlashcards.value = mutableListOf()
        _currentSet.value = newCurrentSet
        _studyIndex.value = 0
        Log.d(TAG, "New currentSet: ${currentSet.value}")
        getFlashcards()
    }

    /** set current download set */
    fun setCurrentDownloadSet(newCurrentDownloadSet: FlashcardsSet) {
        _currentDownloadSet.value = newCurrentDownloadSet
        Log.d(TAG, "New currentDownloadSet: ${currentDownloadSet.value}")
    }

    /** Get current set */
    fun getCurrentSet(): FlashcardsSet{
        return currentSet.value!!
    }

}