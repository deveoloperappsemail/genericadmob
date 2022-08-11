package com.example.allnetworkads.adslib

import android.app.Activity
import android.app.Dialog
import android.view.Window
import com.example.allnetworkads.R

class InHouseInterAds {
    companion object {
        fun showInHouseInterAd(activity: Activity) {
            val dialog = Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_interstitial_layout)
            dialog.show()
        }
    }
}