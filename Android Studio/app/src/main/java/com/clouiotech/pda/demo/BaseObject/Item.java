package com.clouiotech.pda.demo.BaseObject;

import com.google.gson.annotations.SerializedName;
/**
 * Created by roka on 23/10/16.
 */
public class Item {
    public Item(String code, int quantity, String warehouse, String period, String group,
                String itemDesc) {
        this.itemCode = code;
        this.quantityAwal = quantity;
        this.warehouseId = warehouse;
        this.periodName = period;
        this.groupId = group;
        this.itemDescription = itemDesc;
    }

    @SerializedName("A")
    private String itemCode;

    @SerializedName("B")
    private int quantityAwal;

    @SerializedName("C")
    private String warehouseId;

    @SerializedName("D")
    private String periodName;

    @SerializedName("E")
    private String groupId;

    @SerializedName("F")
    private String itemDescription;


    // GET METHOD
    public int getQuantityAwal() {
        return quantityAwal;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getPeriodName() {
        return periodName;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    // SET METHOD
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public void setQuantityAwal(int quantityAwal) {
        this.quantityAwal = quantityAwal;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }
}


