package a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples

/**
 * This file demonstrates the usage of Sealed Interfaces in Kotlin, including
 * basic hierarchies, generics, and multiple inheritance.
 *
 * To run this file, you need a Kotlin compiler installed. You can compile and run it
 * from the command line:
 * kotlinc SealedInterfacesExample.kt -include-runtime -d SealedInterfacesExample.jar
 * java -jar SealedInterfacesExample.jar
 */

// --- Basic Example ---
sealed interface DownloadStatus {
    data object InProgress : DownloadStatus
    data class Success(val content: String) : DownloadStatus
    data class Error(val message: String) : DownloadStatus
}

// --- Example with Generics for Reusability ---
data class SealedUser(val name: String)

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val message: String) : Result<Nothing>
    data object Loading : Result<Nothing>
}

// --- Multiple Inheritance Example ---
sealed interface Loggable {
    fun log()
}

sealed interface Retryable {
    fun retry()
}

// An event can be both a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Loggable and a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Retryable
data class NetworkError(val code: Int) : Loggable, Retryable {
    override fun log() {
        println("Logging network error with code: $code")
    }

    override fun retry() {
        println("Retrying the network request...")
    }
}

data class UserAction(val action: String) : Loggable {
    override fun log() {
        println("Logging user action: $action")
    }
}

fun main() {
    println("--- Sealed Interfaces Examples ---")

    // --- Basic Usage ---
    println("\n--- Basic Download Status ---")
    val status1: DownloadStatus = DownloadStatus.Success("Some file content")
    val status2: DownloadStatus = DownloadStatus.Error("Connection timed out")

    fun handleStatus(status: DownloadStatus) {
        when (status) {
            is DownloadStatus.Success -> println("Download successful: ${status.content}")
            is DownloadStatus.Error -> println("Download failed: ${status.message}")
            DownloadStatus.InProgress -> println("Download is still in progress...")
            else -> println(status)
        }
    }
    handleStatus(status1)
    handleStatus(status2)

    // --- Generics Usage ---
    println("\n--- Generic a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Result Type ---")
    val userResult: Result<SealedUser> = Result.Success(SealedUser("Alice"))
    val photoResult: Result<ByteArray> = Result.Error("Permission denied")

    fun <T> handleResult(result: Result<T>) {
        when (result) {
            is Result.Success -> println("Success! Data: ${result.data}")
            is Result.Error -> println("Error: ${result.message}")
            Result.Loading -> println("Loading...")
            else -> println(result)
        }
    }
    handleResult(userResult)
    handleResult(photoResult)

    // --- Multiple Inheritance Usage ---
    println("\n--- Multiple Inheritance ---")
    val event1: Loggable = NetworkError(500)
    val event2: Loggable = UserAction("Clicked 'Save'")

    event1.log()
    if (event1 is Retryable) {
        event1.retry()
    }

    println("-----")
    event2.log()
    if (event2 is Retryable) {
        // This block will not be executed for a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.UserAction
        event2.retry()
    } else {
        println("'${(event2 as UserAction).action}' is not a retryable event.")
    }

    println("---------------------------------")
}
