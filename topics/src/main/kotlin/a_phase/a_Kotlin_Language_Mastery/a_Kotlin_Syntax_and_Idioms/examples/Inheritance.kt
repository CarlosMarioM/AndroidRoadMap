package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates inheritance and interfaces in Kotlin.
 */

// --- Interfaces ---
// An interface defines a contract that other classes can implement.
interface Drivable {
    val topSpeed: Int
    fun drive()
    fun stop() {
        // Interfaces can provide default implementations
        println("Stopping the vehicle.")
    }
}

// Another interface
interface Loggable {
    fun log(message: String) = println("LOG: $message")
}


// --- Inheritance ---
// In Kotlin, classes are `final` by default. To allow a class to be inherited from,
// you must mark it with the `open` keyword.
open class Vehicle(val brand: String) {

    // Methods are also `final` by default. Mark with `open` to allow overriding.
    open fun start() {
        println("$brand vehicle starting...")
    }
}

// The `a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Car` class inherits from `Vehicle` and implements two interfaces.
// The primary constructor of `Vehicle` must be called.
class CarFromVehicle(brand: String, override val topSpeed: Int) : Vehicle(brand), Drivable, Loggable {

    // Overriding a method from the base class `Vehicle`
    override fun start() {
        super.start() // It's good practice to call the superclass method
        println("$brand car engine ON.")
        log("a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Car started") // Calling method from a_phase.a_Kotlin_Language_Mastery.d_Sealed_Interfaces.examples.Loggable interface
    }

    // Implementing the `drive` method from the `Drivable` interface
    override fun drive() {
        println("$brand car is driving at a max speed of $topSpeed km/h.")
    }

    // We don't need to override `stop()` or `log()` because they have default implementations.
}

fun main() {
    println("--- Inheritance and Interfaces Examples ---")

    val myCar = CarFromVehicle("Audi", 250)
    myCar.start()
    myCar.drive()
    myCar.stop() // Calling the default method from the interface

    println("----------------------------------------")
}
