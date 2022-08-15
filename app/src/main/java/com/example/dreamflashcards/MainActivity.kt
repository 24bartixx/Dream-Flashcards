package com.example.dreamflashcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.dreamflashcards.databinding.ActivityMainBinding
import com.example.dreamflashcards.fragments.SetsFragment
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    // view binding
    private lateinit var binding: ActivityMainBinding

    // Firebase auth
    private lateinit var auth: FirebaseAuth

    // navController
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        auth = FirebaseAuth.getInstance()

        // reference to NavHostFragment in main activity
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // set up navController with BottomNavigationView
        binding.bottomNavigationView.setupWithNavController(navController)

        // ensures that action bar buttons are visible
        setupActionBarWithNavController(navController)

    }

    /** Options menu inflate function */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        return true
    }

    /** if action bar pressed function */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.sign_out_option -> {

                auth.signOut()
                LoginManager.getInstance().logOut()

                Log.i("MainActivity", "Signed out")
                Toast.makeText(this, "Signed out", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, LoginActivity::class.java))
                finish()

            }
        }

        return super.onOptionsItemSelected(item)
    }

    // handle the up button functionality
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}