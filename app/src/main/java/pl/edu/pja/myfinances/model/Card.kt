package pl.edu.pja.myfinances.model

data class Card(
    val name: String,
    var barCode: String
)
{
    constructor() : this("", "")
}