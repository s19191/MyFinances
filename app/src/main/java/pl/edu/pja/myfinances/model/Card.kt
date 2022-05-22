package pl.edu.pja.myfinances.model

data class Card(
    val barCode: String,
    var name: String,
    val formatName: String
) {
    constructor() : this("", "","")
}