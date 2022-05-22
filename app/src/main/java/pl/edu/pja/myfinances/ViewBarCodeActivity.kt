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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val bundle :Bundle ?=intent.extras
        if (bundle!=null){
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

        drawBarCode(card.barCode)
    }

    private fun drawBarCode(contents: String){
        multiFormatWriter = MultiFormatWriter()
        println("Width " + binding.barCodeImageView.width)
        println("Height " + binding.barCodeImageView.height)
        println("Max width " + binding.barCodeImageView.maxWidth)
        println("Max height " + binding.barCodeImageView.maxHeight)
        bitMatrix = multiFormatWriter.encode(contents, BarcodeFormat.valueOf(card.formatName), binding.barCodeImageView.maxWidth, binding.barCodeImageView.maxHeight)
        val bitmap: Bitmap = Bitmap.createBitmap(binding.barCodeImageView.maxWidth, binding.barCodeImageView.maxHeight, Bitmap.Config.RGB_565)

        for (i in 0 until binding.barCodeImageView.maxWidth) {
            for (j in 0 until binding.barCodeImageView.maxHeight) {
                bitmap.setPixel(i, j, if(bitMatrix.get(i, j)) Color.BLACK else Color.WHITE)
            }
        }

        binding.barCodeImageView.setImageBitmap(bitmap)
    }

    fun editCard(view: View) {
        startActivityForResult(Intent(this, EditCardActivity::class.java), EDIT_REQ)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == EDIT_REQ && resultCode == Activity.RESULT_OK) {

        } else super.onActivityResult(requestCode, resultCode, data)
    }
}