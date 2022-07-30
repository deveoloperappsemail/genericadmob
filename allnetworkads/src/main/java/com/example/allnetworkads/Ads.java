package com.example.allnetworkads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.allnetworkads.admob.AdmobAds;
import com.example.allnetworkads.adslib.Constants;
import com.example.allnetworkads.adslib.SharedPrefUtils;
import com.example.allnetworkads.applovin.AppLovinAds;

public class Ads {

    public static void loadNative(Context context, Activity activity, View view, String appName,
                                  String pkgName, boolean isSmallAd, int nativeTheme,
                                  boolean isFragment) {
        boolean showAdmob = SharedPrefUtils.getBooleanData(context, Constants.SHOW_ADMOB);
        if(showAdmob) {
            if(isFragment)
                AdmobAds.refreshFragmentAd(context, activity, view, appName, pkgName, isSmallAd, nativeTheme);
            else
                AdmobAds.refreshAd(context, activity, appName, pkgName, isSmallAd, nativeTheme);
        }
        else {
            if(isFragment)
                AppLovinAds.Companion.loadFragmentNativeAd(context, view, appName, pkgName, isSmallAd);
            else
                AppLovinAds.Companion.loadNativeAd(context, activity, appName, pkgName, isSmallAd);
        }
    }

    public static void loadInter(Context context, Activity activity) {
        boolean showAdmob = SharedPrefUtils.getBooleanData(context, Constants.SHOW_ADMOB);
        if(showAdmob)
            AdmobAds.loadAdmobInters(context);
        else
            AppLovinAds.Companion.loadInterstitialAd(context, activity);
    }

    public static void showInter(Context context, Activity activity, Intent intent, boolean isFinish) {
        boolean showAdmob = SharedPrefUtils.getBooleanData(context, Constants.SHOW_ADMOB);
        if(showAdmob)
            AdmobAds.RedirectActivity(activity, intent, isFinish);
        else
            AppLovinAds.Companion.showAd(context, intent, isFinish);
    }
}
