package a_phase.a_Kotlin_Language_Mastery.a_Kotlin_Syntax_and_Idioms.examples

/**
 * This file demonstrates companion objects, which are Kotlin's answer to
 * the `static` keyword in Java.
 */

// A regular class can have one companion object.
class MyClass private constructor(val name: String) { // The constructor is private

    // Everything inside the companion object is a singleton, tied to the class itself,
    // not to any specific instance of it.
    companion object {
        // A companion object property, similar to a static field.
        private var instanceCount = 0

        // A companion object constant.
        const val MAX_INSTANCES = 5

        // A factory method. This is a common pattern for controlling object creation.
        // It can access the private constructor of the class.
        fun create(name: String): MyClass? {
            return if (instanceCount < MAX_INSTANCES) {
                instanceCount++
                MyClass(name)
            } else {
                null // Return null if we've reached the max number of instances
            }
        }

        fun getInstanceCount(): Int {
            return instanceCount
        }
    }

    fun greet() {
        println("Hello, my name is $name.")
    }
}

fun main() {
    println("--- Companion Objects Examples ---")

    // You can't call the private constructor directly
    // val myInstance = MyClass("test") // This would be a compilation error

    // You access the companion object members directly via the class name.
    println("Maximum instances allowed: ${MyClass.MAX_INSTANCES}")

    val instance1 = MyClass.create("Instance 1")
    val instance2 = MyClass.create("Instance 2")

    instance1?.greet()
    instance2?.greet()

    println("Current instance count: ${MyClass.getInstanceCount()}")

    // Create more instances to reach the limit
    MyClass.create("Instance 3")?.greet()
    MyClass.create("Instance 4")?.greet()
    val instance5 = MyClass.create("Instance 5")?.greet()

    println("Current instance count after creating more: ${MyClass.getInstanceCount()}")

    val instance6 = MyClass.create("Instance 6")
    if (instance6 == null) {
        println("Could not create Instance 6 because the instance limit was reached.")
    }

    println("---------------------------------")
}
