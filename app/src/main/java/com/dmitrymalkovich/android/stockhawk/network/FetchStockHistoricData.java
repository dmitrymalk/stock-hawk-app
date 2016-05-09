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

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dmitry Malkovich on 4/10/16.
 * <p/>
 * For storing response from Yahoo API.
 */
public class FetchStockHistoricData extends AsyncTask<String, Void, ResponseGetHistoricalData> {

    public static final String LOG_TAG = FetchStockHistoricData.class.getSimpleName();
    private final String mQuery;
    private final Listener mListener;

    /**
     * Interface definition for a callback to be pass new data.
     */
    public interface Listener {
        void onFetchedStockHistoricData(List<ResponseGetHistoricalData.Quote> quotes);
    }

    public FetchStockHistoricData(String symbol, String startDate, String endDate, Listener listener) {
        mQuery = "select * from yahoo.finance.historicaldata where symbol=\""
                + symbol + "\" and startDate=\"" + startDate + "\" and endDate=\"" + endDate + "\"";
        mListener = listener;
    }

    @Override
    protected ResponseGetHistoricalData doInBackground(String... params) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(StocksDatabaseService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        StocksDatabaseService service = retrofit.create(StocksDatabaseService.class);
        Call<ResponseGetHistoricalData> call = service.getStockHistoricalData(mQuery);
        Response<ResponseGetHistoricalData> response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            // Ignore
        }

        if (response != null) {
            return response.body();
        } else {
            return null;
        }
    }

    @Override
    protected void onPostExecute(ResponseGetHistoricalData responseGetStockHistoricalData) {
        if (responseGetStockHistoricalData == null) {
            return;
        }
        mListener.onFetchedStockHistoricData(responseGetStockHistoricalData.getHistoricData());
    }
}
