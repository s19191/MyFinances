package pl.edu.pja.myfinances

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import pl.edu.pja.myfinances.databinding.ActivityLogInBinding

const val REGISTER_REQ = 1
const val REGISTER_VIA_GOOGLE_REQ = 2

class LogInActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLogInBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            finish()
        }

        binding.logInViaGoogleButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, REGISTER_VIA_GOOGLE_REQ)
        }
    }

    fun logIn(view: View) {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        if (auth.currentUser == null) {
            if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Wprowadź adres email oraz hasło!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (email.isEmpty()) {
                Toast.makeText(
                    this,
                    "Wprowadź adres email!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Wprowadź hasło!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Hasło musi mieć minimum 6 znaków!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                auth.signInWithEmailAndPassword(
                    email,
                    password
                ).addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Zalogowano ${it.user?.uid} ${it.user?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Nieprawidłowe dane logowania!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                this,
                "Zalogowano ${auth.currentUser?.uid} ${auth.currentUser?.email}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun register(view: View) {
        startActivityForResult(Intent(this, RegisterActivity::class.java), REGISTER_REQ)
    }

    // Nie wiadomo czemu nie działa
    fun logInViaGoogle(view: View) {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, REGISTER_VIA_GOOGLE_REQ)
    }

    //Brakuje klucza SSH
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Zalogowano ${it.user?.uid} ${it.user?.email}",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(
                    this,
                    "Nieprawidłowe dane logowania!",
                    Toast.LENGTH_SHORT
                ).show()
            }
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    val user: FirebaseUser? = auth.currentUser
//                } else {
//                }
//            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REGISTER_REQ && resultCode == Activity.RESULT_OK) {
            finish()
        } else super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REGISTER_VIA_GOOGLE_REQ) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(
                    this,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}