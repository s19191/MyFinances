package pl.edu.pja.myfinances

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import pl.edu.pja.myfinances.databinding.ActivityViewBarCodeBinding
import pl.edu.pja.myfinances.model.Card

const val EDIT_REQ = 3

class ViewBarCodeActivity : AppCompatActivity() {
    private val binding by lazy { ActivityViewBarCodeBinding.inflate(layoutInflater) }
    private lateinit var multiFormatWriter: MultiFormatWriter
    private lateinit var bitMatrix: BitMatrix
    private lateinit var card: Card
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
        }
        drawBarCode()
        setDetails()
    }

    private fun drawBarCode(){
        multiFormatWriter = MultiFormatWriter()
        bitMatrix = multiFormatWriter.encode(card.barCode, BarcodeFormat.valueOf(card.formatName), binding.barCodeImageView.maxWidth, binding.barCodeImageView.maxHeight)
        bitmap = Bitmap.createBitmap(binding.barCodeImageView.maxWidth, binding.barCodeImageView.maxHeight, Bitmap.Config.RGB_565)

        for (i in 0 until binding.barCodeImageView.maxWidth) {
            for (j in 0 until binding.barCodeImageView.maxHeight) {
                bitmap.setPixel(i, j, if(bitMatrix.get(i, j)) Color.BLACK else Color.WHITE)
            }
        }

        binding.barCodeImageView.setImageBitmap(bitmap)
    }

    private fun setDetails() {
        binding.cardNameDetailsTextView.text = card.name
        binding.barCodeDetailsTextView.text = card.barCode
    }

    fun editCard(view: View) {
        startActivityForResult(
            Intent(this, EditCardActivity::class.java)
                .putExtra("barCode", card.barCode)
                .putExtra("name", card.name)
                .putExtra("formatName", card.formatName),
            EDIT_REQ
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EDIT_REQ && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val newName = data.getStringExtra("newName")
                newName.let {
                    binding.cardNameDetailsTextView.text = newName
                    card.name = newName!!
                }
            }
        } else super.onActivityResult(requestCode, resultCode, data)
    }
}