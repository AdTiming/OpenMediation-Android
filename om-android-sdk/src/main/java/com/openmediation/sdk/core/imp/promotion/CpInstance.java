// Copyright 2019 ADTIMING TECHNOLOGY COMPANY LIMITED
// Licensed under the GNU Lesser General Public License Version 3

package com.openmediation.sdk.core.imp.promotion;

import android.app.Activity;

import com.openmediation.sdk.core.InsManager;
import com.openmediation.sdk.core.runnable.LoadTimeoutRunnable;
import com.openmediation.sdk.mediation.AdapterError;
import com.openmediation.sdk.mediation.AdapterErrorBuilder;
import com.openmediation.sdk.mediation.PromotionAdCallback;
import com.openmediation.sdk.promotion.PromotionAdRect;
import com.openmediation.sdk.utils.DeveloperLog;
import com.openmediation.sdk.utils.error.ErrorCode;
import com.openmediation.sdk.utils.model.BaseInstance;
import com.openmediation.sdk.utils.model.Scene;

import java.util.Map;

/**
 * CpInstance
 */
public class CpInstance extends BaseInstance implements PromotionAdCallback, LoadTimeoutRunnable.OnLoadTimeoutListener {

    private CpManagerListener mListener;

    public CpInstance() {
    }

    void initCp(Activity activity) {
        setMediationState(MEDIATION_STATE.INIT_PENDING);
        if (mAdapter != null) {
            mAdapter.initPromotionAd(activity, InsManager.getInitDataMap(this), this);
            InsManager.onInsInitStart(this);
        }
    }

    void loadCp(Activity activity, Map<String, Object> extras) {
        setMediationState(MEDIATION_STATE.LOAD_PENDING);
        if (mAdapter != null) {
            DeveloperLog.LogD("load PromotionAd : " + getMediationId() + " key : " + getKey());
            InsManager.startInsLoadTimer(this, this);
            mLoadStart = System.currentTimeMillis();
            mAdapter.loadPromotionAd(activity, getKey(), extras, this);
        }
    }

    void showCp(Activity activity, PromotionAdRect rect, Scene scene) {
        if (mAdapter != null) {
            mAdapter.showPromotionAd(activity, getKey(), PromotionAdRect.getExtraData(rect), this);
            InsManager.onInsShow(this, scene);
        }
    }

    void hideCp() {
        if (mAdapter != null) {
            mAdapter.hidePromotionAd(getKey(), this);
        }
    }

    boolean isCpAvailable() {
        return mAdapter != null && mAdapter.isPromotionAdAvailable(getKey())
                && getMediationState() == MEDIATION_STATE.AVAILABLE;
    }

    void setCpManagerListener(CpManagerListener listener) {
        mListener = listener;
    }

    @Override
    public void onPromotionAdInitSuccess() {
        InsManager.onInsInitSuccess(this);
        mListener.onPromotionAdInitSuccess(this);
    }

    @Override
    public void onPromotionAdInitFailed(AdapterError error) {
        InsManager.onInsInitFailed(this, error);
        mListener.onPromotionAdInitFailed(this, error);
    }

    @Override
    public void onPromotionAdShowSuccess() {
        mListener.onPromotionAdShowSuccess(this);
    }

    @Override
    public void onPromotionAdLoadSuccess() {
        DeveloperLog.LogD("onPromotionAdLoadSuccess : " + toString());
        mListener.onPromotionAdLoadSuccess(this);
    }

    @Override
    public void onPromotionAdLoadFailed(AdapterError error) {
        mListener.onPromotionAdLoadFailed(this, error);
    }

    @Override
    public void onPromotionAdShowFailed(AdapterError error) {
        DeveloperLog.LogE("PromotionAdShowFailed: " + error);
        mListener.onPromotionAdShowFailed(this, error);
    }

    @Override
    public void onPromotionAdVisible() {
        mListener.onPromotionAdVisible(this);
    }

    @Override
    public void onPromotionAdClicked() {
        mListener.onPromotionAdClicked(this);
    }

    @Override
    public void onPromotionAdHidden() {
        mListener.onPromotionAdHidden(this);
    }

    @Override
    public void onLoadTimeout() {
        onPromotionAdLoadFailed(AdapterErrorBuilder.buildLoadCheckError(
                AdapterErrorBuilder.AD_UNIT_PROMOTION, mAdapter == null ? "" : mAdapter.getClass().getSimpleName(), ErrorCode.ERROR_TIMEOUT));
    }
}
