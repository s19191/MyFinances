package pl.edu.pja.myfinances.model

data class CardToDatabase(
    val name: String,
    var formatName: String
) {
    constructor() : this("", "")
}