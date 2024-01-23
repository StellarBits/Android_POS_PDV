package com.stellarbitsapps.androidpdv.ui.custom.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.stellarbitsapps.androidpdv.R

class ProgressHUD(context: Context?, theme: Int) : Dialog(context!!, theme) {

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val imageView = findViewById<View>(R.id.spinnerImageView) as ImageView
        val spinner = imageView.background as AnimationDrawable
        spinner.start()
    }

    companion object {
        fun show(
            context: Context?,
            message: CharSequence?,
            cancelable: Boolean,
            spinnerGone: Boolean
        ): ProgressHUD {
            val dialog = ProgressHUD(context!!, R.style.ProgressHUD)
            dialog.setTitle("")
            dialog.setContentView(R.layout.progress_hud)

            val txt = dialog.findViewById<View>(R.id.message) as TextView

            if (message.isNullOrEmpty()) {
                txt.visibility = View.GONE
            } else {
                txt.text = message
            }

            val spinner = dialog.findViewById<View>(R.id.spinnerImageView) as ImageView
            spinner.visibility = if (spinnerGone) View.GONE else View.VISIBLE

            dialog.setCancelable(cancelable)
            dialog.window!!.attributes.gravity = Gravity.CENTER

            // Opacity control
            val lp = dialog.window!!.attributes
            lp.dimAmount = 0.2f

            dialog.window!!.attributes = lp
            dialog.show()

            return dialog
        }
    }
}