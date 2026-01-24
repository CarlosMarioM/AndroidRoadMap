package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates Kotlin's robust null safety system, which is designed
 * to eliminate NullPointerExceptions from your code.
 */
fun main() {
    println("--- Null Safety Examples ---")

    // --- Non-nullable vs. Nullable Types ---
    var nonNullableName: String = "Mario"
    // nonNullableName = null // This would cause a compilation error.

    var nullableName: String? = "Carlo"
    nullableName = null // This is allowed because the type is marked with `?`.

    println("Non-nullable value: $nonNullableName")
    println("Nullable value: $nullableName")


    // --- Working with Nullable Types ---
    println("\n--- Safe Calls (?.) ---")
    val name: String? = "I might be null"
    // The `?.` operator will only call `.length` if `name` is not null.
    // If `name` is null, the expression evaluates to null.
    val length = name?.length
    println("The length of '$name' is $length")

    val nullName: String? = null
    val nullLength = nullName?.length
    println("The length of a null string is $nullLength")


    println("\n--- The Elvis Operator (?:) ---")
    // The Elvis operator provides a default value if the expression on the left is null.
    val len = nullName?.length ?: 0 // If length is null, use 0 instead.
    println("Using the Elvis operator, the length is $len")

    val displayName = nullName ?: "Guest"
    println("Display name is '$displayName'")


    println("\n--- The Not-Null Assertion (!!) - AVOID ---")
    // The `!!` operator converts any value to a non-null type and throws a
    // NullPointerException if the value is null. It's a code smell and should be avoided.
    val riskyName: String? = "I'm definitely not null... probably."
    try {
        val l = riskyName!!.length // This is fine
        println("Risky name length: $l")

        val veryRiskyName: String? = null
        veryRiskyName!!.length // This will throw a NullPointerException
    } catch (e: NullPointerException) {
        println("Caught an exception: ${e.message}")
    }
    println("Use !! only when you are 100% certain the value is not null.")


    println("\n--- Safe Cast (as?) ---")
    val someValue: Any = "This is a string"
    val stringValue: String? = someValue as? String
    println("Safe cast to String: $stringValue")

    val anotherValue: Any = 123
    val notAString: String? = anotherValue as? String // This will not throw an exception
    println("Safe cast of an Int to String: $notAString")

    println("-------------------------")
}
