package com.dmitrymalkovich.android.stockhawk.network;

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
    private Results mResults;

    public List<StockQuote> getStockQuotes() {
        List<StockQuote> result = new ArrayList<>();
        List<StockQuote> stockQuotes = mResults.getQuote().getStockQuotes();
        for (StockQuote stockQuote : stockQuotes) {
            if (stockQuote.getBid() != null && stockQuote.getChangeInPercent() != null
                    && stockQuote.getChange() != null) {
                result.add(stockQuote);
            }
        }
        return result;
    }

    @SuppressWarnings("unused")
    public class Results {

        @SerializedName("count")
        private String mCount;

        @SerializedName("results")
        private Quotes mQuote;

        public Quotes getQuote() {
            return mQuote;
        }
    }

    public class Quotes {

        @SerializedName("quote")
        private List<StockQuote> mStockQuotes = new ArrayList<>();

        public List<StockQuote> getStockQuotes() {
            return mStockQuotes;
        }
    }
}
