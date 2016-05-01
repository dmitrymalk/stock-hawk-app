/*
 * Copyright 2016.  Dmitry Malkovich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
