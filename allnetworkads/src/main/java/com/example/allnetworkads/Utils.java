package com.example.allnetworkads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import com.example.allnetworkads.adslib.Constants;
import com.example.allnetworkads.adslib.InHouseAds;
import com.example.allnetworkads.adslib.SharedPrefUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.textview.MaterialTextView;


public class Utils {
    static NativeAd nativeAd1;

    @SuppressLint("MissingPermission")
    public static void refreshAdForAdapter(Context context, View itemView,boolean isSmallAd) {

        try {
//        Activity activity = (Activity) context;
            FrameLayout nativeAds = itemView.findViewById(R.id.fl_adplaceholder);
            LinearLayout AdsAreaEmpty = itemView.findViewById(R.id.ads_area_empty);
            LinearLayoutCompat inHouseAdArea =  itemView.findViewById(R.id.inHouseAd);


            String nativeAD =  SharedPrefUtils.getStringData(context, Constants.NATIVE_AD);
            if (nativeAD == null) {
                nativeAD = "no";
            }
            AdLoader.Builder builder = new AdLoader.Builder(context, nativeAD);

            builder.forNativeAd(
                    new NativeAd.OnNativeAdLoadedListener() {
                        // OnLoadedListener implementation.
                        @Override
                        public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                            // If this callback occurs after the activity is destroyed, you must call
                            // destroy and return or you may get a memory leak.
                            try {
                                boolean isDestroyed = false;

                               /* isDestroyed = activity.isDestroyed();
                                if (isDestroyed || activity.isFinishing() || activity.isChangingConfigurations()) {
                                    nativeAd1.destroy();
                                    return;
                                }*/
                                // You must call destroy on old ads when you are done with them,
                                // otherwise you will have a memory leak.
                                if (nativeAd1 != null) {
                                    nativeAd1.destroy();
                                }
                                nativeAd1 = nativeAd;
    //                            NativeAdView adView =
    //                                    (NativeAdView) activity.getLayoutInflater().inflate(R.layout.ad_unified, null);

                                int layout;
                                if (isSmallAd) {
                                    layout = R.layout.ad_unified_white_small;
                                } else {
                                    layout = R.layout.ad_unified_white;
                                }
                                NativeAdView adView = (NativeAdView)  LayoutInflater.from(context)
                                        .inflate(layout, null);
                                populateNativeAdView(nativeAd, adView, isSmallAd);

                                nativeAds.removeAllViews();
                                nativeAds.addView(adView);

                                //correct
                                nativeAds.setVisibility(View.VISIBLE);
                                inHouseAdArea.setVisibility(View.GONE);
                                AdsAreaEmpty.setVisibility(View.GONE);
                                //Testing
                                //                        showInHouseAds();
                                //                        nativeAds.setVisibility(View.GONE);
                                //                        AdsAreaEmpty.setVisibility(View.GONE);
                                //                        inHouseAdArea.setVisibility(View.VISIBLE);
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
                                                showInHouseAdsForAdapter(context,itemView, isSmallAd);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void showInHouseAdsForAdapter(Context activity, View itemView, boolean isSmallAd) {

        MaterialTextView adsTitle = itemView.findViewById(R.id.adsTitle);
        MaterialTextView adsSubTitle = itemView.findViewById(R.id.adsSubTitle);
        MaterialTextView adsTitleSmall = itemView.findViewById(R.id.adsTitleSmall);
        MaterialTextView adsRating =itemView. findViewById(R.id.adsRating);
        ImageView icon = itemView.findViewById(R.id.iconAds);
        ImageView featureGraphic = itemView.findViewById(R.id.blurImage);
        Button installBtn =itemView. findViewById(R.id.installBtn);
        VideoView ad_mediaVideo = itemView.findViewById(R.id.ad_mediaVideo);

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

            Glide.with(activity)
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
                Glide.with(activity)
                        .load(featureGraphicURL)
                        .into( featureGraphic );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(isSmallAd){
            ad_mediaVideo.setVisibility(View.GONE);
            featureGraphic.setVisibility(View.GONE);
        }

        String finalPackageName = packageName;
        installBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + finalPackageName)));
                } catch (Exception e) {
                    activity. startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + finalPackageName)));
                }
            }
        });
    }

    private static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView, boolean isSmallAd) {
        // Set the media view.

        if (!isSmallAd) {
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
        if (!isSmallAd) {
            adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
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
}
