package com.example.findit

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.findit.objects.RetrofitInstance
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var loaderOverlay: FrameLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var signInBtn: FrameLayout

    companion object {
        private const val RC_SIGN_IN = 1001
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize RetrofitInstance
        //initializeRetrofit()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // from google-services.json
            .requestEmail()
            .setHostedDomain("vitstudent.ac.in")
            .requestProfile() // Request user profile data including name and profile picture
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInBtn = findViewById<FrameLayout>(R.id.bt_sign_in)
        /*googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }*/
        loaderOverlay = findViewById(R.id.loader_overlay)
        progressBar = findViewById(R.id.progressBar)



        signInBtn.setOnClickListener(){
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        }
    }

    private fun initializeRetrofit() {
        // Simply referencing RetrofitInstance will initialize it
        Log.d(TAG, "RetrofitInstance initialized: ${RetrofitInstance.javaClass.name}")
        lifecycleScope.launch {
            try {

                // Make the API call
                val response = RetrofitInstance.publicUserApi.serverStatus()
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Backend Connected ✅")
                    Toast.makeText(
                        this@MainActivity,
                        "Backend Connected ✅",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Log.w(TAG, "Backend Not Connected ❌ : ${response.code()}")
                    // Stay on login screen - backend login failed
                    Toast.makeText(
                        this@MainActivity,
                        "Backend Not Connected ❌",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Backend Not Connected ❌", e)
                Toast.makeText(
                    this@MainActivity,
                    "Backend Not Connected ❌ : ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // User is already signed in, proceed to HomeScreen
            attemptBackendLogin()
        }
    }

    private fun attemptBackendLogin() {
        progressBar.visibility = View.VISIBLE
        loaderOverlay.visibility = View.VISIBLE
        signInBtn.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.authUserApi.loginUser()


                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Backend login successful")

                    // ✅ Hide loader before navigation
                    progressBar.visibility = View.GONE
                    loaderOverlay.visibility = View.GONE

                    val intent = Intent(this@MainActivity, HomeScreen::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    intent.putExtra("name", response.body()?.user?.name)
                    startActivity(intent)
                    finish()
                } else {
                    progressBar.visibility = View.GONE
                    loaderOverlay.visibility = View.GONE
                    Toast.makeText(
                        this@MainActivity,
                        "Login failed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    auth.signOut()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                loaderOverlay.visibility = View.GONE
                Log.e(TAG, "Error during backend login", e)
                Toast.makeText(
                    this@MainActivity,
                    "Login error: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
                auth.signOut()
            }finally {
                signInBtn.isEnabled = true
            }
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
                    // Show loader immediately after email selection
                    progressBar.visibility = View.VISIBLE
                    loaderOverlay.visibility = View.VISIBLE

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
                    // Firebase authentication successful, now try backend login
                    val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    sharedPref.edit().putString("profilePicUrl", account.photoUrl?.toString() ?: "").apply()
                    sharedPref.edit().putString("userName", account.displayName ?: "").apply()

                    loaderOverlay.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    attemptBackendLogin()
                }else {
                    progressBar.visibility = View.GONE
                    loaderOverlay.visibility = View.GONE
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}