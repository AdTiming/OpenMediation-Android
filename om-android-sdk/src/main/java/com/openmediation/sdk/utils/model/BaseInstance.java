package com.openmediation.sdk.utils.model;

import android.text.TextUtils;

import com.openmediation.sdk.bid.BidResponse;
import com.openmediation.sdk.core.runnable.LoadTimeoutRunnable;
import com.openmediation.sdk.mediation.CustomAdsAdapter;
import com.openmediation.sdk.utils.DeveloperLog;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledFuture;

public class BaseInstance extends Frequency implements Comparable<BaseInstance> {
    // AuctionID
    private String reqId;
    //Instances Id
    protected int id;
    //Instances Name
    private String name;

    //Mediation Id
    protected int mediationId;
    //placement key
    protected String key;
    //placement template path
    private String path;
    //group index
    private int grpIndex;
    //own index
    protected int index;
    //is 1st in the group
    private boolean isFirst;

    //data for instance storage
    private Object object;

    private String appKey;

    private int hb;

    private int hbt;
    private BID_STATE bidState = BID_STATE.NOT_BIDDING;
    private int wfAbt;
    private BidResponse bidResponse;

    protected long mInitStart;
    protected long mLoadStart;
    protected long mShowStart;

    protected String mPlacementId;

    // Priority In Rule
    private int priority = -1;
    // Revenue, bidPrice or eCPM
    private double revenue = -1;
    // Revenue Precision, 0:undisclosed, 1:exact, 2:estimated, 3:defined
    private int revenuePrecision = -1;

    private MediationRule mMediationRule;

    protected CustomAdsAdapter mAdapter;

    protected InstanceLoadStatus mLastLoadStatus;

    private MEDIATION_STATE mMediationState;
    private LoadTimeoutRunnable mTimeoutRunnable;
    private ScheduledFuture mScheduledFuture;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getMediationId() {
        return mediationId;
    }

