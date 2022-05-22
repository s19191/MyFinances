package pl.edu.pja.myfinances.model

data class CardToDatabase(
    val name: String,
    val formatName: String
) {
    constructor() : this("", "")
}