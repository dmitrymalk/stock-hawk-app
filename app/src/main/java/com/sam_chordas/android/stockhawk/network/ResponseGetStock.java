package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dmitry Malkovich on 4/10/16.
 * <p/>
 * For storing response from Yahoo API.
 */
@SuppressWarnings("unused")
public class ResponseGetStock {

    @SerializedName("query")
    private Result result;

    public Result getResult() {
        return result;
    }

    public class Result {

        @SerializedName("results")
        private Quote quote;

        public Quote getQuote() {
            return quote;
        }
    }

    public class Quote {

        @SerializedName("quote")
        private StockQuote stockQuote;

        public StockQuote getStockQuote() {
            return stockQuote;
        }
    }
}
