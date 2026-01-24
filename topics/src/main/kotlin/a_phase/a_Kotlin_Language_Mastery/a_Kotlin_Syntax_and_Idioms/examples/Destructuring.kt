package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates destructuring declarations, a convenient way to
 * unpack objects into a number of variables.
 */
// We'll use the data class from DataClasses.kt
// data class a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Book(val title: String, val author: String, val year: Int)

fun main() {
    println("--- Destructuring Declarations Examples ---")

    // --- Destructuring a Data Class ---
    // Any object can be destructured as long as it has `componentN()` functions.
    // Data classes provide these automatically.
    val book = Book("The Hobbit", "J.R.R. Tolkien", 1937)
    val (title, author, year) = book // This is the destructuring declaration

    println("\nDestructuring a data class:")
    println("Title: $title")
    println("Author: $author")
    println("Year: $year")

    // If you only need some of the values, you can use an underscore for the ones you don't need.
    val (titleOnly, _, _) = book
    println("\nDestructuring to get only the title: $titleOnly")


    // --- Destructuring in Loops ---
    val books = listOf(
        Book("1984", "George Orwell", 1949),
        Book("Brave New World", "Aldous Huxley", 1932)
    )

    println("\nDestructuring in a for-loop:")
    for ((bookTitle, bookAuthor, bookYear) in books) {
        println("- '$bookTitle' by $bookAuthor ($bookYear)")
    }


    // --- Destructuring Maps ---
    val bookPrices = mapOf(
        "1984" to 7.99,
        "The Hobbit" to 12.50
    )

    println("\nDestructuring a map:")
    for ((bookName, price) in bookPrices) {
        println("The price of '$bookName' is $$price")
    }

    println("----------------------------------------")
}
