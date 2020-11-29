package com.example.superapp

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.superapp.dagger.DaggerDaggerComponent
import com.example.superapp.firestore.database
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    //Инжектим firebaseAuth для проверки авторизации
    @Inject
    lateinit var firebaseAuth: database


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Скрываем ActionBar
        supportActionBar?.hide()
        //Принудительно устанавливаем темную тему
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        //Создаем инжект даггера
        DaggerDaggerComponent.create().inject(this)


        buttonContinue.setOnClickListener OnClickListener@{
            val number = addressLink!!.getText().toString().trim { it <= ' ' }
            if (number.isEmpty() || number.length < 10) {
                addressLink!!.error = "Invalid Number"
                addressLink!!.requestFocus()
                return@OnClickListener
            }
            val phoneNumber = number
            val intent = Intent(this@MainActivity, VerifyPhoneActivity::class.java)
            intent.putExtra("phonenumber", phoneNumber)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        //Проверяем зарегистрирован ли пользователь в Firebase Autentification
        if (firebaseAuth.firebaseAuth.currentUser != null) {
            //Если да то запускаем основное активити General_screen
            val intent = Intent(this, General_screen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}