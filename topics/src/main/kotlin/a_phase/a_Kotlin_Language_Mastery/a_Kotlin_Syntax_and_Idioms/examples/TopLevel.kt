package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates top-level declarations. In Kotlin, you don't need to
 * create a class to hold functions, properties, or constants. You can declare them
 * at the top level of a file.
 *
 * This is the idiomatic way to handle stateless utility functions and constants.
 */

// A top-level constant. `const` makes it a compile-time constant.
const val APP_VERSION = "1.0.0"

// A top-level property.
var requestCount = 0

// A top-level function.
fun isValidEmail(email: String): Boolean {
    // A very basic check for demonstration purposes
    return email.contains("@") && email.contains(".")
}


// The `main` function is also a top-level function.
// It can access other top-level members in the same file directly.
fun main() {
    println("--- Top-Level Declarations Examples ---")

    println("App version is: $APP_VERSION")

    val email1 = "test@example.com"
    val email2 = "not-an-email"

    println("Is '$email1' a valid email? ${isValidEmail(email1)}")
    requestCount++
    println("Is '$email2' a valid email? ${isValidEmail(email2)}")
    requestCount++

    println("Total validation requests made: $requestCount")

    println("--------------------------------------")
}
