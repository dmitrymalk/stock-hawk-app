package com.sam_chordas.android.stockhawk.network;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitry Malkovich on 4/10/16.
 * <p/>
 * For storing response from Yahoo API.
 */
@SuppressWarnings("unused")
public class ResponseGetStocks {

    @SerializedName("query")
    private Results results;

    public Results getResults() {
        return results;
    }

    @SuppressWarnings("unused")
    public class Results {

        @SerializedName("count")
        private String count;

        @SerializedName("results")
        private Quotes quote;

        public Quotes getQuote() {
            return quote;
        }
    }

    public class Quotes {

        @SerializedName("quote")
        private List<StockQuote> stockQuotes = new ArrayList<>();

        public List<StockQuote> getStockQuotes() {
            return stockQuotes;
        }
    }
}
