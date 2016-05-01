package com.dmitrymalkovich.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dmitry Malkovich on 4/10/16.
 * <p/>
 * For storing response from Yahoo API.
 */
@SuppressWarnings("unused")
public class StockQuote {

    @SerializedName("Change")
    private String change;

    @SerializedName("symbol")
    private String symbol;

    @SerializedName("Bid")
    private String bid;

    @SerializedName("ChangeinPercent")
    private String changeInPercent;

    public String getChange() {
        return change;
    }

    public String getBid() {
        return bid;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getChangeInPercent() {
        return changeInPercent;
    }
}
