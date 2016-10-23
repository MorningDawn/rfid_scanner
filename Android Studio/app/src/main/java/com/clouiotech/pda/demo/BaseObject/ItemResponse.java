package com.clouiotech.pda.demo.BaseObject;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by roka on 23/10/16.
 */
public class ItemResponse {
    @SerializedName("success")
    private int successCode;

    @SerializedName("data")
    private List<Item> listItem;

    public void setSuccessCode(int successCode) {
        this.successCode = successCode;
    }

    public void setListItem(List<Item> listItem) {
        this.listItem = listItem;
    }

    public int getSuccessCode() {
        return successCode;
    }

    public List<Item> getListItem() {
        return listItem;
    }
}
