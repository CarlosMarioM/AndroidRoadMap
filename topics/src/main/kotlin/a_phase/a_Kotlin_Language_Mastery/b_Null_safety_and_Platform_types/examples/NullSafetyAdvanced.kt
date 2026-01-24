package a_phase.a_Kotlin_Language_Mastery.b_Null_safety_and_Platform_types.examples

/**
 * This file demonstrates `checkNotNull` and `requireNotNull` as safer
 * alternatives to `!!` for enforcing non-null constraints.
 */

data class Configuration(val apiKey: String?, val featureEnabled: Boolean)

fun processConfiguration(config: Configuration?) {
    // Using requireNotNull to validate function arguments
    // Throws IllegalArgumentException if config is null
    val nonNullConfig = requireNotNull(config) { "Configuration object cannot be null." }

    // Using checkNotNull for internal state or properties
    // Throws IllegalStateException if apiKey is null
    val validApiKey = checkNotNull(nonNullConfig.apiKey) { "API Key must be provided for the configuration." }

    println("Processing configuration with API Key: $validApiKey")
    println("Feature Enabled: ${nonNullConfig.featureEnabled}")
}

fun main() {
    println("--- checkNotNull and requireNotNull Examples ---")

    // --- Valid Scenario ---
    val validConfig = Configuration("mySecretApiKey123", true)
    println("\nValid Configuration:")
    processConfiguration(validConfig)

    // --- Scenario: Null API Key ---
    val configWithNullApiKey = Configuration(null, false)
    println("\nConfiguration with null API Key (expecting IllegalStateException):")
    try {
        processConfiguration(configWithNullApiKey)
    } catch (e: IllegalStateException) {
        println("Caught expected error: ${e.message}")
    }

    // --- Scenario: Null Configuration Object ---
    val nullConfig: Configuration? = null
    println("\nNull Configuration Object (expecting IllegalArgumentException):")
    try {
        processConfiguration(nullConfig)
    } catch (e: IllegalArgumentException) {
        println("Caught expected error: ${e.message}")
    }

    // --- Demonstrating usage in a simpler context ---
    val someValue: String? = "Hello"
    val guaranteedValue = checkNotNull(someValue) { "This value should not be null!" }
    println("\nGuaranteed value: $guaranteedValue")

    val anotherValue: String? = null
    try {
        val stillGuaranteedValue = checkNotNull(anotherValue) { "This value was null after all!" }
        println("This won't print: $stillGuaranteedValue")
    } catch (e: IllegalStateException) {
        println("Caught expected error for anotherValue: ${e.message}")
    }

    println("----------------------------------------------")
}
