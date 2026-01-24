package a_phase.a_Kotlin_Language_Mastery.e_Inline_Functions_and_Reified_Generics.examples

/**
 * This file demonstrates inline functions and reified generics.
 *
 * To run this file, you need a Kotlin compiler installed. You can compile and run it
 * from the command line:
 * kotlinc InlineFunctionsExample.kt -include-runtime -d InlineFunctionsExample.jar
 * java -jar InlineFunctionsExample.jar
 */

// --- 1. Basic Inline Function ---
// The body of this function will be copied to the call site,
// avoiding the overhead of a function call and lambda object creation.
inline fun measureTimeMillis(block: () -> Unit): Long {
    val start = System.currentTimeMillis()
    block()
    return System.currentTimeMillis() - start
}

// --- 2. Reified Generics ---
// This function can check the type `T` at runtime because it's inlined and reified.
inline fun <reified T> findFirstIsInstance(list: List<Any>): T? {
    for (item in list) {
        if (item is T) {
            return item
        }
    }
    return null
}

// --- 3. Non-Local Return ---
// This is possible because `forEach` is an inline function.
fun findInList(list: List<String>, nameToFind: String) {
    println("\nSearching for '$nameToFind'...")
    list.forEach {
        if (it == nameToFind) {
            println("Found it!")
            return // This returns from the entire `a_phase.a_Kotlin_Language_Mastery.e_Inline_Functions_and_Reified_Generics.examples.findInList` function
        }
    }
    println("'$nameToFind' was not in the list.")
}

// --- 4. crossinline and noinline ---
// `noinline` allows a lambda to be stored or passed, opting it out of inlining.
// `crossinline` prevents non-local returns, for when a lambda is called in a different context.
inline fun processLambdas(crossinline block1: () -> Unit, noinline block2: () -> Unit) {
    // block1 cannot have a non-local return because it's called inside another lambda.
    val runnable = Runnable {
        println("Executing block1 in a Runnable")
        block1()
    }
    runnable.run()

    // block2 can be stored because it is marked noinline.
    val storedLambda = block2
    println("Executing stored block2")
    storedLambda()
}

// --- 5. Inlined Property ---
val rightNow: Long
    inline get() = System.currentTimeMillis()


fun main() {
    println("--- Inline Functions and Reified Generics Examples ---")

    // Example 1: a_phase.a_Kotlin_Language_Mastery.e_Inline_Functions_and_Reified_Generics.examples.measureTimeMillis
    val duration = measureTimeMillis {
        println("\nExecuting a task...")
        Thread.sleep(100) // Simulate work
        println("Task finished.")
    }
    println("The task took $duration ms.")

    // Example 2: Reified generics
    val mixedList: List<Any> = listOf("hello", 123, "world", 45.6, InlineFunctionsUser("Alice"))
    val firstString = findFirstIsInstance<String>(mixedList)
    val firstInt = findFirstIsInstance<Int>(mixedList)
    val firstUser = findFirstIsInstance<InlineFunctionsUser>(mixedList)
    println("\nFrom list: $mixedList")
    println("Found first String: '$firstString'")
    println("Found first Int: $firstInt")
    println("Found first User: $firstUser")

    // Example 3: Non-local return
    val names = listOf("Alice", "Bob", "Charlie")
    findInList(names, "Bob")
    findInList(names, "David")

    // Example 4: crossinline and noinline
    println("\n--- crossinline and noinline ---")
    processLambdas(
        { println("This is block1.") },
        { println("This is block2.") }
    )

    // Example 5: Inlined property
    println("\n--- Inlined Property ---")
    println("The time right now is $rightNow")

    println("-------------------------------------------------------")
}

private data class InlineFunctionsUser(val name: String)
