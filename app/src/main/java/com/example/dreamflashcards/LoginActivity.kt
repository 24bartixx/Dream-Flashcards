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
import com.example.dreamflashcards.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    // view binding
    private lateinit var binding: ActivityLoginBinding

    // FirebaseAuth
    private lateinit var auth: FirebaseAuth

    private var email = ""
    private var password = ""

    companion object{
        private const val EMAIL_TAG = "EmailAuthorization: "
        private const val GOOGLE_TAG = "GoogleAuthorization: "
        private const val FACEBOOK_TAG = "FacebookAuthorization"
        private const val GOOGLE_SIGN_IN = 100
    }

    /** Password login variables **/
    // Progress Dialog
    private lateinit var progressDialog: ProgressDialog

    /** Google login variables */
    // client for interacting with GoogleSignIn API
    private lateinit var googleSignInClient: GoogleSignInClient

    /** Facebook login variables */
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // prepare a ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Waitnig")
        progressDialog.setMessage("Logging in...")
        progressDialog.setCanceledOnTouchOutside(false)

        // check if the user is already logged in
        checkIfLogged()

        /** Go to sign up screen */
        binding.notHaveAccountTextLabel.setOnClickListener {
            Log.d("LoginActivity", "Not have account")
            startActivity(Intent(this, SignupActivity::class.java))
        }

        /** login an password authorization */
        // handle login button click
        binding.loginButton.setOnClickListener {
            validateData()
        }

        /** Google authorization */
        // GoogleSignInOptions contains options to configure GoogleSignIn API
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // client for interacting with GoogleSignIn API
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        // handle GoogleSignIn button click
        binding.googleSignInButton.setOnClickListener {

            Log.d(GOOGLE_TAG, "begin GoogleSignIn")
            // intent required to start GoogleSignIn flow
            val intent = googleSignInClient.signInIntent
            startActivityForResult(intent, GOOGLE_SIGN_IN)

        }

        /** Facebook authorization */
        // CallbackManager manages the callbacks into FacebookSDK from onActivityResult()
        callbackManager = CallbackManager.Factory.create()

        binding.facebookButton.setReadPermissions("email", "public_profile")

        binding.facebookButton.setOnClickListener {
            facebookLogin()
        }

    }

    /** check if logged */
    private fun checkIfLogged(){
        if(auth.currentUser != null){
            // if authorization has a user, go to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    /** validate data for email authorization function */
    private fun validateData(){

        email = binding.emailEditText.text.toString().trim()
        password = binding.passwordEditText.text.toString().trim()

        if(email == "" || email.isNullOrEmpty()){

            // if empty email, trigger an error
            Log.e(EMAIL_TAG, "Empty email during signing in")
            binding.emailEditText.error = "Please enter your email"

        } else if(password == "" || password.isNullOrEmpty()){

            // if empty password, trigger an error
            Log.e(EMAIL_TAG, "Empty password during signing in")
            binding.passwordInputLayout.isPasswordVisibilityToggleEnabled = false
            binding.passwordEditText.error = "Please enter your password"

            binding.passwordEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.passwordInputLayout.isPasswordVisibilityToggleEnabled = true
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?) {}
            })

        } else {
            emailPasswordLogin()
        }

    }

    /** email and password authorization function */
    private fun emailPasswordLogin() {

        // show ProgressDialog
        progressDialog.show()

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                // hide ProgressDialog
                progressDialog.dismiss()
                Log.i(EMAIL_TAG, "User logged with email: ${auth.currentUser!!.email}")
                Toast.makeText(this, "Logged with email: ${auth.currentUser!!.email}", Toast.LENGTH_SHORT).show()

                // go to the app
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
            .addOnFailureListener { e ->

                // hide ProgressDialog
                progressDialog.dismiss()
                Log.e(EMAIL_TAG, "Authorization failed due to: ${e.message}")
                Toast.makeText(this, "Wrong email or password", Toast.LENGTH_LONG).show()

            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){

            /** Google */
            Log.d(GOOGLE_TAG, "Google SignIn intent result")
            val accountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google sign in success, now auth with firebase
                val account = accountTask.getResult(ApiException::class.java)
                authWithGoogleAccount(account)
            } catch(e: Exception) {
                // failed Google SignIn
                Log.d(GOOGLE_TAG, "Error: ${e.message}")
            }

        } else {

            /** Facebook */
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)

        }

    }

    /** Google authorization function */
    private fun authWithGoogleAccount(account: GoogleSignInAccount){

        Log.d(GOOGLE_TAG, "Begin firebase auth with Google account")

        // get credential
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        // sign in with Google
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->

                Log.i(GOOGLE_TAG, "Authorization succeeded")
                Log.i(GOOGLE_TAG, "Logged with email: ${auth.currentUser!!.email}")

                if(authResult.additionalUserInfo!!.isNewUser){
                    Log.i(GOOGLE_TAG, "Account created...")
                    Toast.makeText(this, "Account created with email:\n${auth.currentUser!!.email}", Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(GOOGLE_TAG, "Logged in...")
                    Toast.makeText(this, "Signed in with email:\n${auth.currentUser!!.email}", Toast.LENGTH_SHORT).show()
                }

                // start app activity
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
            .addOnFailureListener { e ->

                Log.e(GOOGLE_TAG, "Authorization failed due to ${e.message}")
                Toast.makeText(this, "Google authorization failed", Toast.LENGTH_SHORT).show()

            }

    }

    /** Facebook authorization function */
    private fun facebookLogin(){

        Log.i(FACEBOOK_TAG, "begin Facebook Authorization")

        binding.facebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult>{

            override fun onSuccess(loginResult: LoginResult) {

                Log.d(FACEBOOK_TAG, "Facebook Authorization succeeded, handleFacebookAccessToken() function called")
                handleFacebookAccessToken(loginResult.accessToken)

            }

            override fun onCancel() {

                Log.w(FACEBOOK_TAG, "Facebook Authorization cancelled")
                Toast.makeText(applicationContext, "Facebook Authorization cancelled", Toast.LENGTH_SHORT).show()

            }

            override fun onError(error: FacebookException) {

                Log.e(FACEBOOK_TAG, "Facebook Authorization failed due to: ${error.message}")
                // idk if it works with applicationContext
                Toast.makeText(applicationContext, "Facebook Authorization failed", Toast.LENGTH_SHORT).show()

            }

        })

    }

    /** Facebook Authorization function */
    private fun handleFacebookAccessToken(accessToken: AccessToken){

        Log.d(FACEBOOK_TAG, "handleFacebookAccessToken: ${accessToken}")

        val credential = FacebookAuthProvider.getCredential(accessToken.token)

        auth.signInWithCredential(credential)
            .addOnSuccessListener {

                Log.i(FACEBOOK_TAG, "signInWithCredential succeeded with email ${auth.currentUser!!.email}")
                Toast.makeText(this, "Facebook Authorization succeeded", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()

            }
            .addOnFailureListener { e ->

                Log.e(FACEBOOK_TAG, "signInWithCredential failed due to ${e.message}")
                Toast.makeText(this, "Facebook Authorization failed", Toast.LENGTH_SHORT).show()

            }

    }

}