package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates how control flow structures like `if` and `when`
 * can be used as expressions in Kotlin.
 */
fun main() {
    println("--- Control Flow as Expressions ---")

    // --- If as an Expression ---
    // In Kotlin, `if` is an expression, meaning it returns a value.
    // There is no ternary operator (`condition ? a : b`) in Kotlin because `if` handles it.
    val a = 10
    val b = 20
    val max = if (a > b) {
        println("Choosing a")
        a // The last line of the block is the return value
    } else {
        println("Choosing b")
        b
    }
    println("The maximum value is $max")

    val simpleMax = if (a > b) a else b
    println("The same logic, simplified: $simpleMax")


    // --- When as an Expression ---
    // `when` is a super-powered `switch` statement that can also be used as an expression.
    println("\n--- The 'when' Expression ---")
    val value: Any = 100L

    val description = when (value) {
        1 -> "One"
        "Hello" -> "A greeting"
        is Long -> "A Long number"
        !is String -> "Not a string"
        else -> "Unknown"
    }
    println("Description of value: '$description'")

    // `when` can also check ranges
    val temperature = 32
    val state = when (temperature) {
        in -50..0 -> "Solid"
        in 1..99 -> "Liquid"
        in 100..200 -> "Gas"
        else -> "Plasma?"
    }
    println("At $temperature degrees, water is in a '$state' state.")

    // `when` without an argument can be used as a more readable if-else-if chain.
    val name = "Guest"
    val greeting = when {
        name.startsWith("Admin") -> "Welcome, Administrator!"
        name == "Guest" -> "Welcome, Guest. Please sign in."
        else -> "Hello, $name."
    }
    println(greeting)

    println("----------------------------------")
}
