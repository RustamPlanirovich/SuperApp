package com.example.superapp.SmsAndCalls

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.superapp.R
import kotlinx.android.synthetic.main.activity_sms.*
import java.text.SimpleDateFormat


class Sms : AppCompatActivity() {
    val REQUEST_CODE_ASK_PERMISSIONS = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms)
        editText.movementMethod = ScrollingMovementMethod()
        if (ContextCompat.checkSelfPermission(
                getBaseContext(),
                "android.permission.READ_SMS"
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            button.isEnabled == false

        } else {
            ActivityCompat.requestPermissions(
                this,
                Array<String?>(3) { "android.permission.READ_SMS" },
                REQUEST_CODE_ASK_PERMISSIONS
            );
        }
        button.setOnClickListener {
            GetSMSList()
        }


    }


    fun GetSMSList() {

        editText.append("\n\n      SMS сообщения")
        editText.append("\n-------------------------------------------------------------")
        val uriSms: Uri = Uri.parse("content://sms/")
        val context: Context = this
        val cur: Cursor? = context.getContentResolver().query(uriSms, null, null, null, null)
        startManagingCursor(cur)
        val format1 = SimpleDateFormat("HH:mm:ss dd.MM.yyyy")
        if (cur?.getCount()!! > 0) {
            while (cur?.moveToNext()!!) {
                editText.append(
                    """
                ${format1.format(cur?.getLong(4)).toString()}
                 ${cur?.getString(2).toString()}: 
                 ${cur?.getString(12)}"""
                )
                editText.append("\n")
            }
        }
        editText.append("\n########################################")
    }
}