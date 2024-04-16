package com.example.examination

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cloud.model.Product
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
        val editTextProductId = findViewById<EditText>(R.id.editTextProductId)
        val editTextProductName = findViewById<EditText>(R.id.editTextProductName)
        val editTextProductPrice = findViewById<EditText>(R.id.editTextProductPrice)
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        val buttonShow = findViewById<Button>(R.id.buttonShow)

        buttonAdd.setOnClickListener {
            val productId = editTextProductId.text.toString().trim()
            val productName = editTextProductName.text.toString().trim()
            val productPrice = editTextProductPrice.text.toString().trim()

            if (productId.isNotEmpty() && productName.isNotEmpty() && productPrice.isNotEmpty()) {
                val product = Product(productId, productName, productPrice.toDouble())
                addProductToFirestore(product)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        buttonShow.setOnClickListener {
            navigateToProductShow()
        }
    }

    private fun addProductToFirestore(product: Product) {
        val editTextProductId = findViewById<EditText>(R.id.editTextProductId)
        val editTextProductName = findViewById<EditText>(R.id.editTextProductName)
        val editTextProductPrice = findViewById<EditText>(R.id.editTextProductPrice)

        val productsCollection = firestore.collection("products")

        // Set the timestamp field to the current server time
        product.timestamp = FieldValue.serverTimestamp()

        productsCollection.document(product.productId)
            .set(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()

                editTextProductId.text.clear()
                editTextProductName.text.clear()
                editTextProductPrice.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to add product: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(ContentValues.TAG, "Error adding product", e)
            }
    }


    private fun navigateToProductShow() {
        val intent = Intent(this, ProductShow::class.java)
        startActivity(intent)
        finish()
    }

    private fun clearFields() {
        val editTextProductId = findViewById<EditText>(R.id.editTextProductId)
        val editTextProductName = findViewById<EditText>(R.id.editTextProductName)
        val editTextProductPrice = findViewById<EditText>(R.id.editTextProductPrice)

        editTextProductId.text.clear()
        editTextProductName.text.clear()
        editTextProductPrice.text.clear()
    }
}
