package com.erdfelt.toy.appwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ToyAppWidgetActivity extends Activity {
    private static final String TAG                      = ToyAppWidgetActivity.class.getSimpleName();
    private static final int    APPWIDGET_HOST_ID        = 2222;
    private static final int    REQUEST_CREATE_APPWIDGET = 5;
    private static final int    REQUEST_PICK_APPWIDGET   = 9;
    private static final int    DIALOG_CANCELED          = 1;
    private static final int    DIALOG_CLOCK_NOT_FOUND   = 2;
    private ToyAppWidgetFrame   widgetFrame;
    private AppWidgetManager    mAppWidgetManager;
    private AppWidgetHost       mAppWidgetHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        widgetFrame = (ToyAppWidgetFrame) findViewById(R.id.widgetFrame);

        mAppWidgetManager = AppWidgetManager.getInstance(this);

        mAppWidgetHost = new AppWidgetHost(this, APPWIDGET_HOST_ID);

        Button btnPickWidget = (Button) findViewById(R.id.btnPickWidget);
        btnPickWidget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelectWidget();
            }
        });

        Button btnPickClock = (Button) findViewById(R.id.btnPickClock);
        btnPickClock.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelectClockWidget();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_CANCELED: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Pick Canceled");
                builder.setMessage("You chose to cancel the widget pick.\nOh well.");
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                return builder.create();
            }
            case DIALOG_CLOCK_NOT_FOUND: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Warning");
                builder.setMessage("Unable to find Clock Widget Provider.");
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                return builder.create();
            }
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {

            if (requestCode == REQUEST_CREATE_APPWIDGET) {
                // TODO: Cleanup?
            }

            showDialog(DIALOG_CANCELED);
            return;
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_APPWIDGET:
                    addAppWidget(data);
                    break;
                case REQUEST_CREATE_APPWIDGET:
                    completeAddAppWidget(data);
                    break;
            }
        }
    }

    private void addAppWidget(Intent data) {
        // TODO: catch bad widget exception when sent
        int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

        logIntent("Add App Widget", data);

        AppWidgetProviderInfo appWidget = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        if (appWidget.configure != null) {
            // Launch over to configure widget, if needed
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidget.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            // Otherwise just add it
            onActivityResult(REQUEST_CREATE_APPWIDGET, Activity.RESULT_OK, data);
        }
    }

    /**
     * Add a widget to the workspace.
     * 
     * @param data
     *            The intent describing the appWidgetId.
     */
    private void completeAddAppWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

        logIntent("Complete Add App Widget", data);

        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);

        ToyAppWidgetInfo launcherInfo = new ToyAppWidgetInfo(appWidgetId);
        launcherInfo.hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        launcherInfo.hostView.setAppWidget(appWidgetId, appWidgetInfo);
        launcherInfo.hostView.setTag(launcherInfo);

        showAppWidget(launcherInfo);
    }

    private void showAppWidget(ToyAppWidgetInfo launcherInfo) {
        widgetFrame.setWidgetView(launcherInfo);
    }

    public void onClickSelectClockWidget() {
        String clockPackageName = "com.android.alarmclock";
        String clockProviderClass = "com.android.alarmclock.AnalogAppWidgetProvider";
        ComponentName provider = new ComponentName(clockPackageName, clockProviderClass);
        
        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        mAppWidgetManager.bindAppWidgetId(appWidgetId, provider);
        
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        onActivityResult(REQUEST_CREATE_APPWIDGET, Activity.RESULT_OK, intent);
    }

    public void onClickSelectWidget() {
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();

        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }

    private void logIntent(String msg, Intent intent) {
        StringBuilder dbg = new StringBuilder();
        dbg.append("Dump of Intent: ").append(msg);

        if (intent.getAction() != null) {
            dbg.append("\n Action = ").append(intent.getAction());
        }

        if (intent.getCategories() != null) {
            for (String category : intent.getCategories()) {
                dbg.append("\n Category = ").append(category);
            }
        }

        if (intent.getData() != null) {
            dbg.append("\n Data = ").append(intent.getDataString());
        }

        if (intent.getType() != null) {
            dbg.append("\n Type = ").append(intent.getType());
        }

        Bundle bundle = intent.getExtras();
        for (String key : bundle.keySet()) {
            Object val = bundle.get(key);
            dbg.append("\n Bundle[").append(key).append("] = ");
            if (val == null) {
                dbg.append("<null>");
            } else {
                dbg.append("(").append(val.getClass().getName()).append(") ");
                dbg.append(val.toString());
            }
        }
        Log.d(TAG, dbg.toString());
    }
}