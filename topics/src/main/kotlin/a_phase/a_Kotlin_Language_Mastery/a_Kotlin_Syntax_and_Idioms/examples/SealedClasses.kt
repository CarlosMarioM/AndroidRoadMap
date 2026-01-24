package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates sealed classes, which are used for representing
 * restricted class hierarchies. They are a perfect fit for modeling states or results.
 */

// A sealed class can only be subclassed inside the same file where it's declared.
// This gives the compiler full knowledge of all possible subtypes.
sealed class NetworkResult {
    // We can use data classes for states that hold data.
    data class Success(val data: String) : NetworkResult()

    // We can use regular classes for states that might have more complex logic.
    class Error(val code: Int, val message: String) : NetworkResult() {
        fun isCritical() = code >= 500
    }

    // We can use objects for states that don't need any specific data.
    data object Loading : NetworkResult()
}

// The power of sealed classes shines when used with `when`.
// Because the compiler knows all possible types, it can enforce that the `when` is exhaustive.
// You don't need an `else` branch if you cover all cases.
fun handleResult(result: NetworkResult) {
    when (result) {
        is NetworkResult.Success -> {
            println("Success! Data received: ${result.data}")
        }
        is NetworkResult.Error -> {
            println("Error ${result.code}: ${result.message}")
            if (result.isCritical()) {
                println("This is a critical server error!")
            }
        }
        NetworkResult.Loading -> {
            println("Loading...")
        }
    }
}

fun main() {
    println("--- Sealed Classes Examples ---")

    val successResult = NetworkResult.Success("{\"user\":\"Mario\"}")
    val loadingResult = NetworkResult.Loading
    val errorResult = NetworkResult.Error(404, "Not Found")
    val criticalErrorResult = NetworkResult.Error(500, "Internal Server Error")

    println("\nHandling different results:")
    handleResult(loadingResult)
    handleResult(successResult)
    handleResult(errorResult)
    handleResult(criticalErrorResult)

    println("----------------------------")
}
