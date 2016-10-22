package com.clouiotech.pda.demo.BaseObject;

import com.google.gson.annotations.SerializedName;
/**
 * Created by roka on 23/10/16.
 */
public class Item {
    public Item(String id, String code, String desc, String partNumber, String bar, String uomCode,
                int stocks, boolean isActive) {
        this.itemId = id;
        this.itemCode = code;
        this.itemDescription = desc;
        this.partNumber = partNumber;
        this.barcode = bar;
        this.uomCode = uomCode;
        this.minStock = stocks;
        this.activeFlag = isActive;
    }

    @SerializedName("item_id")
    private String itemId;

    @SerializedName("item_code")
    private String itemCode;

    @SerializedName("item_description")
    private String itemDescription;

    @SerializedName("part_number")
    private String partNumber;

    @SerializedName("barcode")
    private String barcode;

    @SerializedName("uom_code")
    private String uomCode;

    @SerializedName("min_stock")
    private int minStock;

    @SerializedName("active_flag")
    private boolean activeFlag;

    public String getItemId() {
        return itemId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public String getItem_description() {
        return itemDescription;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getUomCode() {
        return uomCode;
    }

    public int getMinStock() {
        return minStock;
    }

    public boolean getActiveFlag() {
        return activeFlag;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setMinStock(int minStock) {
        this.minStock = minStock;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public void setUomCode(String uomCode) {
        this.uomCode = uomCode;
    }
}
