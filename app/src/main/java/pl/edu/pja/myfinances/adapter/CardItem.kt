package pl.edu.pja.myfinances.adapter

import androidx.recyclerview.widget.RecyclerView
import pl.edu.pja.myfinances.databinding.ItemCardBinding
import pl.edu.pja.myfinances.model.Card

class CardItem(private val binding: ItemCardBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(itemCard: Card) {
        binding.apply {
            cardNameTextView.text = itemCard.name
            barCodeTextView.text = itemCard.barCode
        }
    }
}