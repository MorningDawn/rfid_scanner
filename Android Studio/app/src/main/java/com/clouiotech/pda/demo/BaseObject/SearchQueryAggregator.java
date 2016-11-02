package com.clouiotech.pda.demo.BaseObject;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by roka on 01/11/16.
 */

public class SearchQueryAggregator {

    private List<String> normalOperation= null;
    private List<String> minusOperation = null;
    private List<String> quoteOperation = null;
    private String mQuery = null;
    private String mQueryHelper = null;

    private static final String mMinusPattern = "-[a-zA-Z0-9-]*";
    private static final String mQuotePattern = "\"[a-zA-Z0-9- ]*\"*";

    public SearchQueryAggregator(String query) {
        mQuery = query;
        mQueryHelper = query;

        normalOperation = new ArrayList<>();
        minusOperation = new ArrayList<>();
        quoteOperation = new ArrayList<>();

        Pattern minusPattern = Pattern.compile(mMinusPattern);
        Pattern quotePattern = Pattern.compile(mQuotePattern);

        Matcher minusMatcher = minusPattern.matcher(mQuery);
        Matcher quoteMatcher = quotePattern.matcher(mQuery);

        while(minusMatcher.find()) {
            mQueryHelper = mQueryHelper.replace(minusMatcher.group(),"" );
            minusOperation.add(minusMatcher.group().replace("-",""));
        }

        while(quoteMatcher.find()) {
            mQueryHelper = mQueryHelper.replace(quoteMatcher.group(), "");
            quoteOperation.add(quoteMatcher.group().replace("\"",""));
        }

        Log.d("FAJAR","asdasd" + mQueryHelper);
        for (String string : mQueryHelper.split(" ")) {
            if(string != "" && string.length() != 0) {
                normalOperation.add(string);
            }
        }

        Log.d("FAJAR", minusOperation.toString() + " " + quoteOperation.toString() + " " + normalOperation.toString());

    }

    public void setMinusOperation(List<String> minusOperation) {
        this.minusOperation = minusOperation;
    }

    public void setNormalOperation(List<String> normalOperation) {
        this.normalOperation = normalOperation;
    }

    public void setQuoteOperation(List<String> quoteOperation) {
        this.quoteOperation = quoteOperation;
    }

    public List<String> getMinusOperation() {
        return minusOperation;
    }

    public List<String> getNormalOperation() {
        return normalOperation;
    }

    public List<String> getQuoteOperation() {
        return quoteOperation;
    }
}
