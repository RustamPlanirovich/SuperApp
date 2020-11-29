package com.example.superapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper.create
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.custom_dialog_fragment.*

class MyCustomDialog(
    commentLink: String,
    linkAddress: String,
    applicationContext: Context
) : DialogFragment() {

    var comment = commentLink
    var adress = linkAddress
    var con = applicationContext

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.color.transparent)
        return inflater.inflate(R.layout.custom_dialog_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window?.setLayout(height, ViewGroup.LayoutParams.WRAP_CONTENT)
        var commentText = view?.findViewById<TextView>(R.id.commentText)
        commentText?.movementMethod = ScrollingMovementMethod()
        commentText?.text = comment
        closeButton.setOnClickListener {
            dismiss()
        }

        Glide.with(con)
            .load(create("Link: " + adress + " Comments: " + comment))
            .into(QR)

        QR.setOnClickListener {
            val intent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, comment)
                this.type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, resources.getText(R.string.send_to)))
        }
    }

    fun create(text: String): Bitmap? {
        val writer = QRCodeWriter()
        return try {
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(
                        x,
                        y,
                        if (bitMatrix.get(x, y)) Color.parseColor("#8e24aa") else Color.TRANSPARENT
                    )
                }
            }
            bmp

        } catch (e: WriterException) {
            null
        }
    }

}