    public void setMediationId(int mediationId) {
        this.mediationId = mediationId;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setGrpIndex(int grpIndex) {
        this.grpIndex = grpIndex;
    }

    public int getGrpIndex() {
        return grpIndex;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setPlacementId(String placementId) {
        mPlacementId = placementId;
    }

    public String getPlacementId() {
        return mPlacementId;
    }

    public void setHb(int hb) {
        this.hb = hb;
    }

    public int getHb() {
        return hb;
    }

    public void setHbt(int hbt) {
        this.hbt = hbt;
    }

    public int getHbt() {
        return hbt;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public double getRevenue() {
        return Double.isNaN(revenue) || revenue <= 0 ? 0d : revenue;
    }

    public double getShowRevenue(int scale) {
        if (Double.isNaN(revenue) || revenue <= 0) {
            return 0d;
        }
        try {
            BigDecimal bigDecimal = new BigDecimal(revenue).divide(new BigDecimal(1000d));
            return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception e) {
            DeveloperLog.LogE("Instance getRevenue Error: " + e.getMessage());
        }
        return 0d;
    }

    public double getShowRevenue() {
        return getShowRevenue(8);
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public int getRevenuePrecision() {
        return revenuePrecision;
    }

    public void setRevenuePrecision(int revenuePrecision) {
        this.revenuePrecision = revenuePrecision;
    }

    public void setMediationRule(MediationRule rule) {
        this.mMediationRule = rule;
    }

    public MediationRule getMediationRule() {
        return mMediationRule;
    }

    public void setBidState(BID_STATE bidState) {
        this.bidState = bidState;
    }

    public BID_STATE getBidState() {
        return bidState;
    }

    public void setAdapter(CustomAdsAdapter adapter) {
        mAdapter = adapter;
    }

    public CustomAdsAdapter getAdapter() {
        return mAdapter;
    }

    public void setWfAbt(int wfAbt) {
        this.wfAbt = wfAbt;
    }

    public int getWfAbt() {
        return wfAbt;
    }

    public void setBidResponse(BidResponse bidResponse) {
        this.bidResponse = bidResponse;
    }

    public BidResponse getBidResponse() {
        return bidResponse;
    }

    public void setLastLoadStatus(InstanceLoadStatus lastLoadStatus) {
        this.mLastLoadStatus = lastLoadStatus;
    }

    public InstanceLoadStatus getLastLoadStatus() {
        return mLastLoadStatus;
    }

    /**
     * Sets mediation state.
     *
     * @param state the state
     */
    public void setMediationState(MEDIATION_STATE state) {
        mMediationState = state;
    }

    /**
     * Gets mediation state.
     *
     * @return the mediation state
     */
    public MEDIATION_STATE getMediationState() {
        return mMediationState;
    }

    /**
     * Is caped boolean.
     *
     * @return the boolean
     */
    public boolean isCaped() {
        return getMediationState() == MEDIATION_STATE.CAPPED;
    }

    public void setInitStart(long initStart) {
        this.mInitStart = initStart;
    }

    public long getInitStart() {
        return mInitStart;
    }

    public void setShowStart(long showStart) {
        this.mShowStart = showStart;
    }

    public long getShowStart() {
        return mShowStart;
    }

    public void setLoadStart(long loadStart) {
        this.mLoadStart = loadStart;
    }

    public long getLoadStart() {
        return mLoadStart;
    }

    public LoadTimeoutRunnable getTimeoutRunnable() {
        return mTimeoutRunnable;
    }

    public void setTimeoutRunnable(LoadTimeoutRunnable timeoutRunnable) {
        this.mTimeoutRunnable = timeoutRunnable;
    }

    public void setScheduledFuture(ScheduledFuture scheduledFuture) {
        this.mScheduledFuture = scheduledFuture;
    }

    public ScheduledFuture getScheduledFuture() {
        return mScheduledFuture;
    }

    @Override
    public String toString() {
        return "Ins{" +
                "id=" + id +
                ", mId=" + mediationId +
                ", index=" + index +
                ", grpIndex=" + grpIndex +
                ", pid=" + mPlacementId +
                ", revenue=" + revenue +
                ", name=" + name +
                '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + mediationId;
        result = prime * result + (TextUtils.isEmpty(key) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        } else if (this == obj) {
            return true;
        }
        BaseInstance other = (BaseInstance) obj;
        return TextUtils.equals(key, other.key) && id == other.id;
    }

    @Override
    public int compareTo(BaseInstance instance) {
        if (instance != null) {
            double diff = instance.revenue - this.revenue;
            if (diff > 0) return 1;
            if (diff < 0) return -1;
            return 0;
        }
        return 0;
    }

    public enum BID_STATE {
        /**
         * mediation not yet initialized; sets instance's state to after SDK init is done
         */
        NOT_INITIATED(0),
        /**
         * set after initialization failure
         */
        INIT_FAILED(1),
        /**
         * set after initialization success
         */
        INITIATED(2),
        /**
         * set after load success
         */
        BID_SUCCESS(3),
        /**
         * set after initialization starts
         */
        INIT_PENDING(4),
        /**
         * set after load starts
         */
        BID_PENDING(5),

        /**
         * set after load fails
         */
        BID_FAILED(6),

        NOT_BIDDING(7);

        private int mValue;

        BID_STATE(int value) {
            this.mValue = value;
        }

        public int getValue() {
            return this.mValue;
        }
    }

    /**
     * The enum Mediation state.
     */
    public enum MEDIATION_STATE {
        /**
         * mediation not yet initialized; sets instance's state to after SDK init is done
         */
        NOT_INITIATED(0),
        /**
         * set after initialization failure
         */
        INIT_FAILED(1),
        /**
         * set after initialization success
         */
        INITIATED(2),
        /**
         * set after load success
         */
        AVAILABLE(3),
        /**
         * set after load failure
         */
        NOT_AVAILABLE(4),

        /**
         * Capped per session mediation state.
         */
        CAPPED_PER_SESSION(5),
        /**
         * set after initialization starts
         */
        INIT_PENDING(6),
        /**
         * set after load starts
         */
        LOAD_PENDING(7),

        /**
         * set after load fails
         */
        LOAD_FAILED(8),
        /**
         * Capped per day mediation state.
         */
        CAPPED_PER_DAY(9),

        /**
         * set in the case of frequency control
         */
        CAPPED(10);

        private int mValue;

        MEDIATION_STATE(int value) {
            this.mValue = value;
        }

        /**
         * Gets value.
         *
         * @return the value
         */
        public int getValue() {
            return this.mValue;
        }
    }
}
