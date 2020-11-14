package com.example.superapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.link_add_bottom_sheet.*

class CustomBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {

        const val TAG = "CustomBottomSheetDialogFragment"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.link_add_bottom_sheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme)

//        firstButton.setOnClickListener {
//            //handle click event
//            Toast.makeText(context, "First Button Clicked", Toast.LENGTH_SHORT).show()
//        }
//        secondButton.setOnClickListener {
//            //handle click event
//            Toast.makeText(context, "Second Button Clicked", Toast.LENGTH_SHORT).show()
//        }
//        thirdButton.setOnClickListener {
//            //handle click event
//            Toast.makeText(context, "Third Button Clicked", Toast.LENGTH_SHORT).show()
//        }

    }
}