package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates the basic syntax of Kotlin.
 */
fun main() {
    // To run, right-click the file and select "Run BasicSyntaxKt"
    println("--- Basic Syntax Examples ---")
    variableExamples()
    functionExamples()
    stringTemplateExamples()
    println("--------------------------")
}

fun variableExamples() {
    println("\n--- val vs var ---")
    // Use `val` for read-only (immutable) variables. It's preferred.
    val immutableValue = "I can't be changed."
    println("val: $immutableValue")

    // Use `var` for mutable variables, but only when necessary.
    var mutableValue = "I can be changed."
    println("var (before): $mutableValue")
    mutableValue = "See? I've been updated."
    println("var (after): $mutableValue")
}

fun functionExamples() {
    println("\n--- Functions ---")

    // A standard function with a block body
    fun standardSum(a: Int, b: Int): Int {
        return a + b
    }
    println("Standard function: 5 + 3 = ${standardSum(5, 3)}")

    // A function with an expression body (more concise)
    fun expressionSum(a: Int, b: Int) = a + b
    println("Expression body function: 5 + 3 = ${expressionSum(5, 3)}")


    // A function with default and named arguments
    fun greet(name: String, message: String = "Hello") {
        println("'$message, $name!'")
    }
    print("Function with default argument: ")
    greet("Mario") // Uses the default "Hello"

    print("Function with named argument: ")
    greet(message = "Hi there", name = "Carlo") // Arguments can be out of order if named
}

fun stringTemplateExamples() {
    println("\n--- String Templates & Multiline Strings ---")
    val name = "Kotlin"

    // Simple template for a variable
    println("Simple template: Hello, $name!")

    // Template with an expression
    println("Template with expression: My name has ${name.length} letters.")

    // Multiline strings using triple quotes
    // .trimIndent() removes the leading whitespace from each line.
    val multilineString = """
        This is a string
        that spans multiple lines.
        The indentation from the source code is removed.
    """.trimIndent()
    println("Multiline string:\n$multilineString")
}
