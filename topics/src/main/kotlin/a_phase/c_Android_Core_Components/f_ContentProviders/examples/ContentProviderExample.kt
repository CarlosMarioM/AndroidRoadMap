package a_phase.c_Android_Core_Components.f_ContentProviders.examples

/**
 * This file conceptually demonstrates an Android ContentProvider and ContentResolver.
 * It illustrates the core methods a ContentProvider implements (query, insert, update, delete)
 * and how a ContentResolver (conceptual client) would interact with it using URIs.
 *
 * It is a simplified Kotlin file, not a full Android project.
 *
 * To run this example in a real Android project:
 * 1. Create a new Android project.
 * 2. Create a `ContentProvider` subclass (e.g., `MyDataProvider`).
 * 3. Copy the relevant code snippets into the `ContentProvider` and an `Activity` (for `ContentResolver` usage).
 * 4. Declare the ContentProvider in your `AndroidManifest.xml`.
 */

// --- Conceptual Android Imports ---
// import android.content.ContentProvider
// import android.content.ContentValues
// import android.content.UriMatcher
// import android.database.Cursor
// import android.net.Uri
// import android.database.MatrixCursor // For conceptual Cursor
// import android.content.ContentResolver

// --- Conceptual URI Constants ---
const val AUTHORITY = "com.example.androidroadmap.provider"
const val PATH_ITEMS = "items"
const val PATH_ITEM_ID = "items/#"
const val ITEMS_LIST_CODE = 1
const val ITEMS_ID_CODE = 2

// --- Conceptual UriMatcher ---
// In a real app: UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
// uriMatcher.addURI(a_phase.c_Android_Core_Components.f_ContentProviders.examples.AUTHORITY, a_phase.c_Android_Core_Components.f_ContentProviders.examples.PATH_ITEMS, a_phase.c_Android_Core_Components.f_ContentProviders.examples.ITEMS_LIST_CODE);
// uriMatcher.addURI(a_phase.c_Android_Core_Components.f_ContentProviders.examples.AUTHORITY, a_phase.c_Android_Core_Components.f_ContentProviders.examples.PATH_ITEM_ID, a_phase.c_Android_Core_Components.f_ContentProviders.examples.ITEMS_ID_CODE);

// --- Conceptual Data Storage (in-memory for demo) ---
val conceptualDatabase = mutableMapOf<Long, Map<String, Any>>(
    1L to mapOf("id" to 1L, "name" to "a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item A", "value" to 100),
    2L to mapOf("id" to 2L, "name" to "a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item B", "value" to 200)
)
var nextId = 3L


// --- Conceptual ContentProvider ---
// In a real Android project, this would extend android.content.ContentProvider
class ConceptualContentProvider {

    fun onCreate(): Boolean {
        println("[Provider] onCreate() called. Initializing provider.")
        // Perform initialization, e.g., open a database connection
        return true
    }

    fun query(uri: String, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): String? /* Conceptual Cursor */ {
        println("[Provider] query() called for URI: $uri")
        // In real app, match URI to determine what data to return
        // int match = conceptualUriMatcher.match(Uri.parse(uri));
        // switch (match) { ... }
        if (uri.contains(PATH_ITEMS)) {
            val result = conceptualDatabase.values.joinToString("\n") { it.toString() }
            println("[Provider] Query result: \n$result")
            return result // Conceptual return
        }
        return null
    }

    fun insert(uri: String, values: Map<String, Any>?): String? /* Conceptual Uri */ {
        println("[Provider] insert() called for URI: $uri with values: $values")
        if (uri.contains(PATH_ITEMS) && values != null) {
            val id = nextId++
            conceptualDatabase[id] = values + ("id" to id)
            println("[Provider] Inserted new item with ID: $id")
            return "$uri/$id" // Conceptual new URI
        }
        return null
    }

    fun update(uri: String, values: Map<String, Any>?, selection: String?, selectionArgs: Array<String>?): Int {
        println("[Provider] update() called for URI: $uri with values: $values")
        // In real app, update database rows based on URI and selection
        if (uri.contains(PATH_ITEM_ID) && values != null) {
            val id = uri.substringAfterLast("/").toLongOrNull()
            if (id != null && conceptualDatabase.containsKey(id)) {
                conceptualDatabase[id] = conceptualDatabase[id]!! + values
                println("[Provider] Updated item with ID: $id")
                return 1
            }
        }
        return 0
    }

