package com.example.lab10
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.appcompat.resources.Compatibility.Api15Impl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    private val GOOGLE_SIGN_IN = 100

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_auth)

        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString("message", "Integracion a firebase completa")

        firebaseAnalytics.logEvent("InitScreen", bundle)

        //setup

        setup()

        //session
        session()


    }

    override fun onStart() {
        super.onStart()

        val layoutAuth : LinearLayout  = findViewById(R.id.authLayout) as LinearLayout
        layoutAuth.visibility = View.VISIBLE
    }

    private fun session () {

        val prefs : SharedPreferences = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email: String? = prefs.getString("email", null)
        val provider: String? = prefs.getString("provider", null)

        if (email != null && provider != null) {

            //
            val layoutAuth : LinearLayout  = findViewById(R.id.authLayout) as LinearLayout
            layoutAuth.visibility = View.INVISIBLE
            //

            showHome(email, ProviderType.valueOf(provider))
        }

    }

    private fun setup() {

        title = "Authentication"

        val signUpButton = findViewById(R.id.signUpButton) as Button
        val logInButton = findViewById(R.id.logInButton) as Button
        val googleButton = findViewById(R.id.googleButton) as Button


        val emailEditText = findViewById(R.id.emailEditText) as EditText
        val passwordEditText = findViewById(R.id.passwordEditText) as EditText

        // set on-click listener
        signUpButton.setOnClickListener {
            if (emailEditText.text.isNotBlank() && passwordEditText.text.isNotBlank()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString(),
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC )

                    } else {
                        showAlert(it.exception?.toString() ?: "")

                    }
                }

            }
            //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        }

        // set on-click listener
        logInButton.setOnClickListener {
            if (emailEditText.text.isNotBlank() && passwordEditText.text.isNotBlank()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString(),
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC )

                    } else {
                        showAlert(it.exception?.toString() ?: "")
                    }
                }

            }
            //Toast.makeText(this@MainActivity, "You clicked me.", Toast.LENGTH_SHORT).show()
        }

        googleButton.setOnClickListener {

            //config

            val googleConf  = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("526193774636-dq9u0u99d2b5tmr25bo3lqc37k8k4m4o.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleClient : GoogleSignInClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN )

        }

    }

    private fun showAlert (error: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Authentication Error with the User $error")
        builder.setPositiveButton("Accept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome (email: String, provider: ProviderType) {

        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account : GoogleSignInAccount? = task.getResult(ApiException::class.java)


                if (account != null && account.idToken != null) {

                    val credential:AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(account.email.toString() ?: "", ProviderType.GOOGLE )

                        } else {
                            showAlert(it.exception?.toString() ?: "")
                        }
                    }



                } else {
                    if (account != null) {
                        showAlert("${account.id} ${account.idToken} ERROR ID TOKEN")
                    } else {
                        showAlert(" ERROR ID TOKEN NULL ESTAMOS FRITOS")

                    }
                }
            } catch ( e: ApiException) {
                showAlert("CAI EN EL PRIMER CATCHH EL DE HASTA ARRIBA ${e.toString()}")
            }



        }
    }
}
