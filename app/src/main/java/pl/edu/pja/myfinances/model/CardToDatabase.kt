package pl.edu.pja.myfinances.model

data class CardToDatabase(
    var name: String,
    var formatName: String
) {
    constructor() : this("", "")
}