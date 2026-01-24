# Fragment Lifecycle vs View Lifecycle

Fragments are **UI composition units**, but their lifecycle is **trickier than Activities**. Misunderstanding leads to:
- memory leaks
- null pointer crashes
- unexpected behavior with child views

This document explains **Fragment vs View lifecycles** and the senior-level mental model.

---

## Core truth

- A Fragment manages its **own lifecycle** separate from its **view hierarchy**.
- A Fragment can exist **without a view** (e.g., retained across configuration changes).
- Views are created and destroyed **multiple times** during a Fragment’s lifetime.

Failing to distinguish the two is a major source of bugs.

---

## Fragment lifecycle

Primary callbacks:
1. `onAttach()` → fragment associated with Activity
2. `onCreate()` → non-view initialization, state, variables
3. `onCreateView()` → inflate and return view hierarchy
4. `onViewCreated()` → view setup, listeners, adapters
5. `onStart()` → visible
6. `onResume()` → interactive
7. `onPause()` → lose focus, save UI changes
8. `onStop()` → not visible
9. `onDestroyView()` → **view is destroyed**
10. `onDestroy()` → clean up remaining fragment resources
11. `onDetach()` → fragment disassociated from Activity

Key point:
> `onDestroyView()` ≠ `onDestroy()`

---

## View lifecycle

- Views exist between `onCreateView()` and `onDestroyView()`.
- All references to Views **must be cleared** in `onDestroyView()`.
- Accessing Views outside this window risks **null pointer exceptions**.

Example mistake:
```kotlin
class MyFragment : Fragment(R.layout.fragment_layout) {
    private var textView: TextView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textView = view.findViewById(R.id.text)
    }

    override fun onDestroyView() {
        // textView = null // must clear to avoid leaks
    }
}
```

---

## Senior-level mental model

- Fragment = controller / lifecycle owner
- View = ephemeral UI representation
- **Never hold long-lived references to Views**
- Observers and adapters should be lifecycle-aware to avoid leaks
- Use `viewLifecycleOwner` for LiveData / Flow observation

---

## Common pitfalls

1. Accessing views after `onDestroyView()` → crash
2. Storing View references in Fragment fields → memory leaks
3. Observing LiveData without `viewLifecycleOwner` → leaks and double updates
4. Confusing Fragment retention with View retention → stale UI data
5. Doing heavy work in `onCreateView()` → UI jank

---

## Best practices

- Initialize views in `onViewCreated()`
- Clear references in `onDestroyView()`
- Observe LiveData using `viewLifecycleOwner`
- Keep Fragment state in ViewModel, not Views
- Treat Views as **ephemeral**; Fragments are **persistent across rotation** if retained

---

## Takeaway

> Fragment lifecycle != View lifecycle.

Senior Android developers **separate the two**: Fragment manages logic and state; View exists only for rendering. All UI references should respect the **view lifecycle** to avoid crashes and leaks.

