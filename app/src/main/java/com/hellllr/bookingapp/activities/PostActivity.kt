package com.hellllr.bookingapp.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.hellllr.bookingapp.MainActivity
import com.hellllr.bookingapp.databinding.ActivityPostBinding
import java.util.UUID

class PostActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

private var selectedImageUri: Uri? = null
    private lateinit var binding: ActivityPostBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

       binding.saveButton .setOnClickListener {
            uploadPost()
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
           binding. imageView.setImageURI(selectedImageUri)
        }
    }

    private fun uploadPost() {
        val description = binding.descriptionEditText.text.toString().trim()
        val userId = auth.currentUser?.uid

        if (description.isNotEmpty() && selectedImageUri != null && userId != null) {
            val fileName = UUID.randomUUID().toString()
            val storageRef = storage.reference.child("images/$fileName")

            storageRef.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val post = hashMapOf(
                            "userId" to userId,
                            "description" to description,
                            "imageUrl" to uri.toString(),
                            "timestamp" to System.currentTimeMillis()
                        )

                        db.collection("posts")
                            .add(post)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Post successful!", Toast.LENGTH_SHORT).show()
                                binding.descriptionEditText.text.clear()
                              binding.  imageView.setImageResource(android.R.color.darker_gray)
                                selectedImageUri = null
                                val i=Intent(this, MainActivity::class.java)
                                startActivity(i)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.d("helll","Failed to post: ${e.message}")
                                Toast.makeText(this, "Failed to post: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please enter a description and select an image.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}