package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates how classes and constructors are defined in Kotlin.
 */

// A simple class with a primary constructor.
// The `val` keyword in the constructor declares a read-only property.
// This concise syntax avoids the boilerplate of getters, setters, and field declarations.
class Car(val brand: String, var speed: Int = 0) {
    // This is an initializer block. It runs when an instance of the class is created.
    // It's useful for validation or setup logic that can't be done in the property declaration.
    init {
        require(brand.isNotBlank()) { "Brand name can't be blank." }
        println("$brand car created.")
    }

    fun accelerate(amount: Int) {
        speed += amount
        println("$brand is accelerating to $speed km/h.")
    }

    fun decelerate(amount: Int) {
        speed -= amount
        if (speed < 0) speed = 0
        println("$brand is decelerating to $speed km/h.")
    }
}

fun main() {
    println("--- Classes and Constructors Examples ---")

    // Create an instance of the a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples.Car class
    val myCar = Car("Tesla", 0)
    println("My car is a ${myCar.brand} and its current speed is ${myCar.speed} km/h.")

    // Call methods on the object
    myCar.accelerate(100)
    myCar.decelerate(20)

    println("-----")

    // Create another instance with a different brand
    val anotherCar = Car(brand = "Ford") // Speed defaults to 0
    anotherCar.accelerate(80)

    println("------------------------------------")
}
