package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates data classes, which are a concise way to create classes
 * that just hold data.
 */

// A data class automatically generates useful methods based on its primary constructor properties:
// - equals(): for structural equality
// - hashCode(): consistent with equals()
// - toString(): a readable representation of the object
// - copy(): to create a new instance with modified properties
// - componentN(): functions used for destructuring declarations
data class Book(val title: String, val author: String, val year: Int)

fun main() {
    println("--- Data Classes Examples ---")

    val book1 = Book("The Lord of the Rings", "J.R.R. Tolkien", 1954)

    // 1. toString() - provides a readable output
    println("toString(): $book1")

    // 2. equals() - compares property values
    val book2 = Book("The Lord of the Rings", "J.R.R. Tolkien", 1954)
    println("equals(): book1 == book2 is ${book1 == book2}") // true

    // 3. copy() - create a new object with some properties changed
    val updatedBook = book1.copy(year = 2012) // Creates a new a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Book instance
    println("copy(): $updatedBook")
    println("Original book is unchanged: $book1")

    // 4. componentN() - used for destructuring (see Destructuring.kt for more)
    val title = book1.component1()
    val author = book1.component2()
    println("componentN(): Title is '$title' and author is '$author'")

    println("--------------------------")
}
