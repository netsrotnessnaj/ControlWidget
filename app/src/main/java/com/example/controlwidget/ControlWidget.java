package com.example.controlwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.bluetooth.BluetoothAdapter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ControlWidget extends AppWidgetProvider {

    private static final String BLUETOOTH_CLICKED    = "bluetoothButtonClick";
    private static final String WLAN_CLICKED    = "wlanButtonClick";

    private ImageView buttonBluetoothOn;
    private Context context;

    RemoteViews remoteViews;


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("ControlWidget", "Update Control Widget");
        ComponentName watchWidget;

        remoteViews = new RemoteViews(context.getPackageName(), R.layout.control_widget);
        watchWidget = new ComponentName(context, ControlWidget.class);

        remoteViews.setOnClickPendingIntent(R.id.iconBluetoothOn, getPendingSelfIntent(context, BLUETOOTH_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.iconBluetoothOff, getPendingSelfIntent(context, BLUETOOTH_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.iconWifiOn, getPendingSelfIntent(context, WLAN_CLICKED));
        remoteViews.setOnClickPendingIntent(R.id.iconWifiOff, getPendingSelfIntent(context, WLAN_CLICKED));

        // Bluetooth
        BluetoothAdapter adapter = null;
        adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter.getState() == BluetoothAdapter.STATE_ON) {
            Log.d("ControlWidget", "Bluetooth Status:ON");
            remoteViews.setViewVisibility(R.id.iconBluetoothOn,View.VISIBLE);
            remoteViews.setViewVisibility(R.id.iconBluetoothOff,View.INVISIBLE);

        } else {
            Log.d("ControlWidget", "Bluetooth Status:OFF");
            remoteViews.setViewVisibility(R.id.iconBluetoothOn,View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.iconBluetoothOff,View.VISIBLE);
        }

        // Wifi
        WifiManager wifi = null;
                wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifi.isWifiEnabled()) {
            Log.d("ControlWidget", "WLAN Status:ON");
            remoteViews.setViewVisibility(R.id.iconWifiOn,View.VISIBLE);
            remoteViews.setViewVisibility(R.id.iconWifiOff,View.INVISIBLE);

        } else {
            Log.d("ControlWidget", "WLAN Status:OFF");
            remoteViews.setViewVisibility(R.id.iconWifiOn,View.INVISIBLE);
            remoteViews.setViewVisibility(R.id.iconWifiOff,View.VISIBLE);
        }




        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }



    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        final String action = intent.getAction();
        if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            this.onUpdate(context, AppWidgetManager.getInstance(context),null);
        }


        if (BLUETOOTH_CLICKED.equals(intent.getAction())) {

            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            //Bundle extras = intent.getExtras();
            if(adapter != null) {
                //int[] appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                if(adapter.getState() == BluetoothAdapter.STATE_ON) {
                    Log.d("ControlWidget", "Set Bluetooth disabled");
                    adapter.disable();
                    SystemClock.sleep(1000);

                } else if (adapter.getState() == BluetoothAdapter.STATE_OFF){
                    Log.d("ControlWidget", "Set Bluetooth enabled");
                    adapter.enable();
                    SystemClock.sleep(1000);


                } else {
                    //State.INTERMEDIATE_STATE;

                }
                this.onUpdate(context, AppWidgetManager.getInstance(context),null);
            }
        }

        if (WLAN_CLICKED.equals(intent.getAction())) {

            final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            if (wifi.isWifiEnabled()) {
                Log.d("ControlWidget", "Set Wifi disabled");
                wifi.setWifiEnabled(false);

            } else {
                wifi.setWifiEnabled(true);
                Log.d("ControlWidget", "Set Wifi enabled");



                        while(!wifi.isWifiEnabled()) {
                            try {
                                Log.d("ControlWidget", "Check WLAN Connection");
                                Thread.sleep(500);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }



            }
            SystemClock.sleep(1000);
            this.onUpdate(context, AppWidgetManager.getInstance(context),null);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }


}
