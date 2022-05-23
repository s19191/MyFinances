package pl.edu.pja.myfinances

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import pl.edu.pja.myfinances.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setResult(RESULT_CANCELED)

        auth = FirebaseAuth.getInstance()
    }

    fun register(view: View) {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Wprowadź adres email oraz hasło!", Toast.LENGTH_SHORT).show()
        } else if (email.isEmpty()) {
            Toast.makeText(
                this,
                "Wprowadź adres email!",
                Toast.LENGTH_LONG
            ).show()
        } else if (password.isEmpty()) {
            Toast.makeText(
                this,
                "Wprowadź hasło!",
                Toast.LENGTH_LONG
            ).show()
        } else if (password.length < 6) {
            Toast.makeText(
                this,
                "Hasło musi mieć minimum 6 znaków!",
                Toast.LENGTH_LONG
            ).show()
        } else {
            auth.createUserWithEmailAndPassword(
                email,
                password
            ).addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Zarejestrowano!",
                    Toast.LENGTH_SHORT
                ).show()
                auth.signInWithEmailAndPassword(
                    email,
                    password
                ).addOnSuccessListener {
                    setResult(RESULT_OK)
                    finish()
                }.addOnFailureListener{
                    Log.w(ContentValues.TAG, "logIn RegisterActivity: ", it.fillInStackTrace())
                }
            }.addOnFailureListener {
                Log.w(ContentValues.TAG, "register RegisterActivity: ", it.fillInStackTrace())
            }
        }
    }
}