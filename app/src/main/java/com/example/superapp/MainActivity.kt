package com.example.superapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.superapp.dagger.DaggerDaggerComponent
import com.example.superapp.firestore.database
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var firebaseAuth: database
    private var editText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        DaggerDaggerComponent.create().inject(this)

        editText = findViewById(R.id.addressLink)
        findViewById<Button>(R.id.buttonContinue).setOnClickListener(View.OnClickListener {
            val number = editText!!.getText().toString().trim { it <= ' ' }
            if (number.isEmpty() || number.length < 10) {
                editText!!.error = "Invalid Number"
                editText!!.requestFocus()
                return@OnClickListener
            }

            val phoneNumber = "$number"
            val intent = Intent(this@MainActivity, VerifyPhoneActivity::class.java)
            intent.putExtra("phonenumber", phoneNumber)
            startActivity(intent)
        })

    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.firebaseAuth.currentUser != null) {
            val intent = Intent(this, General_screen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}