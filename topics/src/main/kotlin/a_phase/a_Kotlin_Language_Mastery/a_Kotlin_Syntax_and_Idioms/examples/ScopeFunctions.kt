package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates scope functions, which execute a block of code
 * within the context of an object. They provide a way to write more concise
 * and readable code.
 */
data class Person(var name: String, var age: Int, var city: String = "")

fun main() {
    println("--- Scope Functions Examples ---")

    // --- let ---
    // Context object: `it` | Return value: Lambda result
    // Use `let` for executing a lambda on non-null objects and for introducing expressions as variables.
    println("\n--- let ---")
    val name: String? = "Mario"
    name?.let {
        println("Name is not null: $it")
        println("Length is ${it.length}")
    }
    // `let` is also useful for limiting the scope of a variable
    val numbers = listOf("one", "two", "three")
    numbers.map { it.length }.filter { it > 3 }.let(::println)


    // --- apply ---
    // Context object: `this` | Return value: The object itself
    // Use `apply` for object configuration.
    println("\n--- apply ---")
    val person = Person("Carlo", 30).apply {
        // `this` is the Person instance, so you can access properties directly
        age = 31
        city = "New York"
    }
    println("Created person using apply: $person")


    // --- also ---
    // Context object: `it` | Return value: The object itself
    // Use `also` for actions that refer to the whole object rather than its properties,
    // like logging or other side-effects.
    println("\n--- also ---")
    val numbersList = mutableListOf("one", "two", "three")
    numbersList
        .add("four")
        .also { successful -> // `it` here refers to the result of `add()`, which is a Boolean
            println("Adding 'four' was successful: $successful")
        }
        .also {
            // Because the receiver of also is the list itself we can print it
            println("The list after adding: $numbersList")
        }


    // --- run ---
    // Context object: `this` | Return value: Lambda result
    // Use `run` when you need a lambda that returns a result and you want to operate on `this`.
    println("\n--- run ---")
    val personForRun: Person? = Person("Mederos", 25)
    val personBio = personForRun?.run {
        // `this` is the Person instance
        "My name is $name and I am $age years old." // This string is the return value
    }
    println(personBio)


    // --- with ---
    // Not an extension function.
    // Context object: `this` | Return value: Lambda result
    // Use `with` to group calls to functions on the same object.
    println("\n--- with ---")
    with(numbersList) {
        println("List size is $size")
        println("First element is ${first()}")
        println("Last element is ${last()}")
    }


    println("-----------------------------")
}
