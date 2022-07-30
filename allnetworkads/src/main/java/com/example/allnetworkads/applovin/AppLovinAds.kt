package com.example.allnetworkads.applovin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.mediation.nativeAds.MaxNativeAdListener
import com.applovin.mediation.nativeAds.MaxNativeAdLoader
import com.applovin.mediation.nativeAds.MaxNativeAdView
import com.example.allnetworkads.adslib.Constants
import com.example.allnetworkads.adslib.SharedPrefUtils
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class AppLovinAds {
    companion object {
        private lateinit var interstitialAd: MaxInterstitialAd
        private var retryAttempt = 0.0

        private lateinit var nativeAdLoader: MaxNativeAdLoader
        private var nativeAd: MaxAd? = null

       // private lateinit var nativeAdLayout: FrameLayout

        fun loadNativeAd(context: Context, nativeAdLayout: FrameLayout) {
           // nativeAdLayout = findViewById(R.id.native_ad_layout)
            val adId = SharedPrefUtils.getStringData(context, Constants.APPLOVIN_NATIVE)
            Log.i("MyLog", "adid: "+adId)
            nativeAdLoader = MaxNativeAdLoader(adId, context)
            nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
                override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {
                    // Cleanup any pre-existing native ad to prevent memory leaks.
                    if (nativeAd != null) {
                        nativeAdLoader.destroy(nativeAd)
                    }

                    // Save ad for cleanup.
                    nativeAd = ad

                    // Add ad view to view.
                    nativeAdLayout.removeAllViews()
                    nativeAdLayout.addView(nativeAdView)
                }

                override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {}
                override fun onNativeAdClicked(ad: MaxAd) {}
            })
            nativeAdLoader.loadAd()
        }

        /*private lateinit var nativeAdLoader: MaxNativeAdLoader
        private lateinit var nativeAdView: MaxNativeAdView
        private lateinit var nativeAdLayout: FrameLayout
        private var nativeAd: MaxAd? = null

        fun loadNativeAd() {
            nativeAdLayout = findViewById(R.id.native_ad_layout)
            //setupCallbacksRecyclerView()

            val binder: MaxNativeAdViewBinder = MaxNativeAdViewBinder.Builder(R.layout.native_custom_layout)
                .setTitleTextViewId(R.id.title_text_view)
                .setBodyTextViewId(R.id.body_text_view)
                .setAdvertiserTextViewId(R.id.advertiser_textView)
                .setIconImageViewId(R.id.icon_image_view)
                .setMediaContentViewGroupId(R.id.media_view_container)
                .setOptionsContentViewGroupId(R.id.ad_options_view)
                .setCallToActionButtonId(R.id.cta_button)
                .build()
            nativeAdView = MaxNativeAdView(binder, this)

            nativeAdLoader = MaxNativeAdLoader(getString(R.string.native_test_id), this)
            nativeAdLoader.setRevenueListener(object : MaxAdRevenueListener {
                override fun onAdRevenuePaid(ad: MaxAd?) {
    //                val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_APPLOVIN_MAX)
    //                adjustAdRevenue.setRevenue(ad?.revenue, "USD")
    //                adjustAdRevenue.setAdRevenueNetwork(ad?.networkName)
    //                adjustAdRevenue.setAdRevenueUnit(ad?.adUnitId)
    //                adjustAdRevenue.setAdRevenuePlacement(ad?.placement)
    //
    //                Adjust.trackAdRevenue(adjustAdRevenue)
                }
            })
            nativeAdLoader.setNativeAdListener(object : MaxNativeAdListener() {
                override fun onNativeAdLoaded(nativeAdView: MaxNativeAdView?, ad: MaxAd) {

                    // Cleanup any pre-existing native ad to prevent memory leaks.
                    if (nativeAd != null) {
                        nativeAdLoader.destroy(nativeAd)
                    }

                    // Save ad for cleanup.
                    nativeAd = ad

                    // Add ad view to view.
                    nativeAdLayout.removeAllViews()
                    nativeAdLayout.addView(nativeAdView)
                }

                override fun onNativeAdLoadFailed(adUnitId: String, error: MaxError) {}
                override fun onNativeAdClicked(ad: MaxAd) {}
            })
        }

        override fun onDestroy() {
            // Must destroy native ad or else there will be memory leaks.
            if (nativeAd != null) {
                // Call destroy on the native ad from any native ad loader.
                nativeAdLoader.destroy(nativeAd)
            }

            // Destroy the actual loader itself
            nativeAdLoader.destroy()

            super.onDestroy()
        }

        fun showAd() {
            nativeAdLoader.loadAd(nativeAdView)
        }*/

        fun loadInterstitialAd(context: Context, activity: Activity) {
            val adId = SharedPrefUtils.getStringData(context, Constants.APPLOVIN_INTER)
            interstitialAd = MaxInterstitialAd(adId, activity )
            // Load the first ad
            interstitialAd.loadAd()
        }

        fun showAd(context: Context, intent: Intent) {
            interstitialAd.setListener( object: MaxAdListener {
                override fun onAdLoaded(ad: MaxAd?) {
                    // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'
                    // Reset retry attempt
                    retryAttempt = 0.0
                }

                override fun onAdDisplayed(ad: MaxAd?) {}
                override fun onAdClicked(ad: MaxAd?) {}

                override fun onAdHidden(ad: MaxAd?) {
                    // Interstitial ad is hidden. Pre-load the next ad
                    interstitialAd.loadAd()
                    context.startActivity(intent)
                }

                override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
                    // Interstitial ad failed to load
                    // AppLovin recommends that you retry with exponentially higher delays up to a maximum delay (in this case 64 seconds)

                    retryAttempt++
                    val delayMillis = TimeUnit.SECONDS.toMillis( 2.0.pow(6.0.coerceAtMost(
                        retryAttempt
                    )).toLong() )

                    Handler().postDelayed( { interstitialAd.loadAd() }, delayMillis )
                }

                override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
                    // Interstitial ad failed to display. AppLovin recommends that you load the next ad.
                    interstitialAd.loadAd()
                }
            } )

            if (interstitialAd.isReady )
                interstitialAd.showAd()
            else
                context.startActivity(intent)
        }
    }
}