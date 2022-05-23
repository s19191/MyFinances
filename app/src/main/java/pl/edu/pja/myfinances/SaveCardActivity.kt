package pl.edu.pja.myfinances

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import pl.edu.pja.myfinances.databinding.ActivitySaveCardBinding
import pl.edu.pja.myfinances.model.CardToDatabase

class SaveCardActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySaveCardBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth
    lateinit var cardToDatabase: CardToDatabase

    private val options = ScanOptions()

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) barcodeLauncher.launch(options)
        }

    private val barcodeLauncher =
        registerForActivityResult(
            ScanContract()
        ) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(
                    this,
                    "Cancelled",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Scanned: " + result.contents,
                    Toast.LENGTH_LONG
                ).show()

                cardToDatabase.formatName = result.formatName

                FirebaseDatabase
                    .getInstance()
                    .getReference(auth.uid!!)
                    .child("cards")
                    .child(result.contents)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if(dataSnapshot.exists()) {
                                Toast.makeText(
                                    this@SaveCardActivity,
                                    "Karta z tym kodem kreskowym jest już dodana!",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                FirebaseDatabase.getInstance()
                                    .getReference(auth.uid!!)
                                    .child("cards")
                                    .child(result.contents)
                                    .setValue(
                                        cardToDatabase
                                    )
                                    .addOnCompleteListener {
                                        finish()
                                    }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                this@SaveCardActivity,
                                "Error: ${error.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.w(TAG, "Failed to read value.", error.toException())
                        }
                    })
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LogInActivity::class.java))
        }
    }

    fun openScanner(view: View) {
        val cardName = binding.cardNameEditText.text.toString()

        if (cardName.isNotEmpty() && cardName.length < 21) {
            cardToDatabase = CardToDatabase(
                cardName,
                ""
            )

            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            Toast.makeText(this, "Wprowadź nazwę karty!", Toast.LENGTH_SHORT).show()
        }
    }
}