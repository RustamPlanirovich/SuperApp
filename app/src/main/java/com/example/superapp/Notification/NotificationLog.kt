package com.example.superapp.Notification

import android.app.Notification
import android.app.NotificationManager
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.superapp.R


var mInfoTextView: TextView? = null
var image: ImageView? = null
var image1: ImageView? = null
class NotificationLog : AppCompatActivity() {


    private var mReceiver: NotificationBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_log)

        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }



        mInfoTextView = findViewById<View>(R.id.textView) as TextView
        image = findViewById<View>(R.id.imageView5) as ImageView
        image1 = findViewById<View>(R.id.imageView6) as ImageView
        mReceiver = NotificationBroadcastReceiver()

        val filter = IntentFilter()
        filter.addAction("com.example.superapp.NOTIFICATION_LISTENER_EXAMPLE")
        registerReceiver(mReceiver, filter)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    fun onButtonClicked(view: View) {
        if (view.id == R.id.buttonCreateNotification) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val builder: Notification.Builder = Notification.Builder(this)
            builder.setContentTitle("Важное уведомление")
            builder.setContentText("Пора кормить кота!")
            builder.setTicker("Хозяин, проснись!")
            builder.setSmallIcon(R.drawable.ic_qr_code__1_)
            builder.setAutoCancel(true)
            builder.setLargeIcon(
                BitmapFactory.decodeResource(
                    this.getResources(),
                    R.mipmap.ic_launcher
                )
            )
            manager.notify(System.currentTimeMillis().toInt(), builder.build())
        } else if (view.id == R.id.buttonClearNotification) {
            val intent = Intent("com.example.superapp.NOTIFICATION_LISTENER_SERVICE_EXAMPLE")
            intent.putExtra("command", "clearall")
            sendBroadcast(intent)
        } else if (view.id == R.id.buttonListNotification) {
            val intent = Intent("com.example.superapp.NOTIFICATION_LISTENER_SERVICE_EXAMPLE")
            intent.putExtra("command", "list")
            sendBroadcast(intent)
        }
    }

    class NotificationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val temp = intent.getStringExtra("notification_event") + "\\n" + mInfoTextView?.getText()
            mInfoTextView?.setText(temp)

           var byteArray = intent.getByteArrayExtra("notification_event1")
            var bmp: Bitmap? = null
            if (byteArray != null){
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

            }
            image?.setImageBitmap(bmp)

            var byteArray1 = intent.getByteArrayExtra("notification_event2")
            var bmp1: Bitmap? = null
            if (byteArray1 != null){
                bmp1 = BitmapFactory.decodeByteArray(byteArray1, 0, byteArray1.size)

            }
            image1?.setImageBitmap(bmp1)
        }
    }
}