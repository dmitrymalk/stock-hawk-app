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
package com.dmitrymalkovich.android.stockhawk.data;

import android.content.ContentProviderOperation;
import android.net.Uri;

import com.dmitrymalkovich.android.stockhawk.network.ResponseGetHistoricalData;
import com.dmitrymalkovich.android.stockhawk.network.StockQuote;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.Locale;

/**
 * Created by sam_chordas on 10/5/15.
 * <p/>
 * Updated By: Dmitry Malkovich.
 */
@ContentProvider(authority = QuoteProvider.AUTHORITY, database = QuoteDatabase.class)
public class QuoteProvider {
    public static final String AUTHORITY = "com.dmitrymalkovich.android.stockhawk.data.QuoteProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String QUOTES = "quotes";
        String QUOTES_HISTORIC_DATA = "quotes_historical_data";
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = QuoteDatabase.QUOTES_HISTORICAL_DATA)
    public static class QuotesHistoricData {
        @ContentUri(
                path = Path.QUOTES_HISTORIC_DATA,
                type = "vnd.android.cursor.dir/quote_historical_data"
        )
        public static final Uri CONTENT_URI = buildUri(Path.QUOTES_HISTORIC_DATA);
    }

    @TableEndpoint(table = QuoteDatabase.QUOTES)
    public static class Quotes {
        @ContentUri(
                path = Path.QUOTES,
                type = "vnd.android.cursor.dir/quote"
        )
        public static final Uri CONTENT_URI = buildUri(Path.QUOTES);

        @InexactContentUri(
                name = "QUOTE_ID",
                path = Path.QUOTES + "/*",
                type = "vnd.android.cursor.item/quote",
                whereColumn = QuoteColumns.SYMBOL,
                pathSegment = 1
        )
        public static Uri withSymbol(String symbol) {
            return buildUri(Path.QUOTES, symbol);
        }
    }

    public static ContentProviderOperation buildBatchOperation(ResponseGetHistoricalData.Quote quote) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.QuotesHistoricData.CONTENT_URI);
        builder.withValue(QuoteHistoricalDataColumns.SYMBOL, quote.getSymbol());
        builder.withValue(QuoteHistoricalDataColumns.BIDPRICE, quote.getOpen());
        builder.withValue(QuoteHistoricalDataColumns.DATE, quote.getDate());
        return builder.build();
    }

    public static ContentProviderOperation buildBatchOperation(StockQuote quote) {

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        String change = quote.getChange();
        builder.withValue(QuoteColumns.SYMBOL, quote.getSymbol());
        builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(quote.getBid()));
        builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                quote.getChangeInPercent(), true));
        builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
        builder.withValue(QuoteColumns.ISCURRENT, 1);
        if (change.charAt(0) == '-') {
            builder.withValue(QuoteColumns.ISUP, 0);
        } else {
            builder.withValue(QuoteColumns.ISUP, 1);
        }
        builder.withValue(QuoteColumns.NAME, quote.getName());
        return builder.build();
    }

    private static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format(Locale.US, "%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    private static String truncateChange(String change, boolean isPercentChange) {
        String weight = change.substring(0, 1);
        String ampersand = "";
        if (isPercentChange) {
            ampersand = change.substring(change.length() - 1, change.length());
            change = change.substring(0, change.length() - 1);
        }
        change = change.substring(1, change.length());
        double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
        change = String.format(Locale.US, "%.2f", round);
        StringBuilder changeBuffer = new StringBuilder(change);
        changeBuffer.insert(0, weight);
        changeBuffer.append(ampersand);
        change = changeBuffer.toString();
        return change;
    }
}
