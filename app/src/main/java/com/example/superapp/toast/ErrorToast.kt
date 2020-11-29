package com.example.superapp.toast

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.superapp.R
import www.sanju.motiontoast.MotionToast

class ErrorToast {

    fun ErrorToast(title: String, message: String, context: Context) {

        MotionToast.darkToast(
            context as Activity,
            title,
            message,
            MotionToast.TOAST_ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.helvetica_regular)
        )
    }

    fun SuccessToast(title: String, message: String, context: Context) {

        MotionToast.darkToast(
            context as Activity,
            title,
            message,
            MotionToast.TOAST_SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.helvetica_regular)
        )
    }

}