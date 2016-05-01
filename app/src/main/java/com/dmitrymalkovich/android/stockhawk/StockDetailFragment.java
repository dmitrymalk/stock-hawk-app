package com.dmitrymalkovich.android.stockhawk;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dmitrymalkovich.android.stockhawk.data.QuoteColumns;
import com.dmitrymalkovich.android.stockhawk.data.QuoteProvider;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Stock detail screen.
 * This fragment is either contained in a {@link StockListActivity}
 * in two-pane mode (on tablets) or a {@link StockDetailActivity}
 * on handsets.
 */
public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private static final int CURSOR_LOADER_ID = 1;

    private long mId = -1;

    @Bind(R.id.stock_detail)
    TextView mSymbolView;

    public StockDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mId = getArguments().getLong(ARG_ITEM_ID);
        }

        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_detail, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                QuoteColumns._ID + " = " + mId,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String symbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
            mSymbolView.setText(symbol);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
