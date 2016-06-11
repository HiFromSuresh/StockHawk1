package com.sam_chordas.android.stockhawk;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Administrator on 6/11/2016.
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}
