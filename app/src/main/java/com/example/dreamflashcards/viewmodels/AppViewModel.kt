package com.example.dreamflashcards.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dreamflashcards.models.Flashcard
import com.example.dreamflashcards.models.FlashcardsSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
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

    // current set to modify
    private val _currentCreateSet = MutableLiveData<FlashcardsSet>(FlashcardsSet("", "", "", "", "", "", "", ""))
    val currentCreateSet: LiveData<FlashcardsSet> = _currentCreateSet

    // current set to study
    private val _currentStudySet = MutableLiveData<FlashcardsSet>(FlashcardsSet("", "", "", "", "", "", "", ""))
    val currentStudySet: LiveData<FlashcardsSet> = _currentStudySet

    // current download set
    private val _currentDownloadSet = MutableLiveData<FlashcardsSet>(FlashcardsSet("", "", "", "", "", "", "", ""))
    val currentDownloadSet: LiveData<FlashcardsSet> = _currentDownloadSet

    // current set to revise
    private val _currentReviseSet = MutableLiveData<FlashcardsSet>(FlashcardsSet("", "", "", "", "", "", "", ""))
    val currentReviseSet: LiveData<FlashcardsSet> = _currentReviseSet

    // set id needed for downloading
    private val _downloadSetID = MutableLiveData<String>("")
    val downloadSetID: LiveData<String> = _downloadSetID

    // set option
    private val _option = MutableLiveData<String>("Study")
    val option: LiveData<String> = _option

    // how many words passed while studying
    private val _studiedWordsCount = MutableLiveData<Int>()
    val studiedWordsCount: LiveData<Int> = _studiedWordsCount

    // how many words passed while revising -> index
    private val _reviseIndex = MutableLiveData<Int>()
    val reviseIndex: LiveData<Int> = _reviseIndex

    // Flashcards to study
    private val _studyFlashcards = MutableLiveData<MutableList<Flashcard>>()
    val studyFlashcards: LiveData<MutableList<Flashcard>> = _studyFlashcards

    // Flashcards to revise
    private val _reviseFlashcards = MutableLiveData<MutableList<Flashcard>>()
    val reviseFlashcards: LiveData<MutableList<Flashcard>> = _reviseFlashcards

    // Flashcards to modify
    private val _modifyFlashcards = MutableLiveData<MutableList<Flashcard>>()
    val modifyFlashcards: LiveData<MutableList<Flashcard>> = _modifyFlashcards

    // Flashcards to download
    private val _downloadFlashcards = MutableLiveData<MutableList<Flashcard>>()
    val downloadFlashcards: LiveData<MutableList<Flashcard>> = _downloadFlashcards

    // flashcards to study -> latest flashcard -> time of creation
    private val _latestStudyFlashcardTime = MutableLiveData<Long>(0)
    val latestStudyFlashcardTime: LiveData<Long> = _latestStudyFlashcardTime

    private val _latestReviseFlashcardTime = MutableLiveData<Long>(0)
    val latestReviseFlashcardTime: LiveData<Long> = _latestReviseFlashcardTime

    // last flashcard to study used in shuffle method
    private val _lastStudyFlashcard = MutableLiveData<Flashcard>()
    val lastStudyFlashcard: LiveData<Flashcard> = _lastStudyFlashcard

    // indicates whether the set is learned or not
    private val _studySetStudied = MutableLiveData<Boolean>(false)
    val studySetStudied: LiveData<Boolean> = _studySetStudied

    // indicates whether the download set was added to Firestore
    private val _downloadSetAddedToFirestore = MutableLiveData<Boolean>(false)
    val downloadSetAddedToFirestore: LiveData<Boolean> = _downloadSetAddedToFirestore

    // indicated whether the download is completed
    private val _downloadComplete = MutableLiveData<Boolean>(false)
    val downloadComplete: LiveData<Boolean> = _downloadComplete

    // FirebaseAuth
    private var auth: FirebaseAuth

    // Firestore
    private var firestoreDatabase: FirebaseFirestore

    companion object {
        private const val TAG = "AppViewModel"
        const val MAX_STUDY = 3
        const val MAX_REVISE = 4
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

    /** Get current set's flashcards to study from Firestore function */
    fun getFlashcardsToStudy(){

        val list = mutableListOf<Flashcard>()
        _currentStudySet.value = FlashcardsSet("", "", "", "", "", "", "", "")
        _lastStudyFlashcard.value = Flashcard("", "", "", "")
        _studiedWordsCount.value = 0
        _latestStudyFlashcardTime.value  = 0
        _studyFlashcards.value = mutableListOf()

        firestoreDatabase.collection("Sets").document(currentSet.value!!.setID).collection("Flashcards")
            .whereEqualTo("learned", "no").orderBy("created", Query.Direction.ASCENDING).limit(MAX_STUDY.toLong()).get()
            .addOnSuccessListener{ querySnapshot ->

                Log.d(TAG, "Flashcards to study retrieved: ${querySnapshot.documents}")

                for(flashcard in querySnapshot){

                    val dataOfFlashcard = flashcard.data
                    val flashcardObject = Flashcard(
                        flashcard.id,
                        dataOfFlashcard["term"].toString(),
                        dataOfFlashcard["definition"].toString(),
                        dataOfFlashcard["learned"].toString()
                    )
                    Log.d(TAG, "flashcardsSet: ${flashcardObject}")

                    list.add(flashcardObject)

//                    if(flashcardObject.created.toLong() > latestStudyFlashcardTime.value!!){
//                        _latestStudyFlashcardTime.value = flashcardObject.created.toLong()
//                    }
                }

                Log.d(TAG, "Adding list of flashcards to study to ViewModel: $list")
                _studyFlashcards.value = list
                Log.d(TAG, "Current list of flashcards to study: ${studyFlashcards.value}")

                Log.d(TAG, "Size of current list of flashcards to study: ${studyFlashcards.value!!.size}")

                Log.d(TAG, "Setting currentStudySet to currentSet value: ${currentSet.value}")
                _currentStudySet.value = currentSet.value
                Log.d(TAG, "New currentStudySet: ${currentStudySet.value}")

                shuffleFlashcards()

            }
            .addOnFailureListener{ e ->
                Log.e(TAG, "Could not retrieve flashcards to study from Firestore due to: ${e.message}")
            }

    }

    /** Get current set's flashcards to revise from Firestore function */
    fun getFlashcardsToRevise(){

        val list = mutableListOf<Flashcard>()
        _currentReviseSet.value = FlashcardsSet("", "", "", "", "", "", "", "")
        _reviseIndex.value = 0
        _latestReviseFlashcardTime.value  = 0
        _reviseFlashcards.value = mutableListOf()

        firestoreDatabase.collection("Sets").document(currentSet.value!!.setID).collection("Flashcards")
            .orderBy("created", Query.Direction.ASCENDING).limit(MAX_REVISE.toLong()).get()
            .addOnSuccessListener { querySnapshot ->

                Log.d(TAG, "Flashcards to revise retrieved: ${querySnapshot.documents}")

                for(flashcard in querySnapshot){

                    val dataOfFlashcard = flashcard.data
                    val flashcardObject = Flashcard(
                        flashcard.id,
                        dataOfFlashcard["term"].toString(),
                        dataOfFlashcard["definition"].toString(),
                        dataOfFlashcard["learned"].toString()
                    )

                    Log.d(TAG, "flashcardsSet: ${flashcardObject}")

                    list.add(flashcardObject)

                }

                Log.d(TAG, "Adding list of flashcards to revise to ViewModel: $list")
                _reviseFlashcards.value = list
                Log.d(TAG, "Current list of flashcards to revise: ${reviseFlashcards.value}")

                Log.d(TAG, "Size of current list of flashcards to study: ${reviseFlashcards.value!!.size}")

                Log.d(TAG, "Setting currentReviseSet to currentSet value: ${currentSet.value}")
                _currentReviseSet.value = currentSet.value
                Log.d(TAG, "New currentReviseSet: ${currentStudySet.value}")

                // setting "created" of latest flashcard to revise
                //_latestReviseFlashcardTime.value = list[list.size - 1].created.toLong()
                Log.d(TAG, "Latest revise time: ${latestReviseFlashcardTime.value!!}")

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Could not retrieve flashcard to revise from Firestore due to: ${e.message}")
            }

    }

    /** Get current set's flashcards to modify from Firestore function */
    fun getFlashcardsToModify(){

        val list = mutableListOf<Flashcard>()
        _currentCreateSet.value = FlashcardsSet("", "", "", "", "", "", "", "")
        _modifyFlashcards.value = mutableListOf()

        firestoreDatabase.collection("Sets").document(currentSet.value!!.setID)
            .collection("Flashcards").document("FlashcardsDocument").get()
            .addOnSuccessListener { documentSnapshot ->

                Log.d(TAG, "Flashcards to modify retrieved: ${documentSnapshot.data}")

                for(index in 1..currentSet.value!!.wordsCount.toInt()){

                    val termString = "term${index}"
                    val defString = "def${index}"
                    val termLearnedString = "term${index}learned"

                    val flashcardObject = Flashcard(
                        documentSnapshot.id,
                        documentSnapshot.data?.get(termString).toString(),
                        documentSnapshot.data?.get(defString).toString(),
                        documentSnapshot.data?.get(termLearnedString).toString()
                    )

                    list.add(flashcardObject)

                }

                Log.d(TAG, "Adding list of flashcards to modify to ViewModel: $list")
                _modifyFlashcards.value = list
                Log.d(TAG, "Current list of flashcards to modify: ${modifyFlashcards.value}")

                Log.d(TAG, "Setting currentCreateSet to currentSet value: ${currentSet.value}")
                _currentCreateSet.value = currentSet.value
                Log.d(TAG, "New currentCreateSet: ${currentCreateSet.value}")

            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Could not retrieve flashcards to modify from Firestore due to: ${e.message}")
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

        _modifyFlashcards.value = mutableListOf()
        _currentCreateSet.value = FlashcardsSet("", "", "", "", "", "", "", "")

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

                _currentCreateSet.value = FlashcardsSet(documentReference.id, name, auth.currentUser!!.uid, "0", "0", "user", "none", "1")
                Log.d(TAG, "Set added to Firestore with ID: ${documentReference.id}")
                Log.d(TAG, "New currentCreateSet: ${currentCreateSet.value}")

            }
            .addOnFailureListener { e ->

                Log.e(TAG, "Creation of the set in the Firestore went wrong due to: ${e.message}")

            }

    }

    /** Add one flashcard to Firestore */
    fun addFlashcard(term: String, definition: String){

        //val currentTimeInt = System.currentTimeMillis()

        val flashcard = hashMapOf(
            "term${currentCreateSet.value!!.next.toInt()}" to term,
            "def${currentCreateSet.value!!.next.toInt()}" to definition,
            "term${currentCreateSet.value!!.next.toInt()}learned" to "no",
        )

        firestoreDatabase.collection("Sets").document(currentCreateSet.value!!.setID)
            .collection("Flashcards").document("FlashcardsDocument").set(flashcard, SetOptions.merge())
            .addOnSuccessListener {

                var listToUpdate = _modifyFlashcards.value
                if(listToUpdate.isNullOrEmpty()) {
                    listToUpdate = mutableListOf(Flashcard(currentCreateSet.value!!.next, term, definition, "no"))
                } else {
                    listToUpdate.add(Flashcard(currentCreateSet.value!!.next, term, definition, "no"))
                }

                if(!listToUpdate.isNullOrEmpty())  { _modifyFlashcards.value = listToUpdate!! }

                updateSetWordsCount()

                Log.d(TAG, "Flashcard added to document with id: \"FlashcardsDocument\"")
                Log.d(TAG, "Current flashcards to modify: ${modifyFlashcards.value}")
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
            "words_count" to currentDownloadSet.value!!.wordsCount,
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

        firestoreDatabase.collection("Sets").document(downloadSetID.value!!).collection("Flashcards").add(flashcards)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Download flashcards saved in Firestore in a document with id: ${documentReference.id}")
                _downloadComplete.value = true
                _downloadFlashcards.value = mutableListOf()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "Failed to save download flashcards due to: ${e.message}")
            }

    }

    /** Delete set from Firestore */
    fun deleteSet(){

        val path = "/Sets/" + currentSet.value!!.setID + "/Flashcards"

        val deleteFn = Firebase.functions.getHttpsCallable("deleteFlashcards")
        deleteFn.call(hashMapOf("path" to path))
            .addOnSuccessListener {
                Log.d(TAG, "Deleting the set")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to delete the set due to: ${e.message}")
            }

    }

    /** update "words_count" of the modified set */
    fun updateSetWordsCount(){

        val hashMapToUpdate = hashMapOf(
            "words_count" to currentCreateSet.value!!.wordsCount.toInt() + 1,
            "next" to currentCreateSet.value!!.next.toInt() + 1
        )

        firestoreDatabase.collection("Sets").document(currentCreateSet.value!!.setID).set(hashMapToUpdate, SetOptions.merge())
            .addOnSuccessListener {
                _currentCreateSet.value!!.wordsCount = (currentCreateSet.value!!.wordsCount.toInt() + 1).toString()
                _currentCreateSet.value!!.next = (currentCreateSet.value!!.next.toInt() + 1).toString()
            }

    }

    /** update "learned" of the studied set */
    fun updateSetLearned(){

        val hashMapToUpdate = hashMapOf(
            "learned" to currentStudySet.value!!.learned.toInt() + 1
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

    /** get additional flashcards, shuffle, and set StudiedWordsCount to 0 */
    fun getMoreStudyFlashcards(){

        if(studyFlashcards.value!!.size < MAX_STUDY){

            // get additional flashcards to study from Firestore
            val howManyToGet = MAX_STUDY - studyFlashcards.value!!.size

            // get additional flashcards from Firestore
            firestoreDatabase.collection("Sets").document(currentStudySet.value!!.setID).collection("Flashcards")
                .whereGreaterThan("created", latestStudyFlashcardTime.value!!).orderBy("created", Query.Direction.ASCENDING).limit(howManyToGet.toLong())
                .get()
                .addOnSuccessListener { querySnapshot ->

                    Log.d(TAG, "Additional flashcards to study retrieved: ${querySnapshot.documents}")
                    var list = mutableListOf<Flashcard>()

                    for(flashcard in querySnapshot){

                        val dataOfFlashcard = flashcard.data
                        val flashcardObject = Flashcard(
                            flashcard.id,
                            dataOfFlashcard["term"].toString(),
                            dataOfFlashcard["definition"].toString(),
                            dataOfFlashcard["learned"].toString()
                        )
                        Log.d(TAG, "Flashcard: ${flashcardObject}")

                        list.add(flashcardObject)

//                        if(flashcardObject.created.toLong() > latestStudyFlashcardTime.value!!){
//                            _latestStudyFlashcardTime.value = flashcardObject.created.toLong()
//                        }

                    }

                    Log.d(TAG, "Adding list of flashcards to study to ViewModel: $list")
                    _studyFlashcards.value!!.addAll(list)
                    Log.d(TAG, "Current list of flashcards to study: ${studyFlashcards.value}")

                    resetStudiedWordsCount()
                    shuffleFlashcards()

                }
                .addOnFailureListener { e ->

                    Log.e(TAG, "An error occurred during getting additional flashcards to study due to: ${e.message}")
                    resetStudiedWordsCount()
                    shuffleFlashcards()

                }

        } else {
            resetStudiedWordsCount()
            shuffleFlashcards()
        }

    }

    /** get additional flashcards, shuffle, and set reviseIndex to 0 */
    fun getMoreReviseFlashcards(){

        if(currentReviseSet.value!!.wordsCount.toInt() == reviseFlashcards.value!!.size){

            Log.d(TAG, "No more flashcards to revise to retrieve")
            resetReviseIndex()
            _reviseFlashcards.value = reviseFlashcards.value!!
            Log.d(TAG, "Flashcards to revise: ${reviseFlashcards.value}")

        } else {

            firestoreDatabase.collection("Sets").document(currentReviseSet.value!!.setID).collection("Flashcards")
                .orderBy("created").whereGreaterThan("created", latestReviseFlashcardTime.value!!).limit(MAX_REVISE.toLong()).get()
                .addOnSuccessListener { querySnapshot ->

                    Log.d(TAG, "Additional flashcards to revise retrieved: ${querySnapshot.documents}")
                    var list = mutableListOf<Flashcard>()

                    for(flashcard in querySnapshot){

                        val dataOfFlashcard = flashcard.data
                        val flashcardObject = Flashcard(
                            flashcard.id,
                            dataOfFlashcard["term"].toString(),
                            dataOfFlashcard["definition"].toString(),
                            dataOfFlashcard["learned"].toString()
                        )
                        Log.d(TAG, "Flashcard: ${flashcardObject}")

                        list.add(flashcardObject)

                    }

                    // setting "created" of latest flashcard to revise
                    // _latestReviseFlashcardTime.value = list[list.size - 1].created.toLong()
                    Log.d(TAG, "Latest revise time: ${latestReviseFlashcardTime.value!!}")

                    Log.d(TAG, "Adding list of flashcards to revise to ViewModel: $list")

                    val listToUpdate = reviseFlashcards.value!!
                    listToUpdate.addAll(list)
                    _reviseFlashcards.value = listToUpdate

                    Log.d(TAG, "Current list of flashcards to revise: ${reviseFlashcards.value}")

                }
                .addOnFailureListener { e ->

                    Log.e(TAG, "An error occurred during getting additional flashcards to revise due to: ${e.message}")
                    resetReviseIndex()

                }

        }

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
    fun shuffleFlashcards(){

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

    /** reset downloadComplete variable */
    fun resetDownloadComplete(){
        _downloadComplete.value = false
    }

    /** reset modification variables */
    fun resetModifyVariables(){
        _currentCreateSet.value = FlashcardsSet("", "", "", "", "", "", "", "")
        _modifyFlashcards.value = mutableListOf()
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

    /** Set current set */
    fun setCurrentSet(newCurrentSet: FlashcardsSet){
        _currentSet.value = newCurrentSet
        Log.d(TAG, "New currentSet: ${currentSet.value}")
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