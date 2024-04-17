package com.example.examination

import NoteShow
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.examination.model.Note
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class Edit : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        firestore = FirebaseFirestore.getInstance()

        val TextTitle = findViewById<EditText>(R.id.TextTitle)
        val TextNote = findViewById<EditText>(R.id.TextNote)
        val buttonSave = findViewById<Button>(R.id.buttonSave)
        val buttonShow2 = findViewById<Button>(R.id.buttonShow2)


        buttonSave.setOnClickListener {
            val noteTtle = TextTitle.text.toString().trim()
            val note = TextNote.text.toString().trim()


            if (noteTtle.isNotEmpty() && note.isNotEmpty() ){
                val product = Note(noteTtle, note)
                updateNoteInFirestore(product)

            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        buttonShow2.setOnClickListener {
            navigateToNoteShow()
        }

    }


    private fun updateNoteInFirestore(product: Note) {
        val productsCollection = firestore.collection("notes")

        // Set the timestamp field to the current server time
        product.timestamp = FieldValue.serverTimestamp()

        val updateData = mapOf(
            "title" to product.title,
            "note" to product.note,
            "timestamp" to product.timestamp
        )

        productsCollection.document(product.title)
            .update(updateData)
            .addOnSuccessListener {
                Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update product: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error updating product", e)
            }
    }


    private fun navigateToNoteShow() {
        val intent = Intent(this, NoteShow::class.java)
        startActivity(intent)
    }


}