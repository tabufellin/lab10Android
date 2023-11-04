package com.example.lab10
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FilterQueryProvider
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.lab10.ui.theme.Lab10Theme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

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


    }

    private fun setup() {

        title = "Authentication"

        val signUpButton = findViewById(R.id.signUpButton) as Button
        val logInButton = findViewById(R.id.logInButton) as Button

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
}
