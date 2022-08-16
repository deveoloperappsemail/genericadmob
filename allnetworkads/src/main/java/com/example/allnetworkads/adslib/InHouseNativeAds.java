package com.example.allnetworkads.adslib;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.allnetworkads.R;
import com.example.allnetworkads.admob.ENUMS;
import com.google.android.material.textview.MaterialTextView;

public class InHouseNativeAds {
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
}
