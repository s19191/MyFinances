package pl.edu.pja.myfinances

import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import pl.edu.pja.myfinances.databinding.ActivityLogInBinding

class LogInActivity : AppCompatActivity() {
    private val binding by lazy { ActivityLogInBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val registerResultLauncher = registerForActivityResult(
        ActivityResultContracts
            .StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            finish()
        }
    }

    private val registerViaGoogleResultLauncher = registerForActivityResult(
        ActivityResultContracts
            .StartActivityForResult())
    { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let {
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.w(TAG, "logInViaGoogle LogInActivity: ", e.fillInStackTrace())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            finish()
        }

        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.logInViaGoogleButton.setOnClickListener {
            registerViaGoogleResultLauncher
                .launch(
                    googleSignInClient.signInIntent
                )
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener {
                Log.w(TAG, "logInViaGoogle signInWithCredential LogInActivity: failure", it.fillInStackTrace())
            }
    }

    fun logIn(view: View) {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        if (auth.currentUser == null) {
            if (email.isEmpty() && password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Wprowad?? adres email oraz has??o!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (email.isEmpty()) {
                Toast.makeText(
                    this,
                    "Wprowad?? adres email!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Wprowad?? has??o!",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Has??o musi mie?? minimum 6 znak??w!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                auth.signInWithEmailAndPassword(
                    email,
                    password
                ).addOnSuccessListener {
                    finish()
                }.addOnFailureListener {
                    Log.w(ContentValues.TAG, "logIn LogInActivity: ", it.fillInStackTrace())
                }
            }
        }
    }

    fun register(view: View) {
        registerResultLauncher.launch(
            Intent(this, RegisterActivity::class.java)
        )
    }
}