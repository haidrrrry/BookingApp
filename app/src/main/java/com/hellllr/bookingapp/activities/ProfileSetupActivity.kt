package com.hellllr.bookingapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hellllr.bookingapp.MainActivity
import com.hellllr.bookingapp.R

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var nameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var adressEditText: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        nameEditText = findViewById(R.id.nameEditText)
        bioEditText = findViewById(R.id.bioEditText)
        adressEditText = findViewById(R.id.adressEditText)
        submitButton = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val name = nameEditText.text.toString()
            val bio = bioEditText.text.toString()
            val adress = adressEditText.text.toString()

            if (name.isEmpty() || bio.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return
            }

            val profileData = mapOf(
                "name" to name,
                "bio" to bio,
                "adress" to adress
            )

            db.collection("usersbio").document(userId)
                .set(profileData)
                .addOnSuccessListener {
                    Log.d("ProfileSetupActivity", "Profile saved successfully")
                    // Save in shared preferences
                    val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("profileCompleted", true).apply()

                    // Navigate to MainActivity
                    val intent = Intent(this@ProfileSetupActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileSetupActivity", "Error saving profile", e)
                    Toast.makeText(this, "Failed to save profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
            Log.e("ProfileSetupActivity", "User ID is null")
        }
    }
}
