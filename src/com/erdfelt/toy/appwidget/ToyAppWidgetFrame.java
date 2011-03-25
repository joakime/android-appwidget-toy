package com.erdfelt.toy.appwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class ToyAppWidgetFrame extends FrameLayout {
    private static final String TAG = ToyAppWidgetFrame.class.getSimpleName();

    public ToyAppWidgetFrame(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ToyAppWidgetFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ToyAppWidgetFrame(Context context) {
        super(context);
    }

    public void setWidgetView(ToyAppWidgetInfo launcherInfo) {
        if (launcherInfo.hostView == null) {
            Log.w(TAG, "No hostview??");
            return;
        }
        removeAllViews();
        int width = FrameLayout.LayoutParams.MATCH_PARENT;
        int height = FrameLayout.LayoutParams.WRAP_CONTENT;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
        addView(launcherInfo.hostView, lp);
    }
}
