package com.sam_chordas.android.stockhawk;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by Administrator on 6/11/2016.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    Intent intent;
    private Cursor data = null;

    public WidgetDataProvider(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (data != null) {
            data.close();
        }

        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();

        // This is the same query from MyStocksActivity
        data = context.getContentResolver().query(
                QuoteProvider.Quotes.CONTENT_URI,
                new String[] {
                        QuoteColumns._ID,
                        QuoteColumns.SYMBOL,
                        QuoteColumns.BIDPRICE,
                        QuoteColumns.PERCENT_CHANGE,
                        QuoteColumns.CHANGE,
                        QuoteColumns.ISUP
                },
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                data == null || !data.moveToPosition(position)) {
            return null;
        }

        // Get the layout
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_collection_item);

        // Bind data to the views
        views.setTextViewText(R.id.stock_symbol, data.getString(data.getColumnIndex
                (context.getResources().getString(R.string.string_symbol))));

        if (data.getInt(data.getColumnIndex(QuoteColumns.ISUP)) == 1) {
            views.setInt(R.id.change, context.getResources().getString(R.string.string_set_background_resource), R.drawable.percent_change_pill_green);
        } else {
            views.setInt(R.id.change, context.getResources().getString(R.string.string_set_background_resource), R.drawable.percent_change_pill_red);
        }

        if (Utils.showPercent) {
            views.setTextViewText(R.id.change, data.getString(data.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
        } else {
            views.setTextViewText(R.id.change, data.getString(data.getColumnIndex(QuoteColumns.CHANGE)));
        }

        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra(context.getResources().getString(R.string.string_symbol), data.getString(data.getColumnIndex(QuoteColumns.SYMBOL)));
        views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
