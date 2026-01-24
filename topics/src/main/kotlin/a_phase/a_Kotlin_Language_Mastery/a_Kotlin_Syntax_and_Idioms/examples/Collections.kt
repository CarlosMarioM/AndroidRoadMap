package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates the basics of working with collections and using
 * higher-order functions for a functional programming style.
 */
fun main() {
    println("--- Collections and Functional Style ---")

    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    println("Original list: $numbers")

    // --- Higher-Order Functions ---
    // These are functions that take other functions (lambdas) as parameters.

    // .filter { } - returns a new list containing only elements that match the predicate.
    val evenNumbers = numbers.filter { it % 2 == 0 } // `it` is the implicit name for a single parameter
    println("\nfilter (even numbers): $evenNumbers")

    // .map { } - returns a new list with the results of applying the transform to each element.
    val squaredNumbers = numbers.map { number -> number * number } // Explicitly naming the parameter
    println("map (squared numbers): $squaredNumbers")

    // .forEach { } - performs the given action on each element.
    print("forEach (printing numbers): ")
    numbers.forEach { print("$it ") }
    println()

    // .find { } or .firstOrNull { } - returns the first element matching the predicate, or null.
    val firstGreaterThanFive = numbers.find { it > 5 }
    println("find (first > 5): $firstGreaterThanFive")

    // .any { } - returns true if at least one element matches the predicate.
    val hasNumberThree = numbers.any { it == 3 }
    println("any (has 3?): $hasNumberThree")

    // .all { } - returns true if all elements match the predicate.
    val allPositive = numbers.all { it > 0 }
    println("all (are > 0?): $allPositive")

    // --- Chaining Operations ---
    // The real power comes from chaining these operations together.
    val result = numbers
        .filter { it % 2 != 0 } // Keep odd numbers: [1, 3, 5, 7, 9]
        .map { it * 10 }        // Multiply by 10: [10, 30, 50, 70, 90]
        .filter { it > 40 }     // Keep numbers greater than 40: [50, 70, 90]
        .joinToString(", ") // Create a string from the result

    println("\nChained result (odd numbers * 10, > 40): $result")

    println("---------------------------------------")
}
