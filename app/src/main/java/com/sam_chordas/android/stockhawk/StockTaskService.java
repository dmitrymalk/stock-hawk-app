package com.sam_chordas.android.stockhawk;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.network.ResponseGetStock;
import com.sam_chordas.android.stockhawk.network.StockQuote;
import com.sam_chordas.android.stockhawk.network.StocksDatabaseService;
import com.sam_chordas.android.stockhawk.network.ResponseGetStocks;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 * <p/>
 * Updated By: Dmitry Malkovich.
 */
public class StockTaskService extends GcmTaskService {

    private static String LOG_TAG = StockTaskService.class.getSimpleName();
    private final static String INIT_QUOTES = "\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\"";
    public final static String TAG_PERIODIC = "periodic";

    private Context mContext;
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean mIsUpdate;

    public StockTaskService(Context context) {
        mContext = context;
    }

    @SuppressWarnings("unused")
    public StockTaskService() {
    }

    @Override
    public int onRunTask(TaskParams params) {

        if (mContext == null) {
            return GcmNetworkManager.RESULT_FAILURE;
        }
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(StocksDatabaseService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            StocksDatabaseService service = retrofit.create(StocksDatabaseService.class);
            String query = "select * from yahoo.finance.quotes where symbol in ("
                    + buildUrl(params)
                    + ")";

            // UGLY : JSON is different if we request data multiple stocks.
            if (params.getTag().equals(StockIntentService.ACTION_INIT)) {
                Call<ResponseGetStocks> call = service.getStocks(query);
                Response<ResponseGetStocks> response = call.execute();
                ResponseGetStocks responseGetStocks = response.body();

                saveData(responseGetStocks.getResults().getQuote().getStockQuotes());
            } else {
                Call<ResponseGetStock> call = service.getStock(query);
                Response<ResponseGetStock> response = call.execute();
                ResponseGetStock responseGetStock = response.body();

                List<StockQuote> quotes = new ArrayList<>();
                quotes.add(responseGetStock.getResult().getQuote().getStockQuote());
                saveData(quotes);
            }

            return GcmNetworkManager.RESULT_SUCCESS;

        } catch (IOException | RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return GcmNetworkManager.RESULT_FAILURE;
        }
    }

    private String buildUrl(TaskParams params) throws UnsupportedEncodingException {
        ContentResolver resolver = mContext.getContentResolver();
        if (params.getTag().equals(StockIntentService.ACTION_INIT)
                || params.getTag().equals(TAG_PERIODIC)) {
            mIsUpdate = true;
            Cursor cursor = resolver.query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{"Distinct " + QuoteColumns.SYMBOL}, null,
                    null, null);

            if (cursor != null && cursor.getCount() == 0 || cursor == null) {
                // Init task. Populates DB with quotes for the symbols seen below
                return INIT_QUOTES;
            } else {
                DatabaseUtils.dumpCursor(cursor);
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    mStoredSymbols.append("\"");
                    mStoredSymbols.append(cursor.getString(
                            cursor.getColumnIndex(QuoteColumns.SYMBOL)));
                    mStoredSymbols.append("\",");
                    cursor.moveToNext();
                }
                mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), "");
                return mStoredSymbols.toString();
            }
        } else if (params.getTag().equals(StockIntentService.ACTION_ADD)) {
            mIsUpdate = false;
            // Get symbol from params.getExtra and build query
            String stockInput = params.getExtras().getString(StockIntentService.EXTRA_SYMBOL);
            return "\"" + stockInput + "\"";
        } else {
            throw new IllegalStateException("Action not specified in TaskParams.");
        }
    }

    private void saveData(List<StockQuote> quotes) throws RemoteException, OperationApplicationException {
        ContentResolver resolver = mContext.getContentResolver();

        // Update is_current to 0 (false), so new data is current.
        if (mIsUpdate) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(QuoteColumns.ISCURRENT, 0);
            resolver.update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                    null, null);
        }

        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        for (StockQuote quote : quotes) {
            batchOperations.add(QuoteProvider.buildBatchOperation(quote));
        }

        resolver.applyBatch(QuoteProvider.AUTHORITY, batchOperations);
    }
}
