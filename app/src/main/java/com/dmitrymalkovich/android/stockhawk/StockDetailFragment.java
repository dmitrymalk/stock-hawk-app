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
package com.dmitrymalkovich.android.stockhawk;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.dmitrymalkovich.android.stockhawk.data.QuoteColumns;
import com.dmitrymalkovich.android.stockhawk.data.QuoteProvider;
import com.dmitrymalkovich.android.stockhawk.network.FetchStockHistoricData;
import com.dmitrymalkovich.android.stockhawk.network.ResponseGetHistoricalData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * A fragment representing a single Stock detail screen.
 * This fragment is either contained in a {@link StockListActivity}
 * in two-pane mode (on tablets) or a {@link StockDetailActivity}
 * on handsets.
 */
public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        TabHost.OnTabChangeListener, FetchStockHistoricData.Listener {

    @SuppressWarnings("unused")
    public static String LOG_TAG = StockDetailFragment.class.getSimpleName();
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private static final int CURSOR_LOADER_ID = 1;

    private long mId = -1;
    private String mSymbol;
    private String mBidPrice;

    @Bind(R.id.stock_symbol)
    TextView mSymbolView;
    @Bind(R.id.stock_ebitda)
    TextView mEbitdaView;
    @Bind(android.R.id.tabhost)
    TabHost mTabHost;
    @Bind(R.id.chart)
    LineChartView mChart;
    @Bind(R.id.change)
    TextView mChange;
    @Bind(android.R.id.tabcontent)
    View mTabContent;

    public StockDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mId = getArguments().getLong(ARG_ITEM_ID);
        }

        if (getActionBar() != null) {
            getActionBar().setElevation(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_detail, container, false);
        ButterKnife.bind(this, rootView);
        setupTabs();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(), QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP,
                        QuoteColumns.NAME},
                QuoteColumns._ID + " = " + mId,
                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mSymbol = data.getString(data.getColumnIndex(QuoteColumns.SYMBOL));
            mSymbolView.setText(getString(R.string.stock_detail_tab_header, mSymbol));

            String ebitda = data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE));
            mEbitdaView.setText(ebitda);
            mBidPrice = ebitda;

            String name = data.getString(data.getColumnIndex(QuoteColumns.NAME));
            if (getActionBar() != null) {
                getActionBar().setTitle(name);
            }

            String change = data.getString(data.getColumnIndex(QuoteColumns.CHANGE));
            String percentChange = data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE));
            String mixedChange = change + " (" + percentChange + ")";
            mChange.setText(mixedChange);

            onTabChanged(getString(R.string.stock_detail_tab1));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onTabChanged(String tabId) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();

        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(currentDate);
        calEnd.add(Calendar.DATE, -1);

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(currentDate);
        calStart.add(Calendar.DATE, -30);

        new FetchStockHistoricData(mSymbol,
                dateFormat.format(calStart.getTime()),
                dateFormat.format(calEnd.getTime()),
                this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onFetchedStockHistoricData(List<ResponseGetHistoricalData.Quote> quotes) {

        List<AxisValue> axisValuesX = new ArrayList<>();
        List<PointValue> pointValues = new ArrayList<>();

        for (int i = 0; i < quotes.size(); i++) {
            ResponseGetHistoricalData.Quote quote = quotes.get(i);
            PointValue pv = new PointValue(i, Float.valueOf(quote.getOpen()));
            pv.setLabel(quote.getDate());
            pointValues.add(pv);

            if (i != 0 && i % 2 == 0) {
                AxisValue axisValueX = new AxisValue(i);
                axisValueX.setLabel(quote.getDate());
                axisValuesX.add(axisValueX);
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();

        PointValue pv = new PointValue(quotes.size(), Float.valueOf(mBidPrice));
        pv.setLabel(dateFormat.format(currentDate));
        pointValues.add(pv);

        if (quotes.size() != 0 && quotes.size() % 2 == 0) {

            AxisValue axisValueX = new AxisValue(quotes.size());
            axisValueX.setLabel(dateFormat.format(currentDate));
            axisValuesX.add(axisValueX);
        }


        Line line = new Line(pointValues).setColor(Color.WHITE).setCubic(false);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axisX = new Axis(axisValuesX);
        axisX.setHasLines(true);
        axisX.setMaxLabelChars(4);
        data.setAxisXBottom(axisX);

        Axis axisY = new Axis();
        axisY.setAutoGenerated(true);
        axisY.setHasLines(true);
        axisY.setMaxLabelChars(4);
        data.setAxisYLeft(axisY);

        mChart.setInteractive(false);
        mChart.setLineChartData(data);
        mChart.setVisibility(View.VISIBLE);
        mTabContent.setVisibility(View.VISIBLE);
    }

    @Nullable
    private ActionBar getActionBar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            return activity.getSupportActionBar();
        }
        return null;
    }

    private void setupTabs() {
        mTabHost.setup();

        TabHost.TabSpec tabSpec;
        tabSpec = mTabHost.newTabSpec(getString(R.string.stock_detail_tab1));
        tabSpec.setIndicator(getString(R.string.stock_detail_tab1));
        tabSpec.setContent(android.R.id.tabcontent);
        mTabHost.addTab(tabSpec);

        tabSpec = mTabHost.newTabSpec(getString(R.string.stock_detail_tab2));
        tabSpec.setIndicator(getString(R.string.stock_detail_tab2));
        tabSpec.setContent(android.R.id.tabcontent);
        mTabHost.addTab(tabSpec);

        tabSpec = mTabHost.newTabSpec(getString(R.string.stock_detail_tab3));
        tabSpec.setIndicator(getString(R.string.stock_detail_tab3));
        tabSpec.setContent(android.R.id.tabcontent);

        mTabHost.addTab(tabSpec);
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setCurrentTab(0);
    }
}
