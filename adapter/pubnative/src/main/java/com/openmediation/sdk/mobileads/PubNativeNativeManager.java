// Copyright 2020 ADTIMING TECHNOLOGY COMPANY LIMITED
// Licensed under the GNU Lesser General Public License Version 3

package com.openmediation.sdk.mobileads;

import android.app.Application;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crosspromotion.sdk.utils.Cache;
import com.crosspromotion.sdk.utils.ImageUtils;
import com.crosspromotion.sdk.utils.ResDownloader;
import com.openmediation.sdk.mediation.AdapterErrorBuilder;
import com.openmediation.sdk.mediation.MediationInfo;
import com.openmediation.sdk.mediation.NativeAdCallback;
import com.openmediation.sdk.nativead.AdIconView;
import com.openmediation.sdk.nativead.AdInfo;
import com.openmediation.sdk.nativead.MediaView;
import com.openmediation.sdk.nativead.NativeAdView;
import com.openmediation.sdk.utils.AdLog;
import com.openmediation.sdk.utils.IOUtil;
import com.openmediation.sdk.utils.WorkExecutor;

import net.pubnative.lite.sdk.models.NativeAd;

import java.io.File;
import java.util.Map;

public class PubNativeNativeManager {

    private static class Holder {
        private static final PubNativeNativeManager INSTANCE = new PubNativeNativeManager();
    }

    private PubNativeNativeManager() {
    }

    public static PubNativeNativeManager getInstance() {
        return PubNativeNativeManager.Holder.INSTANCE;
    }

    public void initAd(Application application, Map<String, Object> extras, final NativeAdCallback callback) {
        String appKey = (String) extras.get("AppKey");
        PubNativeSingleTon.getInstance().init(application, appKey, new PubNativeSingleTon.InitListener() {
            @Override
            public void initSuccess() {
                if (callback != null) {
                    callback.onNativeAdInitSuccess();
                }
            }

            @Override
            public void initFailed(String error) {
                if (callback != null) {
                    callback.onNativeAdInitFailed(AdapterErrorBuilder.buildInitError(
                            AdapterErrorBuilder.AD_UNIT_NATIVE, "PubNativeAdapter", error));
                }
            }
        });
    }

    public void loadAd(String adUnitId, Map<String, Object> extras, final NativeAdCallback callback) {
        final NativeAd nativeAd = PubNativeSingleTon.getInstance().getNativeAd(adUnitId);
        if (nativeAd == null) {
            String error = PubNativeSingleTon.getInstance().getError(adUnitId);
            if (TextUtils.isEmpty(error)) {
                error = "No Fill";
            }
            if (callback != null) {
                callback.onNativeAdLoadFailed(AdapterErrorBuilder.buildLoadError(
                        AdapterErrorBuilder.AD_UNIT_NATIVE, "PubNativeAdapter", error));
            }
            return;
        }
        WorkExecutor.execute(new Runnable() {
            @Override
            public void run() {
                downloadRes(nativeAd, callback);
            }
        });
    }

    public void registerNativeView(String adUnitId, NativeAdView adView, final NativeAdCallback callback) {
        try {
            final NativeAd nativeAd = PubNativeSingleTon.getInstance().getNativeAd(adUnitId);
            if (nativeAd == null) {
                return;
            }
            if (!TextUtils.isEmpty(nativeAd.getBannerUrl()) && adView.getMediaView() != null) {
                MediaView mediaView = adView.getMediaView();
                mediaView.removeAllViews();

                ImageView adnMediaView = new ImageView(adView.getContext());
                mediaView.addView(adnMediaView);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                adnMediaView.setLayoutParams(layoutParams);
                Bitmap content = ImageUtils.getBitmap(IOUtil.getFileInputStream(Cache.getCacheFile(adView.getContext(),
                        nativeAd.getBannerUrl(), null)));
                adnMediaView.setImageBitmap(content);
            }

            if (!TextUtils.isEmpty(nativeAd.getIconUrl()) && adView.getAdIconView() != null) {
                AdIconView iconView = adView.getAdIconView();
                iconView.removeAllViews();
                ImageView adnIconView = new ImageView(adView.getContext());
                iconView.addView(adnIconView);
                adnIconView.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;
                adnIconView.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
                Bitmap content = ImageUtils.getBitmap(IOUtil.getFileInputStream(Cache.getCacheFile(adView.getContext(),
                        nativeAd.getIconUrl(), null)));
                adnIconView.setImageBitmap(content);
            }
            NativeAd.Listener listener = new NativeAd.Listener() {
                @Override
                public void onAdImpression(NativeAd ad, View view) {

                }

                @Override
                public void onAdClick(NativeAd ad, View view) {
                    if (callback != null) {
                        callback.onNativeAdAdClicked();
                    }
                }
            };
            if (adView.getCallToActionView() != null) {
                nativeAd.startTracking(adView.getCallToActionView(), listener);
            } else {
                nativeAd.startTracking(adView, listener);
            }
        } catch(Throwable ignored) {
        }
    }

    public void destroyAd(String adUnitId) {
        PubNativeSingleTon.getInstance().destroyNativeAd(adUnitId);
    }

    private void downloadRes(NativeAd ad, NativeAdCallback callback) {
        try {
            if (!TextUtils.isEmpty(ad.getBannerUrl())) {
                File file = ResDownloader.downloadFile(ad.getBannerUrl());
                if (file == null || !file.exists()) {
                    if (callback != null) {
                        callback.onNativeAdLoadFailed(AdapterErrorBuilder.buildLoadCheckError(
                                AdapterErrorBuilder.AD_UNIT_NATIVE, "PubNativeAdapter", "NativeAd Load Failed"));
                    }
                    return;
                }
                AdLog.getSingleton().LogD("PubNativeNative", "Content File = " + file);
            }
            if (!TextUtils.isEmpty(ad.getIconUrl())) {
                File file = ResDownloader.downloadFile(ad.getIconUrl());
                if (file == null || !file.exists()) {
                    if (callback != null) {
                        callback.onNativeAdLoadFailed(AdapterErrorBuilder.buildLoadCheckError(
                                AdapterErrorBuilder.AD_UNIT_NATIVE, "PubNativeAdapter", "NativeAd Load Failed"));
                    }
                    return;
                }
                AdLog.getSingleton().LogD("PubNativeNative", "Icon File = " + file);
            }
            AdInfo adInfo = new AdInfo();
            adInfo.setDesc(ad.getDescription());
            adInfo.setType(MediationInfo.MEDIATION_ID_23);
            adInfo.setTitle(ad.getTitle());
            adInfo.setCallToActionText(ad.getCallToActionText());
            adInfo.setStarRating(ad.getRating());
            if (callback != null) {
                callback.onNativeAdLoadSuccess(adInfo);
            }
        } catch(Exception e) {
            if (callback != null) {
                callback.onNativeAdLoadFailed(AdapterErrorBuilder.buildLoadError(
                        AdapterErrorBuilder.AD_UNIT_NATIVE, "PubNativeAdapter", "NativeAd Load Failed: " + e.getMessage()));
            }
        }
    }

}
