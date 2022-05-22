package pl.edu.pja.myfinances.model

data class Card(
    val barCode: String,
    val name: String,
    val formatName: String
) {
    constructor() : this("", "","")
}