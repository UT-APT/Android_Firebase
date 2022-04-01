package com.example.babyfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // [START auth_fui_create_launcher]
    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }
    // [END auth_fui_create_launcher]

    // [START Google_sign_in_client]
    private lateinit var googleSignInClient: GoogleSignInClient
    // [END Google_sign_in_client]

    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    // [END declare_auth]

    // [START declare_buttons]
    private lateinit var signInButton: Button
    private lateinit var signOutButton: Button
    private lateinit var googleSignInButton: Button
    // [END declare_buttons]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth
        // [END initialize_auth]

        signInButton = findViewById(R.id.signInButton)
        signOutButton = findViewById(R.id.signOutButton)
        signInButton.setOnClickListener { startSignIn() }
        signOutButton.setOnClickListener { signOut() }

        googleSignInButton = findViewById(R.id.googleSignInButton)
        googleSignInButton.setOnClickListener { googleSignIn() }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))  // IDE will complain but was able to build and launch
            .requestEmail()
            .build()

        // Toast.makeText(this, gso.toString(), Toast.LENGTH_LONG).show()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun googleSignIn() {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
            .setAvailableProviders(listOf(AuthUI.IdpConfig.GoogleBuilder().build()))
            .build()
        signInLauncher.launch(signInIntent)
    }


    private fun startSignIn() {
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
            .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
            .build()
        signInLauncher.launch(signInIntent)
    }

    // [START auth_fui_result]
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            updateUI(user)
            // ...
        } else {
            Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show()
        }
    }
    // [END auth_fui_result]

    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
        // [END auth_fui_signout]

        updateUI(null)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            signInButton.visibility = View.GONE
            signOutButton.visibility = View.VISIBLE
            Toast.makeText(this, "Signed in", Toast.LENGTH_SHORT).show()
            Log.i("updateUI", "Signed in")
        }
        else {
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
            Toast.makeText(this, "Not signed in", Toast.LENGTH_SHORT).show()
            Log.i("updateUI", "Not signed in")
        }
    }

}