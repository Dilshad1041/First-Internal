package com.example.examination

import NoteShow
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.examination.model.Note
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Home : AppCompatActivity() {
    private lateinit var firestore: FirebaseFirestore

    // Define a constant for logging tag
    companion object {
        private const val TAG = "Home"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initializeViews()
        firestore = FirebaseFirestore.getInstance()
    }

    private fun initializeViews() {
        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextNote = findViewById<EditText>(R.id.editTextNote)
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        val buttonShow = findViewById<Button>(R.id.buttonShow)

        buttonAdd.setOnClickListener {
            val noteTtle = editTextTitle.text.toString().trim()
            val note = editTextNote.text.toString().trim()


            if (noteTtle.isNotEmpty() && note.isNotEmpty() ){
                val product = Note(noteTtle, note)
                addProductToFirestore(product)

            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        buttonShow.setOnClickListener {
            navigateToNoteShow()
        }
    }

    private fun addProductToFirestore(product: Note) {
        val editTextTitle = findViewById<EditText>(R.id.editTextTitle)
        val editTextNote = findViewById<EditText>(R.id.editTextNote)


        val productsCollection = firestore.collection("notes") // Update collection name to "notes"


        // Set the timestamp field to the current server time
        product.timestamp = FieldValue.serverTimestamp()

        productsCollection.document(product.title)
            .set(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product saved successfully", Toast.LENGTH_SHORT).show()

                editTextTitle.text.clear()
                editTextNote.text.clear()

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save product: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(ContentValues.TAG, "Error saving product", e)
            }
    }

//    private fun editNote(product: Note) {
//        val productsCollection = firestore.collection("notes")
//
//        // Set the timestamp field to the current server time
//        product.timestamp = FieldValue.serverTimestamp()
//
//        productsCollection.document(product.title)
//            .set(product)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(this, "Failed to update product: ${e.message}", Toast.LENGTH_SHORT).show()
//                Log.e(TAG, "Error updating product", e)
//            }
//    }



    private fun navigateToNoteShow() {
        val intent = Intent(this, NoteShow::class.java)
        startActivity(intent)
    }


}