    fun delete(uri: String, selection: String?, selectionArgs: Array<String>?): Int {
        println("[Provider] delete() called for URI: $uri")
        // In real app, delete rows based on URI and selection
        if (uri.contains(PATH_ITEM_ID)) {
            val id = uri.substringAfterLast("/").toLongOrNull()
            if (id != null && conceptualDatabase.remove(id) != null) {
                println("[Provider] Deleted item with ID: $id")
                return 1
            }
        }
        return 0
    }

    fun getType(uri: String): String? {
        println("[Provider] getType() called for URI: $uri")
        // Return MIME type for the URI
        if (uri.contains(PATH_ITEMS)) return "vnd.android.cursor.dir/vnd.com.example.item"
        if (uri.contains(PATH_ITEM_ID)) return "vnd.android.cursor.item/vnd.com.example.item"
        return null
    }
}

// --- Conceptual ContentResolver (Client-Side) ---
class ConceptualContentResolver(private val provider: ConceptualContentProvider) {
    fun query(uri: String, projection: Array<String>? = null, selection: String? = null, selectionArgs: Array<String>? = null, sortOrder: String? = null): String? {
        println("[Resolver] Calling query on Provider for URI: $uri")
        return provider.query(uri, projection, selection, selectionArgs, sortOrder)
    }

    fun insert(uri: String, values: Map<String, Any>?): String? {
        println("[Resolver] Calling insert on Provider for URI: $uri with values: $values")
        return provider.insert(uri, values)
    }

    fun update(uri: String, values: Map<String, Any>?, selection: String? = null, selectionArgs: Array<String>? = null): Int {
        println("[Resolver] Calling update on Provider for URI: $uri with values: $values")
        return provider.update(uri, values, selection, selectionArgs)
    }

    fun delete(uri: String, selection: String? = null, selectionArgs: Array<String>? = null): Int {
        println("[Resolver] Calling delete on Provider for URI: $uri")
        return provider.delete(uri, selection, selectionArgs)
    }

    fun getType(uri: String): String? {
        println("[Resolver] Calling getType on Provider for URI: $uri")
        return provider.getType(uri)
    }
}

fun main() {
    println("--- Conceptual ContentProvider and ContentResolver Example ---")

    val provider = ConceptualContentProvider()
    provider.onCreate() // Initialize provider

    val resolver = ConceptualContentResolver(provider)

    val baseUri = "content://$AUTHORITY/$PATH_ITEMS"

    // --- Query operation ---
    println("\n--- Query All Items ---")
    val allItems = resolver.query(baseUri)
    println("Resolver received (conceptual Cursor):\n$allItems")

    // --- Insert operation ---
    println("\n--- Insert New a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item ---")
    val newItemValues = mapOf("name" to "a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item C", "value" to 300)
    val newItemUri = resolver.insert(baseUri, newItemValues)
    println("Resolver received new item URI: $newItemUri")

    println("\n--- Query All Items (after insert) ---")
    println("Resolver received:\n${resolver.query(baseUri)}")

    // --- Update operation ---
    println("\n--- Update an a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item ---")
    val updateUri = "content://$AUTHORITY/$PATH_ITEMS/1"
    val updateValues = mapOf("value" to 150)
    val updatedRows = resolver.update(updateUri, updateValues)
    println("Resolver updated $updatedRows row(s).")

    println("\n--- Query All Items (after update) ---")
    println("Resolver received:\n${resolver.query(baseUri)}")

    // --- Delete operation ---
    println("\n--- Delete an a_phase.a_Kotlin_Language_Mastery.g_Collections_and_Immutability.examples.Item ---")
    val deleteUri = "content://$AUTHORITY/$PATH_ITEMS/2"
    val deletedRows = resolver.delete(deleteUri)
    println("Resolver deleted $deletedRows row(s).")

    println("\n--- Query All Items (after delete) ---")
    println("Resolver received:\n${resolver.query(baseUri)}")

    println("\n--- Get Type ---")
    val mimeType = resolver.getType("content://$AUTHORITY/$PATH_ITEMS")
    println("MIME type for $baseUri: $mimeType")

    println("-------------------------------------------------------------")
}
