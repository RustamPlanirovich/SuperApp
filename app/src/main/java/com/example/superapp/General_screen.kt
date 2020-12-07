package com.example.superapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.superapp.Link.Links
import com.example.superapp.Notification.NotificationLog
import com.example.superapp.SmsAndCalls.Call
import com.example.superapp.SmsAndCalls.Sms
import kotlinx.android.synthetic.main.activity_general_screen.*

class General_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_general_screen)

        linkButton.setOnClickListener {
            var intentLink = Intent(this, Links::class.java)
            startActivity(intentLink)
        }
        profile.setOnClickListener {
            var intentLink = Intent(this,ProfileActivity::class.java)
            startActivity(intentLink)
        }

        musicForProgramming.setOnClickListener {
            var intentLink = Intent(this,Music::class.java)
            startActivity(intentLink)
        }

        sms.setOnClickListener {
            var intentLink = Intent(this,Sms::class.java)
            startActivity(intentLink)
        }

        calls.setOnClickListener {
            var intentLink = Intent(this,Call::class.java)
            startActivity(intentLink)
        }

        notification.setOnClickListener {
            var intentLink = Intent(this,NotificationLog::class.java)
            startActivity(intentLink)
        }
    }
}