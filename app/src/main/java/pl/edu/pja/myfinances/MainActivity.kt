package pl.edu.pja.myfinances

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import pl.edu.pja.myfinances.adapter.CardsAdapter
import pl.edu.pja.myfinances.databinding.ActivityMainBinding
import pl.edu.pja.myfinances.model.Card

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var auth: FirebaseAuth
    private val cardsAdapter by lazy { CardsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LogInActivity::class.java))
        }

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupNewsList()
    }

    private fun setupNewsList() {
        binding.cardsListRecyclerView.apply {
            adapter = cardsAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onResume() {
        super.onResume()
        executeCall()
    }

    private fun executeCall() {
        FirebaseDatabase
            .getInstance()
            .getReference(auth.uid!!)
            .child("cards")
            .addValueEventListener(object : ValueEventListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val genericTypeIndicator : GenericTypeIndicator<Map<String, String>> =
                        object : GenericTypeIndicator<Map<String, String>>() {}
                    val value = dataSnapshot.getValue(genericTypeIndicator)
                    var cards: MutableList<Card> = mutableListOf()
                    value?.forEach {
                        cards.add(
                            Card(
                                it.key,
                                it.value
                            )
                        )
                    }
                    cards.sortBy {
                        it.name
                    }
                    cardsAdapter.cards = cards
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun scanBarCode(view: View) {
        startActivity(Intent(this, SaveCardActivity::class.java))
    }

    fun signOut(view: View) {
        auth.signOut()
        startActivity(Intent(this, LogInActivity::class.java))
    }
}