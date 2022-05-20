package pl.edu.pja.myfinances.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import pl.edu.pja.myfinances.databinding.ItemCardBinding
import pl.edu.pja.myfinances.model.Card

class CardsAdapter : RecyclerView.Adapter<CardItem>() {
    var cards: MutableList<Card> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItem {
        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CardItem(binding)
            .also { holder ->
                auth = FirebaseAuth.getInstance()
                binding.root.setOnClickListener {

                }
                binding.root.setOnLongClickListener {
                    removeCard(holder.layoutPosition, parent)
                    return@setOnLongClickListener true
                }
            }
    }

    override fun onBindViewHolder(holder: CardItem, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    private fun removeCard(position: Int, parent: ViewGroup) {
        val builder = AlertDialog.Builder(parent.context)
        builder.setMessage("Czy na pewno chcesz usunać kartę?")
            .setCancelable(false)
            .setPositiveButton("Tak") { _, _ ->
                FirebaseDatabase
                    .getInstance()
                    .getReference(auth.uid!!)
                    .child("cards")
                    .child(cards[position].barCode)
                    .removeValue()
                    .addOnCompleteListener {
                        notifyDataSetChanged()
                        Toast.makeText(
                            parent.context,
                            "Usunąłeś kartę",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            parent.context,
                            "Error: ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .setNegativeButton("Nie") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}