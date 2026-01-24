package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates the difference between structural and referential equality in Kotlin.
 */

// A regular class. By default, `equals()` checks for referential equality (like `===`).
class RegularPerson(val name: String)

// A data class. The compiler automatically generates `equals()` based on the constructor properties.
data class DataPerson(val name: String)

fun main() {
    println("--- Equality Examples (== vs ===) ---")

    println("\n--- Comparing regular classes ---")
    val person1 = RegularPerson("Mario")
    val person2 = RegularPerson("Mario")
    val person3 = person1

    // Structural Equality (`==`)
    // For regular classes, this is the same as `===` unless `equals()` is overridden.
    println("person1 == person2: ${person1 == person2}") // false

    // Referential Equality (`===`)
    // Checks if two references point to the exact same object in memory.
    println("person1 === person2: ${person1 === person2}") // false
    println("person1 === person3: ${person1 === person3}") // true, because person3 points to person1


    println("\n--- Comparing data classes ---")
    val dataPerson1 = DataPerson("Carlo")
    val dataPerson2 = DataPerson("Carlo")
    val dataPerson3 = dataPerson1

    // Structural Equality (`==`)
    // For data classes, this compares the values of the properties.
    println("dataPerson1 == dataPerson2: ${dataPerson1 == dataPerson2}") // true

    // Referential Equality (`===`)
    println("dataPerson1 === dataPerson2: ${dataPerson1 === dataPerson2}") // false
    println("dataPerson1 === dataPerson3: ${dataPerson1 === dataPerson3}") // true

    println("------------------------------------")
}

