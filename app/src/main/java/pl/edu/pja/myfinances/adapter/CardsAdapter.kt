package pl.edu.pja.myfinances.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.edu.pja.myfinances.databinding.ItemCardBinding
import pl.edu.pja.myfinances.model.Card

class CardsAdapter : RecyclerView.Adapter<CardItem>() {
    var cards: List<Card> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItem {
        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CardItem(binding)
    }

    override fun onBindViewHolder(holder: CardItem, position: Int) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size
}