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
import com.google.gson.internal.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Dmitry Malkovich on 4/10/16.
 * <p/>
 * For storing response from Yahoo API.
 */
@SuppressWarnings("unused")
public class ResponseGetHistoricalData {

    @SerializedName("query")
    private Results mResults;

    public List<Quote> getHistoricData() {
        List<Quote> result = new ArrayList<>();
        if (mResults.getQuote() != null) {
            List<Quote> quotes = mResults.getQuote().getStockQuotes();
            for (Quote quote : quotes) {
                result.add(quote);
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
        private List<Quote> mStockQuotes = new ArrayList<>();

        public List<Quote> getStockQuotes() {
            return mStockQuotes;
        }
    }

    public class Quote {

        @SerializedName("Symbol")
        private String mSymbol;

        @SerializedName("Date")
        private String mDate;

        @SerializedName("Low")
        private String mLow;

        @SerializedName("High")
        private String mHigh;

        @SerializedName("Open")
        private String mOpen;

        public String getSymbol() {
            return mSymbol;
        }

        public String getDate() {
            return mDate;
        }

        public String getOpen() {
            return mOpen;
        }
    }
}
