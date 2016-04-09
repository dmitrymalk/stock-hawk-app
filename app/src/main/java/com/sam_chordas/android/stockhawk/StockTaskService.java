package com.sam_chordas.android.stockhawk;

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
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by sam_chordas on 9/30/15.
 * The GCMTask service is primarily for periodic tasks. However, OnRunTask can be called directly
 * and is used for the initialization and adding task as well.
 * <p/>
 * Updated By: Dmitry Malkovich.
 */
public class StockTaskService extends GcmTaskService {

    private String LOG_TAG = StockTaskService.class.getSimpleName();
    private final static String BASE_URL = "https://query.yahooapis.com/v1/public/yql?q=";
    private final static String QUERY_PREFIX = "select * from yahoo.finance.quotes where symbol in (";
    private final static String QUERY_SUFFIX = "&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
            + "org%2Falltableswithkeys&callback=";
    private final static String CHARSET_NAME = "UTF-8";
    private final static String INIT_QUOTES = "\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")";
    public final static String TAG_PERIODIC = "periodic";

    private Context mContext;
    private OkHttpClient mClient = new OkHttpClient();
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean mIsUpdate;

    public StockTaskService(Context context) {
        mContext = context;
    }

    public StockTaskService() {
    }

    @Override
    public int onRunTask(TaskParams params) {
        try {
            String url = buildUrl(params);
            saveData(fetchData(url));
            return GcmNetworkManager.RESULT_SUCCESS;

        } catch (IOException | RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            return GcmNetworkManager.RESULT_FAILURE;
        }
    }

    private String buildUrl(TaskParams params) throws UnsupportedEncodingException {
        ContentResolver resolver = mContext.getContentResolver();
        StringBuilder urlBuilder = new StringBuilder();

        // Base URL for the Yahoo query
        urlBuilder.append(BASE_URL);
        urlBuilder.append(URLEncoder.encode(QUERY_PREFIX, CHARSET_NAME));

        if (params.getTag().equals(StockIntentService.ACTION_INIT)
                || params.getTag().equals(TAG_PERIODIC)) {
            mIsUpdate = true;
            Cursor cursor = resolver.query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{"Distinct " + QuoteColumns.SYMBOL}, null,
                    null, null);

            if (cursor != null && cursor.getCount() == 0 || cursor == null) {
                // Init task. Populates DB with quotes for the symbols seen below
                urlBuilder.append(
                        URLEncoder.encode(INIT_QUOTES, CHARSET_NAME));
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
                mStoredSymbols.replace(mStoredSymbols.length() - 1, mStoredSymbols.length(), ")");
                urlBuilder.append(URLEncoder.encode(mStoredSymbols.toString(), CHARSET_NAME));
            }
        } else if (params.getTag().equals(StockIntentService.ACTION_ADD)) {
            mIsUpdate = false;
            // get symbol from params.getExtra and build query
            String stockInput = params.getExtras().getString(StockIntentService.EXTRA_SYMBOL);
            urlBuilder.append(URLEncoder.encode("\"" + stockInput + "\")", CHARSET_NAME));
        }
        // finalize the URL for the API query.
        urlBuilder.append(QUERY_SUFFIX);

        return urlBuilder.toString();
    }

    private String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

    private void saveData(String quoteJson) throws RemoteException, OperationApplicationException {
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues contentValues = new ContentValues();
        // Update is_current to 0 (false), so new data is current.
        if (mIsUpdate) {
            contentValues.put(QuoteColumns.ISCURRENT, 0);
            resolver.update(QuoteProvider.Quotes.CONTENT_URI, contentValues,
                    null, null);
        }
        resolver.applyBatch(QuoteProvider.AUTHORITY,
                Utils.quoteJsonToContentVals(quoteJson));
    }
}
