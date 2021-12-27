package pl.edu.pja.myfinances.model

data class Card(
    val name: String,
    val barCode: String
)
{
    constructor() : this("", "")
}