package com.example.allnetworkadslibrary

import android.app.Application
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()

        // Make sure to set the mediation provider value to "max" to ensure proper functionality
        AppLovinSdk.getInstance( applicationContext ).mediationProvider = "max"
        AppLovinSdk.getInstance( applicationContext ).initializeSdk { _:
                                                                      AppLovinSdkConfiguration ->
            // AppLovin SDK is initialized, start loading ads
        }
        //AppLovinSdk.getInstance(this).showMediationDebugger()
    }

}