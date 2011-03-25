package com.erdfelt.toy.appwidget;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.LayoutInflater;

public class ToyAppWidgetHostView extends AppWidgetHostView {
    @SuppressWarnings("unused")
    private LayoutInflater mInflater;

    public ToyAppWidgetHostView(Context context) {
        super(context);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

}
