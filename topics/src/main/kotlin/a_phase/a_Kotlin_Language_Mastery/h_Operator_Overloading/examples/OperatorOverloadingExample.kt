package a_phase.a_Kotlin_Language_Mastery.h_Operator_Overloading.examples

/**
 * This file demonstrates operator overloading in Kotlin for various operators.
 *
 * To run this file, you need a Kotlin compiler installed. You can compile and run it
 * from the command line:
 * kotlinc OperatorOverloadingExample.kt -include-runtime -d OperatorOverloadingExample.jar
 * java -jar OperatorOverloadingExample.jar
 */

// --- 1. Custom Class for Operators ---
data class Point(val x: Int, val y: Int) {
    // Overload the plus (+) operator
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)

    // Overload the minus (-) operator
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    // Overload the times (*) operator for scalar multiplication
    operator fun times(scalar: Int) = Point(x * scalar, y * scalar)

    // Overload the div (/) operator for scalar division
    operator fun div(scalar: Int) = Point(x / scalar, y / scalar)

    // Overload unaryMinus (-) operator
    operator fun unaryMinus() = Point(-x, -y)

    // Overload compareTo for comparison operators (>, <, >=, <=)
    operator fun compareTo(other: Point): Int {
        val thisMagnitude = x * x + y * y
        val otherMagnitude = other.x * other.x + other.y * other.y
        return thisMagnitude.compareTo(otherMagnitude)
    }
}

// --- 2. Overloading Indexing Operators (get/set) ---
class Matrix(private val rows: Int, private val cols: Int) {
    private val data = Array(rows) { IntArray(cols) }

    operator fun get(row: Int, col: Int): Int {
        require(row in 0 until rows && col in 0 until cols) { "Index out of bounds" }
        return data[row][col]
    }

    operator fun set(row: Int, col: Int, value: Int) {
        require(row in 0 until rows && col in 0 until cols) { "Index out of bounds" }
        data[row][col] = value
    }

    override fun toString(): String {
        return data.joinToString("\n") { it.joinToString(" ") }
    }
}

// --- 3. Overloading 'in' operator (contains) ---
operator fun List<String>.contains(element: String): Boolean {
    println("Custom 'in' check for list of strings!")
    return this.any { it.equals(element, ignoreCase = true) }
}


fun main() {
    println("--- Operator Overloading Examples ---")

    println("\n---Point Class Operators ---")
    val p1 = Point(10, 20)
    val p2 = Point(5, 3)

    println("p1: $p1")
    println("p2: $p2")
    println("p1 + p2 = ${p1 + p2}")
    println("p1 - p2 = ${p1 - p2}")
    println("p1 * 2 = ${p1 * 2}")
    println("p1 / 5 = ${p1 / 5}")
    println("-p1 = ${-p1}")

    println("p1 > p2: ${p1 > p2}") // Uses compareTo

    // --- a_phase.a_Kotlin_Language_Mastery.h_Operator_Overloading.examples.Matrix Indexing Operators ---
    println("\n--- Matrix Indexing Operators ---")
    val matrix = Matrix(2, 3)
    matrix[0, 0] = 1
    matrix[0, 1] = 2
    matrix[0, 2] = 3
    matrix[1, 0] = 4
    matrix[1, 1] = 5
    matrix[1, 2] = 6
    println("Matrix:\n$matrix")
    println("matrix[0, 1] = ${matrix[0, 1]}")

    // --- Custom 'in' operator ---
    println("\n--- Custom 'in' operator ---")
    val names = listOf("Alice", "Bob", "Charlie")
    val searchName = "bob"
    if (searchName in names) { // Uses our custom contains operator
        println("'$searchName' is in the list (case-insensitive).")
    } else {
        println("'$searchName' is NOT in the list.")
    }

    println("------------------------------------")
}
