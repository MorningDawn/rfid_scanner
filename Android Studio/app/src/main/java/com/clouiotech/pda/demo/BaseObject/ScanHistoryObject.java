package com.clouiotech.pda.demo.BaseObject;

import com.clouiotech.pda.demo.Adapter.ScanHistoryAdapter;

import java.util.Date;

/**
 * Created by roka on 30/07/16.
 */
public class ScanHistoryObject {

    public ScanHistoryObject(String code, String number, String date) {
        this.historyCode = code;
        this.historyDate = date;
        this.historyNumber = number;
    }

    private String historyCode, historyNumber, historyDate;

    public void setHistoryCode(String historyCode) {
        this.historyCode = historyCode;
    }

    public void setHistoryDate(String historyDate) {
        this.historyDate = historyDate;
    }

    public void setHistoryNumber(String historyNumber) {
        this.historyNumber = historyNumber;
    }

    public String getHistoryCode() {
        return historyCode;
    }

    public String getHistoryDate() {
        return historyDate;
    }

    public String getHistoryNumber() {
        return historyNumber;
    }
}
