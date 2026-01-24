package a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples

/**
 * This file demonstrates key concepts of Collections and Immutability in Kotlin,
 * including read-only vs mutable collections, immutable operations, defensive copies,
 * and collection builders.
 *
 * To run this file, you need a Kotlin compiler installed. You can compile and run it
 * from the command line:
 * kotlinc CollectionsImmutabilityExample.kt -include-runtime -d CollectionsImmutabilityExample.jar
 * java -jar CollectionsImmutabilityExample.jar
 */

data class Item(val id: Int, val name: String)
data class UserProfile(private val _permissions: List<String>) {
    // Defensive copy: ensures external code cannot modify the internal list
    val permissions: List<String> = _permissions.toList()
}

fun main() {
    println("--- Collections and Immutability Examples ---")

    // --- 1. Read-Only vs Mutable Collections ---
    println("\n--- Read-Only vs Mutable ---")
    val readOnlyList: List<Int> = listOf(1, 2, 3)
    // readOnlyList.add(4) // Compilation error: 'add' is not a member of List

    val mutableList: MutableList<Int> = mutableListOf(1, 2, 3)
    mutableList.add(4)
    println("Mutable list after add: $mutableList")

    // --- 2. Common Immutable Operations ---
    println("\n--- Immutable Operations ---")
    val originalNumbers = listOf(1, 2, 3, 4, 5)
    println("Original numbers: $originalNumbers")

    val filteredNumbers = originalNumbers.filter { it > 2 } // [3, 4, 5]
    println("Filtered numbers (>2): $filteredNumbers")

    val mappedNumbers = originalNumbers.map { it * 10 } // [10, 20, 30, 40, 50]
    println("Mapped numbers (*10): $mappedNumbers")

    val combinedList = originalNumbers + listOf(6, 7) // [1, 2, 3, 4, 5, 6, 7]
    println("Combined list (+): $combinedList")

    val removedElementList = originalNumbers - 3 // [1, 2, 4, 5]
    println("List after removing 3: $removedElementList")

    // Note: originalNumbers remains unchanged after all these operations.
    println("Original numbers (still unchanged): $originalNumbers")

    // --- 3. When Mutation is Acceptable (and how to hide it) ---
    println("\n--- Controlled Mutation ---")
    val builtList = buildList { // 'buildList' provides a mutable builder
        add("Apple")
        add("Banana")
        for (i in 1..3) {
            add("Fruit$i")
        }
    } // The result is an immutable List<String>
    println("List built with mutable operations (final result is immutable): $builtList")

    // --- 4. Defensive Copies ---
    println("\n--- Defensive Copies ---")
    val initialPermissions = mutableListOf("READ", "WRITE")
    val userProfile = UserProfile(initialPermissions)

    println("User permissions (before external mutation): ${userProfile.permissions}")
    initialPermissions.add("DELETE") // External code tries to mutate the original list

    println("User permissions (after external mutation attempt): ${userProfile.permissions}")
    // The userProfile.permissions list is safe because of the defensive copy!

    println("------------------------------------------")
}
