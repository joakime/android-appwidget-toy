package com.erdfelt.toy.appwidget;

import android.appwidget.AppWidgetHostView;

public class ToyAppWidgetInfo {
    public AppWidgetHostView hostView;
    public final int appWidgetId;

    public ToyAppWidgetInfo(int appWidgetId) {
        this.appWidgetId = appWidgetId;
    }
}
