// Copyright 2020 ADTIMING TECHNOLOGY COMPANY LIMITED
// Licensed under the GNU Lesser General Public License Version 3

package com.openmediation.sdk.bid;

import android.content.Context;
import android.util.SparseArray;

import com.openmediation.sdk.banner.AdSize;
import com.openmediation.sdk.utils.AdtUtil;
import com.openmediation.sdk.utils.DeveloperLog;
import com.openmediation.sdk.utils.crash.CrashUtil;
import com.openmediation.sdk.utils.event.EventId;
import com.openmediation.sdk.utils.event.EventUploadManager;
import com.openmediation.sdk.utils.model.BaseInstance;
import com.openmediation.sdk.utils.model.Configurations;
import com.openmediation.sdk.utils.model.Instance;
import com.openmediation.sdk.utils.model.Placement;
import com.openmediation.sdk.utils.request.network.AdRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BidAuctionManager {

    private final ConcurrentHashMap<String, List<BaseInstance>> mBidInstances;

    private static final class BidHolder {
        private static final BidAuctionManager INSTANCE = new BidAuctionManager();
    }

    private BidAuctionManager() {
        mBidInstances = new ConcurrentHashMap<>();
    }

    public static BidAuctionManager getInstance() {
        return BidHolder.INSTANCE;
    }

    public ConcurrentHashMap<String, List<BaseInstance>> getBidInstances() {
        return mBidInstances;
    }

    public void initBid(Context context, Configurations config) {
        if (config == null) {
            return;
        }

        Map<String, Placement> placementMap = config.getPls();
        if (placementMap == null || placementMap.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Placement> placementEntry : placementMap.entrySet()) {
            if (placementEntry == null) {
                continue;
            }
            List<BaseInstance> bidInstances = new ArrayList<>();
            SparseArray<BaseInstance> insMap = placementEntry.getValue().getInsMap();

            if (insMap == null || insMap.size() <= 0) {
                continue;
            }

            int size = insMap.size();
            for (int i = 0; i < size; i++) {
                BaseInstance instance = insMap.valueAt(i);
                if (instance == null) {
                    continue;
                }

                if (instance.getHb() == 1) {
                    BidAdapter bidAdapter = BidAdapterUtil.getBidAdapter(instance.getMediationId());
                    if (bidAdapter != null) {
                        try {
                            bidAdapter.initBid(context, BidUtil.makeBidInitInfo(config, instance.getMediationId()),
                                    null);
                            bidInstances.add(instance);
                        } catch (Throwable throwable) {
                            DeveloperLog.LogE("initBid error: " + throwable.toString());
                            CrashUtil.getSingleton().saveException(throwable);
                        }
                    }
                }
            }
            if (bidInstances.size() > 0) {
                mBidInstances.put(placementEntry.getKey(), bidInstances);
            }
        }
    }

    public void c2sBid(Context context, List<Instance> bidInstances, String placementId, String reqId, int adType, AuctionCallback callback) {
        c2sBid(context, bidInstances, placementId, reqId, adType, null, callback);
    }

    public void c2sBid(Context context, List<Instance> bidInstances, String placementId, String reqId, int adType, AdSize adSize, AuctionCallback callback) {
        BidC2SAuctionManager.getInstance().bid(context, bidInstances, placementId, reqId, adType, adSize, callback);
    }

    public void s2sBid(Context context, String placementId, String reqId, int adType, AuctionCallback callback) {
        BidS2SAuctionManager.getInstance().bid(context, placementId, reqId, adType, callback);
    }

    public void notifyWin(BaseInstance instance) {
        if (BidAdapterUtil.hasBidAdapter(instance.getMediationId())) {
            BidAdapter bidAdapter = BidAdapterUtil.getBidAdapter(instance.getMediationId());
            if (bidAdapter != null) {
                bidAdapter.notifyWin(instance.getKey(), null);
                EventUploadManager.getInstance().uploadEvent(EventId.INSTANCE_BID_WIN, instance.buildReportData());
            }
        }
    }

    void notifyWin(String url, BaseInstance instance) {
        AdRequest.get().url(url).performRequest(AdtUtil.getApplication());
        EventUploadManager.getInstance().uploadEvent(EventId.INSTANCE_BID_WIN, instance.buildReportData());
    }

    public void notifyLose(BaseInstance instance, int reason) {
        if (BidAdapterUtil.hasBidAdapter(instance.getMediationId())) {
            BidAdapter bidAdapter = BidAdapterUtil.getBidAdapter(instance.getMediationId());
            if (bidAdapter != null) {
                bidAdapter.notifyLose(instance.getKey(), makeNotifyMap(reason));
                EventUploadManager.getInstance().uploadEvent(EventId.INSTANCE_BID_LOSE, instance.buildReportData());
            }
        }
    }

    private Map<String, Object> makeNotifyMap(int reason) {
        Map<String, Object> map = new HashMap<>();
        map.put(BidConstance.BID_NOTIFY_REASON, reason);
        return map;
    }

    void notifyLose(String url, BaseInstance instance) {
        AdRequest.get().url(url).performRequest(AdtUtil.getApplication());
        EventUploadManager.getInstance().uploadEvent(EventId.INSTANCE_BID_LOSE, instance.buildReportData());
    }

}