package com.example.superapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_general_screen.*

class General_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_screen)

        linkButton.setOnClickListener {
            var intentLink = Intent(this,Links::class.java)
            startActivity(intentLink)
        }
        profile.setOnClickListener {
            var intentLink = Intent(this,ProfileActivity::class.java)
            startActivity(intentLink)
        }
    }
}