package pl.edu.pja.myfinances

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
    lateinit var nameTmp: String

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
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

            result.formatName

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
                                .setValue(CardToDatabase(
                                    nameTmp,
                                    result.formatName
                                ))
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
//                        Log.w(TAG, "Failed to read value.", error.toException())
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

        if (cardName.isNotEmpty()) {
            nameTmp = cardName

            val options = ScanOptions()
//        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
//        options.setPrompt("Scan a barcode")
//        options.setCameraId(0) // Use a specific camera of the device
//
//        options.setBeepEnabled(false)
//        options.setBarcodeImageEnabled(true)
            barcodeLauncher.launch(options)
        } else {
            Toast.makeText(this, "Wprowadź nazwę karty!", Toast.LENGTH_SHORT).show()
        }
    }
}