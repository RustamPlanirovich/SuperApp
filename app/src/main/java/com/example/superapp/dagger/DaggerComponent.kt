package com.example.superapp.dagger

import com.example.superapp.FirebaseRepo
import com.example.superapp.Links
import com.example.superapp.MainActivity
import com.example.superapp.VerifyPhoneActivity
import com.example.superapp.firestore.database
import dagger.Component

@Component
interface DaggerComponent {
    fun getDatabase(): database

    fun inject(act: Links)
    fun inject(act: FirebaseRepo)
    fun inject(act: VerifyPhoneActivity)
    fun inject(act: MainActivity)

}