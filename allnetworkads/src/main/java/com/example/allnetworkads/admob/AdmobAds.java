package com.example.allnetworkads.admob;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.bumptech.glide.Glide;
import com.example.allnetworkads.AdsCounter;
import com.example.allnetworkads.R;
import com.example.allnetworkads.adslib.AdsClick;
import com.example.allnetworkads.adslib.Constants;
import com.example.allnetworkads.adslib.InHouseAds;
import com.example.allnetworkads.adslib.SharedPrefUtils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.textview.MaterialTextView;

public class AdmobAds {

    public static InterstitialAd mInterstitial;
    public static NativeAd nativeAd1;

    public static void loadAdmobInters(Context context) {
        try {
            String interID = "";
            try {
                interID = SharedPrefUtils.getStringData(context, Constants.INTERSTIAL);
                if (interID == null) {
                    interID = "";
                }
            } catch (Exception e) {
                e.printStackTrace();
                interID = "";
            }
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(context, interID, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitial = interstitialAd;
                            Log.d("TAG", "onAdLoaded");
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.d("TAG", loadAdError.getMessage());
                            mInterstitial = null;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void adOnBack(Context context, Activity activity){
        if (AdsCounter.isShowAd(context)) {
            if (mInterstitial != null) {
                mInterstitial.show(activity);
                mInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("TAG", "The ad was dismissed.");
                        activity.finish();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        mInterstitial = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            } else {
                activity.finish();
            }
        }else {
            activity.finish();
        }
    }

    public static void RedirectActivity(Activity fromActivity, Intent intent, boolean isFinish) {
        if (AdsCounter.isShowAd(fromActivity)) {
            if (mInterstitial != null) {
                mInterstitial.show(fromActivity);
                mInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d("TAG", "The ad was dismissed.");
                        fromActivity.startActivity(intent);
                        if(isFinish) {
                            fromActivity.finish();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitial = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });

            } else {
                fromActivity.startActivity(intent);
                if(isFinish) {
                    fromActivity.finish();
                }
            }
        } else {
            fromActivity.startActivity(intent);
            if(isFinish) {
                fromActivity.finish();
            }
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */
    @SuppressLint("MissingPermission")
    public static void refreshAd(Context context, Activity activity, String appName,
                          String pkgName, int isSmallAd, int nativeThemeColor) {
        FrameLayout nativeAds = activity.findViewById(R.id.fl_adplaceholder);
        LinearLayout AdsAreaEmpty = activity.findViewById(R.id.ads_area_empty);
        LinearLayoutCompat inHouseAdArea =  activity.findViewById(R.id.inHouseAd);

        String nativeAdId = SharedPrefUtils
                .getStringData(context, Constants.NATIVE_AD);
        if(nativeAdId==null){
            nativeAdId="no";
        }
        AdLoader.Builder builder = new AdLoader.Builder(context, nativeAdId);

        builder.forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                    // OnLoadedListener implementation.
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.
                        try {
                            boolean isDestroyed = false;

                            isDestroyed = activity.isDestroyed();
                            if (isDestroyed || activity.isFinishing() || activity.isChangingConfigurations()) {
                                nativeAd1.destroy();
                                return;
                            }
                            // You must call destroy on old ads when you are done with them,
                            // otherwise you will have a memory leak.
                            if (nativeAd1 != null) {
                                nativeAd1.destroy();
                            }
                            nativeAd1 = nativeAd;
                            int layout = getNativeLayout(isSmallAd, nativeThemeColor);

                            NativeAdView adView =
                                    (NativeAdView) activity.getLayoutInflater().inflate(layout, null);
                            populateNativeAdView(nativeAd, adView,isSmallAd);
                            nativeAds.removeAllViews();
                            nativeAds.addView(adView);

                            //correct
                            nativeAds.setVisibility(View.VISIBLE);
                            inHouseAdArea.setVisibility(View.GONE);
                            AdsAreaEmpty.setVisibility(View.GONE);

                            //Testing
                      /* showInHouseAds();
                        nativeAds.setVisibility(View.GONE);
                        AdsAreaEmpty.setVisibility(View.GONE);
                        inHouseAdArea.setVisibility(View.VISIBLE);*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }



                    }
                });

        VideoOptions videoOptions =
                new VideoOptions.Builder().setStartMuted(false).build();

        NativeAdOptions adOptions =
                new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader =
                builder
                        .withAdListener(
                                new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                                        if (InHouseAds.getModelAdsList().size() > 0) {
                                            showInHouseAds(context, activity, appName, pkgName, isSmallAd);
                                            nativeAds.setVisibility(View.GONE);
                                            AdsAreaEmpty.setVisibility(View.GONE);
                                            inHouseAdArea.setVisibility(View.VISIBLE);
                                        } else {
                                            nativeAds.setVisibility(View.GONE);
                                            inHouseAdArea.setVisibility(View.GONE);
                                            AdsAreaEmpty.setVisibility(View.VISIBLE);
                                        }
                                    }
                                })
                        .build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private static int getNativeLayout(int isSmallAd, int nativeThemeColor) {
        int layout = R.layout.ad_unified_white;

        if(nativeThemeColor == ENUMS.BLACK) {
            if (isSmallAd == ENUMS.SMALL_ADS) {
                layout = R.layout.ad_unified_black_small;
            } else if (isSmallAd == ENUMS.LARGE_ADS) {
                layout = R.layout.ad_unified_black;
            }
        }
        else if(nativeThemeColor == ENUMS.WHITE){
            if (isSmallAd == ENUMS.SMALL_ADS) {
                layout = R.layout.ad_unified_white_small;
            } else if (isSmallAd == ENUMS.LARGE_ADS) {
                layout = R.layout.ad_unified_white;
            }
        }

        return layout;
    }

    public static void showInHouseAds(Context context, Activity activity, String appName,
                                      String pkgName, int isSmallAd) {

        MaterialTextView adsTitle = activity.findViewById(R.id.adsTitle);
        MaterialTextView adsSubTitle = activity.findViewById(R.id.adsSubTitle);
        MaterialTextView adsTitleSmall = activity.findViewById(R.id.adsTitleSmall);
        MaterialTextView adsRating = activity.findViewById(R.id.adsRating);
        ImageView icon = activity.findViewById(R.id.iconAds);
        ImageView featureGraphic = activity.findViewById(R.id.blurImage);
        Button installBtn = activity.findViewById(R.id.installBtn);
        VideoView ad_mediaVideo = activity.findViewById(R.id.ad_mediaVideo);

        adsSubTitle.setSelected(true);

        String packageName = "";
        try {
            String iconURL = InHouseAds.getModelAdsList().get(0).getAdsIcon();
            String featureGraphicURL = InHouseAds.getModelAdsList().get(0).getAdsImage();
            String videoURL = InHouseAds.getModelAdsList().get(0).getAdsVideo();
            packageName = InHouseAds.getModelAdsList().get(0).getAppPackage();
            boolean isVideoAvailable = InHouseAds.getModelAdsList().get(0).isVideo();

            adsTitle.setText(   InHouseAds.getModelAdsList().get(0).getAdsTitle()); // title
            adsSubTitle.setText(InHouseAds.getModelAdsList().get(0).getAdsSubText());
            adsTitleSmall.setText(InHouseAds.getModelAdsList().get(0).getAdsTitle());
            adsRating.setText(InHouseAds.getModelAdsList().get(0).getAdsRating());

            Glide.with(context)
                    .load(iconURL)
                    .into( icon );

            if (isVideoAvailable) {
                ad_mediaVideo.setVisibility(View.VISIBLE);
                featureGraphic.setVisibility(View.GONE);

                Uri video = Uri.parse(videoURL);
                ad_mediaVideo.setVideoURI(video);
                ad_mediaVideo.start();
                ad_mediaVideo.setOnCompletionListener(mediaPlayer -> {
                    Log.d("TAG", "showInHouseAds: ");
                    ad_mediaVideo.start();
                });

            } else {
                ad_mediaVideo.setVisibility(View.GONE);
                featureGraphic.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(featureGraphicURL)
                        .into( featureGraphic );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(isSmallAd == ENUMS.SMALL_ADS){
            ad_mediaVideo.setVisibility(View.GONE);
            featureGraphic.setVisibility(View.GONE);
        }
        String finalPackageName = packageName;
        installBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + finalPackageName)));
                } catch (Exception e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + finalPackageName)));
                }

                try {
                    AdsClick.onAdClick(context, appName, pkgName,finalPackageName, Constants.NATIVE_AD );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public static void showFragmentInHouseAds(Context context, View view, String appName,
                                              String pkgName, int isSmallAd) {

        MaterialTextView adsTitle = view.findViewById(R.id.adsTitle);
        MaterialTextView adsSubTitle = view.findViewById(R.id.adsSubTitle);
        MaterialTextView adsTitleSmall = view.findViewById(R.id.adsTitleSmall);
        MaterialTextView adsRating = view.findViewById(R.id.adsRating);
        ImageView icon = view.findViewById(R.id.iconAds);
        ImageView featureGraphic = view.findViewById(R.id.blurImage);
        Button installBtn = view.findViewById(R.id.installBtn);
        VideoView ad_mediaVideo = view.findViewById(R.id.ad_mediaVideo);

        adsSubTitle.setSelected(true);

        String packageName = "";
        try {
            String iconURL = InHouseAds.getModelAdsList().get(0).getAdsIcon();
            String featureGraphicURL = InHouseAds.getModelAdsList().get(0).getAdsImage();
            String videoURL = InHouseAds.getModelAdsList().get(0).getAdsVideo();
            packageName = InHouseAds.getModelAdsList().get(0).getAppPackage();
            boolean isVideoAvailable = InHouseAds.getModelAdsList().get(0).isVideo();

            adsTitle.setText(   InHouseAds.getModelAdsList().get(0).getAdsTitle()); // title
            adsSubTitle.setText(InHouseAds.getModelAdsList().get(0).getAdsSubText());
            adsTitleSmall.setText(InHouseAds.getModelAdsList().get(0).getAdsTitle());
            adsRating.setText(InHouseAds.getModelAdsList().get(0).getAdsRating());

            Glide.with(context)
                    .load(iconURL)
                    .into( icon );

            if (isVideoAvailable) {
                ad_mediaVideo.setVisibility(View.VISIBLE);
                featureGraphic.setVisibility(View.GONE);

                Uri video = Uri.parse(videoURL);
                ad_mediaVideo.setVideoURI(video);
                ad_mediaVideo.start();
                ad_mediaVideo.setOnCompletionListener(mediaPlayer -> {
                    Log.d("TAG", "showInHouseAds: ");
                    ad_mediaVideo.start();
                });

            } else {
                ad_mediaVideo.setVisibility(View.GONE);
                featureGraphic.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(featureGraphicURL)
                        .into( featureGraphic );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if(isSmallAd == ENUMS.SMALL_ADS){
            ad_mediaVideo.setVisibility(View.GONE);
            featureGraphic.setVisibility(View.GONE);
        }
        String finalPackageName = packageName;
        installBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + finalPackageName)));
                } catch (Exception e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + finalPackageName)));
                }

                try {
                    AdsClick.onAdClick(context, appName, pkgName,finalPackageName, Constants.NATIVE_AD );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }


    private static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView, int isSmallAd) {
        // Set the media view.
        try {

            if (isSmallAd == ENUMS.LARGE_ADS) {
                adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));
            }
            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

            // The headline and mediaContent are guaranteed to be in every NativeAd.
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            if (isSmallAd == ENUMS.LARGE_ADS) {
                adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        try {
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd);

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            VideoController vc = nativeAd.getMediaContent().getVideoController();

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc.hasVideoContent()) {


                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before
                        // refreshing or replacing them with another ad in the same UI location.

                        super.onVideoEnd();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public static void refreshFragmentAd(Context context, Activity activity, View view, String appName,
                                  String pkgName, int isSmallAd, int nativeThemeColor) {
        FrameLayout nativeAds =view.findViewById(R.id.fl_adplaceholder);
        LinearLayout AdsAreaEmpty = view.findViewById(R.id.ads_area_empty);
        LinearLayoutCompat inHouseAdArea =  view.findViewById(R.id.inHouseAd);
        String nativeAD = "";
        try {
            nativeAD = SharedPrefUtils.getStringData(activity,
                    Constants.NATIVE_AD);
            if (nativeAD == null) {
                nativeAD = "no";
            }
        } catch (Exception e) {
            e.printStackTrace();
            nativeAD = "";
        }
        AdLoader.Builder builder = new AdLoader.Builder(activity, nativeAD);

        builder.forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                    // OnLoadedListener implementation.
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        // If this callback occurs after the activity is destroyed, you must call
                        // destroy and return or you may get a memory leak.
                        try {
                            boolean isDestroyed = false;

                            isDestroyed = activity.isDestroyed();
                            if (isDestroyed || activity.isFinishing() || activity.isChangingConfigurations()) {
                                nativeAd1.destroy();
                                return;
                            }
                            // You must call destroy on old ads when you are done with them,
                            // otherwise you will have a memory leak.
                            if (nativeAd1 != null) {
                                nativeAd1.destroy();
                            }
                            nativeAd1 = nativeAd;
                            int layout = getNativeLayout(isSmallAd, nativeThemeColor);
                            NativeAdView adView =
                                    (NativeAdView) activity.getLayoutInflater().inflate(layout, null);
                            populateNativeAdView(nativeAd, adView,isSmallAd);

                            nativeAds.removeAllViews();
                            nativeAds.addView(adView);

                            //correct
                            nativeAds.setVisibility(View.VISIBLE);
                            inHouseAdArea.setVisibility(View.GONE);
                            AdsAreaEmpty.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        VideoOptions videoOptions =
                new VideoOptions.Builder().setStartMuted(false).build();

        NativeAdOptions adOptions =
                new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader =
                builder
                        .withAdListener(
                                new AdListener() {
                                    @Override
                                    public void onAdFailedToLoad(LoadAdError loadAdError) {

                                        try {
                                            if (InHouseAds.getModelAdsList().size() > 0) {
                                                showFragmentInHouseAds(context, view, appName, pkgName, isSmallAd);
                                                nativeAds.setVisibility(View.GONE);
                                                AdsAreaEmpty.setVisibility(View.GONE);
                                                inHouseAdArea.setVisibility(View.VISIBLE);
                                            } else {
                                                nativeAds.setVisibility(View.GONE);
                                                inHouseAdArea.setVisibility(View.GONE);
                                                AdsAreaEmpty.setVisibility(View.VISIBLE);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                        .build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }
}
