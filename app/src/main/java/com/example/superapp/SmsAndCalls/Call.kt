package com.example.superapp.SmsAndCalls

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.superapp.R
import kotlinx.android.synthetic.main.activity_sms.*
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class Call : AppCompatActivity() {
    val REQUEST_CODE_ASK_PERMISSIONS = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        editText.movementMethod = ScrollingMovementMethod()
        if (
            ContextCompat.checkSelfPermission(
                getBaseContext(),
                "android.permission.READ_CALL_LOG"
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            button.isEnabled == false

        } else {
            ActivityCompat.requestPermissions(
                this,
                Array<String?>(3) { "android.permission.READ_CALL_LOG" },
                REQUEST_CODE_ASK_PERMISSIONS
            );
        }
        button.setOnClickListener {
            GetSMSList()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun GetSMSList() {

//                val _id: Long = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
//                val number: String = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
//                val date: Long = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE))


        editText.append("\n\n      Звонки")
        editText.append("\n-------------------------------------------------------------")

        val allCalls = Uri.parse("content://call_log/calls")
        val cursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null)

        startManagingCursor(cursor)
        val format1 = SimpleDateFormat("HH:mm:ss dd.MM.yyyy")
        if (cursor?.getCount()!! > 0) {
            while (cursor?.moveToNext()!!) {
                var typeOutpot: String? = null
                val typeInput: Int = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE))
                when (typeInput) {
                    CallLog.Calls.OUTGOING_TYPE -> typeOutpot = "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> typeOutpot = "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> typeOutpot = "MISSED"
                }
                editText.append(
                    """
                 ${format1.format(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)))} 
                 ${cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))}
                 ${cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))}
                 ${convertMillis1(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION)))}
                 ${typeOutpot}
                """
                )
                editText.append("\n")
            }
        }
        editText.append("\n########################################")
    }

    @SuppressLint("DefaultLocale")
    private fun convertMillis1(milliseconds2: Long): kotlin.String {
        val sdfsd = java.lang.String.format(
            "%02d:%02d",
            TimeUnit.SECONDS.toMinutes(milliseconds2) % 60,
            TimeUnit.SECONDS.toSeconds(milliseconds2) % 60
        )
        return sdfsd
    }
}