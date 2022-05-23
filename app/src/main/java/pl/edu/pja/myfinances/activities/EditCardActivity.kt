package pl.edu.pja.myfinances.activities

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import pl.edu.pja.myfinances.databinding.ActivityEditCardBinding
import pl.edu.pja.myfinances.model.Card
import pl.edu.pja.myfinances.model.CardToDatabase

class EditCardActivity : AppCompatActivity() {
    private  val binding by lazy { ActivityEditCardBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth
    private lateinit var card: Card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        val bundle :Bundle ?= intent.extras
        if (bundle != null) {
            val barCode = bundle.getString("barCode")
            val name = bundle.getString("name")
            val formatName = bundle.getString("formatName")
            if (barCode != null && name != null && formatName != null) {
                card = Card(
                    barCode,
                    name,
                    formatName
                )
            }
            binding.newCardNameEditText.setText(card.name)
        }
    }

    fun saveChanges(view: View) {
        val newCardName = binding.newCardNameEditText.text.toString()
        if (newCardName.isNotEmpty() && newCardName.length < 21 && newCardName != card.name) {
            FirebaseDatabase.getInstance()
                .getReference(auth.uid!!)
                .child("cards")
                .child(card.barCode)
                .setValue(
                    CardToDatabase(
                    newCardName,
                    card.formatName
                    )
                )
                .addOnCompleteListener {
                    val resultIntent = Intent()
                        .putExtra("newName", newCardName)
                    setResult(
                        Activity.RESULT_OK,
                        resultIntent
                    )
                    finish()
                }
                .addOnFailureListener {
                    Log.w(ContentValues.TAG, "editingCard EditCardActivity: ", it.fillInStackTrace())
                }
        }
    }
}