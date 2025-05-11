package com.example.findit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 1001
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // from google-services.json
            .requestEmail()
            .requestProfile() // Request user profile data including name and profile picture
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInBtn = findViewById<FrameLayout>(R.id.bt_sign_in)
        signInBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val email = account.email ?: ""

                if (email.endsWith("@vitstudent.ac.in")) {
                    firebaseAuthWithGoogle(account.idToken!!, account)
                } else {
                    Toast.makeText(this, "Only @vitstudent.ac.in emails are allowed", Toast.LENGTH_SHORT).show()
                    googleSignInClient.signOut()
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Get user information
                    val fullName = account.displayName ?: ""
                    val firstName = fullName.split(" ")[0] // Get first name (text before first space)
                    val profilePicUrl = account.photoUrl?.toString() ?: ""

                    // Also extract the full name without registration number
                    val fullNameWithoutReg = fullName.split("\\d".toRegex())[0].trim()

                    // Create intent and pass user data
                    val intent = Intent(this, HomeScreen::class.java).apply {
                        putExtra("firstName", firstName)
                        putExtra("profilePicUrl", profilePicUrl)
                        putExtra("fullNameWithoutReg", fullNameWithoutReg)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}