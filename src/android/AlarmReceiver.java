package com.az.cordova.plugin.shabatalarms;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = "AlarmReceiver";

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onReceive(Context context, Intent intent) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Log.d(LOG_TAG, "AlarmReceiver fired at " + sdf.format(new Date().getTime()));
// 		WakeupPlugin.setAlarmsFromPrefs( context );
	}
}
