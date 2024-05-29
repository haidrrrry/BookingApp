package com.hellllr.bookingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bhelllr.eventsapp.fragments.PostQueriesFragment
import com.bhelllr.eventsapp.fragments.ProfileFragment
import com.bhelllr.eventsapp.fragments.UserBookingsFragment

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hellllr.bookingapp.activities.PostActivity

class MainActivity : AppCompatActivity() {
    private lateinit var postbtn: FloatingActionButton
    private lateinit var bottonNavBar: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        bottonNavBar=findViewById(R.id.bottom_nav_barr_home)
        postbtn=findViewById(R.id.postbtn)
        postbtn.setOnClickListener {
            val i=Intent(this@MainActivity, PostActivity::class.java)
            startActivity(i)
        }

        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val showQuestionnaire = intent.getBooleanExtra("showQuestionnaire", false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid
        if (userId != null) {

                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout_home, HomeFragment())
                    .commit()

        } else {
            // Handle case where user is not authenticated
        }
//        setCurrentFragment(HomeFragment())
        bottonNavBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.mHome->

                {
                    setCurrentFragment(HomeFragment())

                    postbtn.visibility= View.VISIBLE
                }
                R.id.userprofile->

                {
                    postbtn.visibility= View.GONE
                  setCurrentFragment(UserBookingsFragment())
                }
                R.id.bookingqueries->

                {
                    postbtn.visibility= View.GONE
                  setCurrentFragment(PostQueriesFragment())
                }
                R.id.profilesetting->

                {
                    postbtn.visibility= View.GONE
                  setCurrentFragment(ProfileFragment())
                }



            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout_home,fragment)
            commit()
        }
}