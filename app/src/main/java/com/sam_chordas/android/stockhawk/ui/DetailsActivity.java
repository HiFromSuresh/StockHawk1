package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

public class DetailsActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String KEY = "key";
    public static final int LOADER_ID = 1;
    private LineChartView lineChartView;
    private LineSet dataset;
    double minValue = 0;
    double maxValue = 0;
    String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        lineChartView = (LineChartView) findViewById(R.id.linechart);
        Intent intent = getIntent();
        symbol = intent.getStringExtra(KEY);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(), QuoteProvider.Quotes.CONTENT_URI,
                new String[]{QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE},
                QuoteColumns.SYMBOL + " =?", new String[]{symbol}, QuoteColumns._ID + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dataset = new LineSet();

        int i = 0;
        if (data.moveToFirst()) {
            String bidprice = data.getString(1);
            float firstBidPrice = Float.parseFloat(bidprice);
            minValue = firstBidPrice;
            maxValue = firstBidPrice;
            dataset.addPoint(new Point(String.valueOf(i), firstBidPrice));
            i++;

            while (data.moveToNext()) {
                bidprice = data.getString(1);
                float floatBidprice = Float.parseFloat(bidprice);
                dataset.addPoint(new Point(String.valueOf(i), floatBidprice));

                if (minValue > floatBidprice) minValue = floatBidprice;
                if (maxValue < floatBidprice) maxValue = floatBidprice;
                i++;
            }
            dataset.setDotsColor(getResources().getColor(R.color.material_green_700));
            dataset.setColor(getResources().getColor(R.color.material_red_700));

            lineChartView.dismiss();
            lineChartView.addData(dataset);
            lineChartView.setAxisBorderValues((int) minValue - 5, (int) maxValue + 5);
            lineChartView.setAxisColor(Color.WHITE);
            lineChartView.setLabelsColor(Color.WHITE);
            lineChartView.setXAxis(false);
            lineChartView.setYAxis(false);
            lineChartView.setXLabels(AxisController.LabelPosition.NONE);
            lineChartView.setStep(1);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            lineChartView.setGrid(ChartView.GridType.FULL, paint);
            lineChartView.show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
