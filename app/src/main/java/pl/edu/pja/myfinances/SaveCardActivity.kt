package pl.edu.pja.myfinances

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import pl.edu.pja.myfinances.databinding.ActivitySaveCardBinding
import pl.edu.pja.myfinances.model.Card

class SaveCardActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySaveCardBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth
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
            card.barCode = result.contents
            FirebaseDatabase.getInstance()
                .getReference(auth.uid!!)
                .child("cards")
                .push()
                .setValue(
                    card
                ).addOnCompleteListener {
                    finish()
                }
        }
    }

    lateinit var card: Card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LogInActivity::class.java))
        }
    }

    fun openScanner(view: View) {
        val cardName = binding.cardName.text.toString()

        if (cardName.isNotEmpty()) {
            card = Card(
                cardName,
                ""
            )

            val options = ScanOptions()
//        options.setDesiredBarcodeFormats(ScanOptions.ONE_D_CODE_TYPES)
//        options.setPrompt("Scan a barcode")
//        options.setCameraId(0) // Use a specific camera of the device
//
//        options.setBeepEnabled(false)
//        options.setBarcodeImageEnabled(true)
            val aaa = barcodeLauncher.launch(options)
        } else {
            Toast.makeText(this, "Wprowadź nazwę karty!", Toast.LENGTH_SHORT).show()
        }
    }
}