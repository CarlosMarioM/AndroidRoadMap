package a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples

/**
 * This file demonstrates the combined usage of Data Classes, Sealed Classes,
 * and Value Classes (Inline Classes) to model a robust domain.
 *
 * To run this file, you need a Kotlin compiler installed. You can compile and run it
 * from the command line:
 * kotlinc DataSealedValueClasses.kt -include-runtime -d DataSealedValueClasses.jar
 * java -jar DataSealedValueClasses.jar
 */

// 1. Value Class: Providing type safety for primitive IDs
@JvmInline
value class UserId(val value: String) {
    companion object {
        fun fromString(id: String): UserId {
            require(id.isNotBlank()) { "User ID cannot be blank" }
            return UserId(id)
        }
    }
}

// 2. Data Class: Holding structured data, often composed with Value Classes
data class DataSealedUser(
    val id: UserId, // Using our type-safe a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId
    val name: String,
    val email: String
)

// 3. Sealed Interface: Defining a closed hierarchy of states or results
// Using a sealed interface allows for more flexible composition if needed,
// as a class can implement multiple interfaces.
sealed interface LoadUserState {
    data object Loading : LoadUserState // Singleton object for loading state
    data class Success(val user: DataSealedUser) : LoadUserState // Data class for success state
    data class Error(val message: String) : LoadUserState // Data class for error state
}

fun simulateUserLoad(id: UserId): LoadUserState {
    return when (id.value) {
        "user_123" -> LoadUserState.Success(DataSealedUser(id, "Alice", "alice@example.com"))
        "user_404" -> LoadUserState.Error("User with ID ${id.value} not found.")
        else -> LoadUserState.Error("Invalid user ID.")
    }
}

fun handleLoadUserState(state: LoadUserState) {
    when (state) {
        LoadUserState.Loading -> println("Loading user data...")
        is LoadUserState.Success -> println("User loaded: ${state.user.name} (${state.user.email})")
        is LoadUserState.Error -> println("Failed to load user: ${state.message}")
        else -> println(state)
    }
}

fun main() {
    println("--- Data Classes vs Sealed Classes vs Value Classes Examples ---")

    // --- Value Class Usage ---
    val userId1 = UserId.fromString("user_123")
    println("Created a_phase.a_Kotlin_Language_Mastery.c_Data_Classes_vs_Sealed_Classes_vs_Value_Classes.examples.UserId: ${userId1.value}")

    // --- Data Class Usage ---
    val user1 = DataSealedUser(userId1, "Bob", "bob@example.com")
    println("Created User: $user1")
    println("User ID (type-safe): ${user1.id.value}")

    // --- Sealed Interface Usage ---
    println("\nSimulating user load:")
    handleLoadUserState(simulateUserLoad(UserId.fromString("user_123")))
    handleLoadUserState(simulateUserLoad(UserId.fromString("user_404")))
    handleLoadUserState(simulateUserLoad(UserId.fromString("user_999")))

    println("-------------------------------------------------------------")
}
