package com.clouiotech.pda.demo.BaseObject;

/**
 * Created by roka on 29/07/16.
 */
public class EpcObject {
    private int mQuantity;
    private int mPhysic;
    private int mDelta;
    private int mStatus;

    public static int EPC_STATUS_NORMAL = 0;
    public static int EPC_STATUS_OVER = 1;
    public static int EPC_STATUS_LESS = 2;

    // constructor
    public EpcObject(int quantity, int physic) {
        this.mQuantity = quantity;
        this.mPhysic = physic;
        this.mDelta = physic - quantity;

        if (this.mDelta == 0) this.mStatus = EPC_STATUS_NORMAL;
        else if (this.mDelta > 0) this.mStatus = EPC_STATUS_OVER;
        else this.mStatus = EPC_STATUS_LESS;
    }

    // set method

    public void setQuantity(int quantity) {
        this.mQuantity = quantity;
    }

    public void setPhysic(int physic) {
        this.mPhysic = physic;
    }

    public void setDelta(int delta) {
        this.mDelta = delta;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    // get method

    public int getQuantity() {
        return mQuantity;
    }

    public int getmPhysic() {
        return mPhysic;
    }

    public int getDelta() {
        return mDelta;
    }

    public int getStatus() {
        return mStatus;
    }

}
