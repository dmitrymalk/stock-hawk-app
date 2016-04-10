package com.sam_chordas.android.stockhawk.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Dmitry Malkovich on 4/10/16.
 * <p/>
 * For getting stocks quotes from Yahoo API.
 */
public interface StocksDatabaseService {

    String BASE_URL = "https://query.yahooapis.com";

    @GET("/v1/public/yql?" +
            "format=json&diagnostics=true&" +
            "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseGetStocks> getStocks(@Query("q") String query);

    @GET("/v1/public/yql?" +
            "format=json&diagnostics=true&" +
            "env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<ResponseGetStock> getStock(@Query("q") String query);
}
