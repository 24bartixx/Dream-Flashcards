package com.example.dreamflashcards

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.dreamflashcards.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    // view binding
    private lateinit var binding: ActivitySignupBinding

    // Firebase Auth
    private lateinit var auth: FirebaseAuth

    // ActionBar to support navigation back
    private lateinit var actionBar: ActionBar

    // ProgressDialog
    private lateinit var progressDialog: ProgressDialog

    private var email = ""
    private var password = ""

    companion object {
        private const val EMAIL_TAG = "EmailAuthorization"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // configure ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Waiting")
        progressDialog.setMessage("Creating account")
        progressDialog.setCanceledOnTouchOutside(false)

        // configure ActionBar to support navigation back
        actionBar = supportActionBar!!
        actionBar.title = "Sign Up"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        // get instance of Firebase Auth
        auth = FirebaseAuth.getInstance()

        // if register button pressed validate the input data
        binding.loginButton.setOnClickListener {
            validateData()
        }

    }

    /** Go to previous activity support function */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // go back to previous activity, when back button of actionbar is clicked
        return super.onSupportNavigateUp()
    }

    /** Validate data before registration function */
    private fun validateData(){
        email = binding.emailEditText.text.toString().trim()
        password = binding.passwordEditText.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            // if email input text has incorrect format, trigger an error
            Log.e(EMAIL_TAG, "Incorrect email format")
            binding.emailEditText.error = "Incorrect email format"

        } else if(password.length < 7) {
            // if password has not at least 7 characters, trigger an error
            Log.e(EMAIL_TAG, "Too short password, must be at least 7 characters")
            binding.passwordInputLayout.isPasswordVisibilityToggleEnabled = false
            binding.passwordEditText.error = "Your password must have at least 7 characters"

            // show toggle visibility icon
            addPasswordToggleIconListener("pass")
        } else if (checkIfCapitalLetter(password)) {

            // if password has not a capital letter, trigger an error
            Log.e(EMAIL_TAG, "No capital letter in the password")
            binding.passwordInputLayout.isPasswordVisibilityToggleEnabled = false
            binding.passwordEditText.error = "Your password must have a capital letter"

            // show toggle visibility icon listener
            addPasswordToggleIconListener("pass")

        } else if (checkIfDigit()) {

            // if password has not a number, trigger an error
            Log.e(EMAIL_TAG, "No number in the password")
            binding.passwordInputLayout.isPasswordVisibilityToggleEnabled = false
            binding.passwordEditText.error = "Your password must have a number"

            // show toggle visibility icon listener
            addPasswordToggleIconListener("pass")

        } else if(password != binding.repeatPasswordEditText.text.toString()) {

            // if repeated password is incorrect, trigger an error
            Log.e(EMAIL_TAG, "Two different passwords entered")
            binding.repeatPasswordInputLayout.isPasswordVisibilityToggleEnabled = false
            binding.repeatPasswordEditText.error = "You must enter the same passwords"

            // show toggle visibility icon listener
            addPasswordToggleIconListener("repeat")

        } else {
            // sing up
            signupUser()
        }
    }

    /** password validation support function */
    private fun checkIfCapitalLetter(letter: String): Boolean {

        // if there is uppercase letter return false
        // if not return true
        var isDigit = true

        for (position in 0 until password.length ) {
            if (password[position].isUpperCase()){
                isDigit = false
                break
            }
        }

        return isDigit
    }

    /** password validation support function */
    private fun checkIfDigit(): Boolean {
        var noDigit = true

        // if there is digit return false
        // if not return true
        for (position in 0 until password.length ) {

            val code = password[position].code
            if (code > 47 && code < 58) {
                noDigit = false
                break
            }

        }

        return noDigit
    }

    /** password validation support function */
    private fun addPasswordToggleIconListener(editText: String){
        // show toggle visibility icon listener
        if (editText=="pass") {
            binding.passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.passwordInputLayout.isPasswordVisibilityToggleEnabled = true
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
            })
        } else {
            binding.repeatPasswordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.repeatPasswordInputLayout.isPasswordVisibilityToggleEnabled = true
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
            })
        }
    }

    /** sign up function */
    private fun signupUser(){

        progressDialog.show()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                progressDialog.dismiss()
                Log.i(EMAIL_TAG,"User created with email: ${auth.currentUser!!.email}")
                Toast.makeText(this,"New user created", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
            .addOnFailureListener { exception ->

                progressDialog.dismiss()
                Log.e(EMAIL_TAG, "Sign up failed due to: ${exception.message}")
                Toast.makeText(this, "Sign up failed", Toast.LENGTH_SHORT).show()

            }
    }

}