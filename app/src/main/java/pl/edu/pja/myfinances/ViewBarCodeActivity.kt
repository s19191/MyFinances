package pl.edu.pja.myfinances

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.edu.pja.myfinances.databinding.ActivityViewBarCodeBinding

class ViewBarCodeActivity : AppCompatActivity() {
    private val binding by lazy { ActivityViewBarCodeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}