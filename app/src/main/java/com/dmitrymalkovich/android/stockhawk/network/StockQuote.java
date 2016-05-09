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

    @SerializedName("Name")
    private String name;

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

    public String getName() {
        return name;
    }
}
