package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates one of Kotlin's most powerful features: Extension Functions and Properties. 
 * They allow you to add new functionality to existing classes without modifying their source code.
 */

// --- Extension Function ---
// Here, we add a new function `initials` to the `String` class.
// The `this` keyword inside the function refers to the receiver object (the string itself).
fun String.initials(): String {
    return this.split(' ')
        .filter { it.isNotBlank() }
        .map { it.firstOrNull()?.uppercase() ?: "" }
        .joinToString("")
}

// --- Extension Property ---
// We can also add new properties to existing classes.
// Since there's no place to store a backing field, they must be computed (read-only).
val String.wordCount: Int
    get() = this.split(' ').filter { it.isNotBlank() }.size


// --- Extension Function on a Nullable Receiver ---
// You can define extensions for nullable types as well.
fun String?.isBlankOrNull(): Boolean {
    // Inside, `this` is nullable.
    return this == null || this.isBlank()
}


fun main() {
    println("--- Extension Functions and Properties Examples ---")

    val fullName = "Carlo di Mario Mederos"
    println("Full Name: \"$fullName\"")
    println("Initials: ${fullName.initials()}") // Calling our new extension function like a regular method
    println("Word Count: ${fullName.wordCount}") // Accessing our new extension property

    println("-----")

    val emptyString = ""
    val nullableString: String? = null

    println("Is \"$emptyString\" blank or null? ${emptyString.isBlankOrNull()}")
    println("Is the nullable string blank or null? ${nullableString.isBlankOrNull()}")

    println("----------------------------------------------")
}

