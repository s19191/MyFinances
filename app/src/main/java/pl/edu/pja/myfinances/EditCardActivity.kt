package pl.edu.pja.myfinances

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pl.edu.pja.myfinances.databinding.ActivityEditCardBinding

class EditCardActivity : AppCompatActivity() {
    private  val binding by lazy { ActivityEditCardBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun saveChanges(view: View) {
        val newCardName = binding.newCardNameEditText.text.toString()
        if (newCardName.isNotEmpty()) {

        }
    }
}