import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examination.Edit
import com.example.examination.R
import com.example.examination.adapter.NoteAdapter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NoteShow : AppCompatActivity(), NoteAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NoteAdapter
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_show)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView2)
        recyclerView.layoutManager = LinearLayoutManager(this)

        retrieveProductsFromFirestore()
    }

    private fun retrieveProductsFromFirestore() {
        val productsCollection = firestore.collection("notes")

        productsCollection.get()
            .addOnSuccessListener { result ->
                val notes = mutableListOf<NoteList>()
                for (document in result) {
                    val title = document.getString("noteTitle")
                    val timestamp = document.getTimestamp("timestamp")?.toDate()
                    val note = document.getString("note")
                    val formattedTimestamp = timestamp?.toString()
                    notes.add(NoteList(title ?: "", note ?: "", formattedTimestamp ?: ""))
                }
                adapter = NoteAdapter(notes, this)
                recyclerView.adapter = adapter // Set the adapter here
            }
            .addOnFailureListener { exception ->
                Log.e("NoteShow", "Error getting documents: ", exception)
                // Display an error message to the user
            }
    }

    override fun onEditClick(position: Int) {
        val note = adapter.notes[position]
        val intent = Intent(this, Edit::class.java)
        // Pass the note ID or any necessary data to the Edit activity
        intent.putExtra("noteId", note.title)
        startActivity(intent)
    }

    override fun onDeleteClick(position: Int) {
        val note = adapter.notes[position]
        // Delete note from Firestore
        firestore.collection("notes").document(note.title).delete()
            .addOnSuccessListener {
                // Remove note from adapter's list
                val newList = adapter.notes.toMutableList().apply { removeAt(position) }
                adapter.notes = newList
                // Notify adapter that the item is removed
                adapter.notifyItemRemoved(position)
            }
            .addOnFailureListener { exception ->
                // Handle failure
                Log.e("NoteShow", "Error deleting note: $exception")
            }
    }

}
