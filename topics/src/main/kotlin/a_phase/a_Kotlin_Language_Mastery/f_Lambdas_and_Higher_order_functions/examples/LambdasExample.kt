package a_phase.a_Kotlin_Language_Mastery.f_Lambdas_and_Higher_order_functions.examples

import a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item

/**
 * This file demonstrates lambdas and higher-order functions in Kotlin.
 *
 * To run this file, you need a Kotlin compiler installed. You can compile and run it
 * from the command line:
 * kotlinc LambdasExample.kt -include-runtime -d LambdasExample.jar
 * java -jar LambdasExample.jar
 */

// --- 1. Defining a Lambda ---
// A lambda is an anonymous function. Here, we store it in a variable.
val greet: (String) -> String = { name -> "Hello, $name!" }

// --- 2. Higher-Order Function ---
// A function that takes another function as an argument.
fun operateOnNumber(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

// --- 3. Type Alias for a Function Type ---
data class LambdasItem(val id: Int, val name: String)
// This makes the function type more readable and reusable.
typealias OnItemClickListener = (item: Item, position: Int) -> Unit

class RecyclerView {
    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun simulateClick(item: Item, position: Int) {
        println("\nSimulating a click...")
        listener?.invoke(item, position)
    }
}

// --- 4. Returning a Lambda (Function Factory) ---
fun getMultiplier(factor: Int): (Int) -> Int {
    return { number -> number * factor }
}

// --- 5. Lambda with a Receiver (DSL-like syntax) ---
fun buildHtml(block: StringBuilder.() -> Unit): String {
    val stringBuilder = StringBuilder()
    stringBuilder.block()
    return stringBuilder.toString()
}

fun main() {
    println("--- Lambdas and Higher-Order Functions Examples ---")

    // --- Calling a lambda ---
    println("\n1. Calling a lambda:")
    println(greet("Kotlin Developer"))

    // --- Using a higher-order function ---
    println("\n2. Using a higher-order function:")
    val sum = operateOnNumber(10, 5) { a, b -> a + b }
    val product = operateOnNumber(10, 5) { a, b -> a * b }
    println("Sum: $sum")
    println("Product: $product")

    // Using trailing lambda syntax
    operateOnNumber(20, 4) { a, b ->
        println("Dividing $a by $b")
        a / b // The last expression is the return value
    }

    // --- Using a type alias ---
    println("\n3. Using a type alias for a listener:")
    val recyclerView = RecyclerView()
    recyclerView.setOnItemClickListener { item, position ->
        println("Clicked on item '${item.name}' at position $position")
    }
    recyclerView.simulateClick(
        Item(
            1,
            "Kotlin a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Book"
        ), 0)

    // --- Capturing a variable (Closure) ---
    println("\n4. Lambda with a closure:")
    var counter = 0
    val incrementAndPrint = {
        counter++ // The lambda "closes over" the counter variable
        println("Counter is now: $counter")
    }
    incrementAndPrint()
    incrementAndPrint()

    // --- Returning a lambda ---
    println("\n5. Function that returns a lambda:")
    val doubler = getMultiplier(2)
    val tripler = getMultiplier(3)
    println("Doubling 5: ${doubler(5)}")
    println("Tripling 5: ${tripler(5)}")

    // --- Lambda with a receiver ---
    println("\n6. Lambda with a receiver (DSL):")
    val html = buildHtml {
        append("<html>\n")
        append("  <body>\n")
        append("    <h1>Hello, DSL!</h1>\n")
        append("  </body>\n")
        append("</html>")
    }
    println(html)

    println("--------------------------------------------------")
}
