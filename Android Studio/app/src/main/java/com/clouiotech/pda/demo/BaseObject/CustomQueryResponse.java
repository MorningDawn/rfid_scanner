package com.clouiotech.pda.demo.BaseObject;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by roka on 17/03/17.
 */

public class CustomQueryResponse {
    @SerializedName("success")
    private int successCode;


    @SerializedName("data")
    private List<CustomQueryResponse> listQuery;

    private int id = 0;
    private String query_alias = "";
    private String query = "";

    public String getQueryAlias() {
        return query_alias;
    }

    public void setQueryAlias(String queryAlias) {
        this.query_alias = queryAlias;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<CustomQueryResponse> getListQuery() {
        return listQuery;
    }

    public void setListQuery(List<CustomQueryResponse> listQuery) {
        this.listQuery = listQuery;
    }
}
