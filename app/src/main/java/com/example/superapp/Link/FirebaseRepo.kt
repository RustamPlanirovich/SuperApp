package com.example.superapp.Link

import com.example.superapp.dagger.DaggerDaggerComponent
import com.example.superapp.firestore.database
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class FirebaseRepo{
    @Inject
    lateinit var firebaseAuth: database

    fun getUser(): FirebaseUser? {
        DaggerDaggerComponent.create().inject(this)
        return firebaseAuth.firebaseAuth.currentUser
    }
}