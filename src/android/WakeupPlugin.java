package com.az.cordova.plugin.wakeupplugin;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class WakeupPlugin extends CordovaPlugin {

	protected static final String LOG_TAG = "WakeupPlugin";

	public static CallbackContext connectionCallbackContext;



	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		boolean ret=true;
		JSONObject options=args.getJSONObject(0);
		Context context = cordova.getActivity().getApplicationContext();
		try {
			if(action.equalsIgnoreCase("setAlarm")) {
				setAlarm(callbackContext, options, context);
			}else if(action.equalsIgnoreCase("cancelAlarm")) {
				cancelAlarms(callbackContext, options, context);
			}else{
				PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, LOG_TAG + " error: invalid action (" + action + ")");
				pluginResult.setKeepCallback(true);
				callbackContext.sendPluginResult(pluginResult);
				ret=false;
			}
		} catch (Exception e) {
			PluginResult pluginResult = new PluginResult(PluginResult.Status.ERROR, LOG_TAG + " error: " + e.getMessage());
			pluginResult.setKeepCallback(true);
			callbackContext.sendPluginResult(pluginResult);
			ret = false;
		}
		return ret;
	}

	private void setAlarm(CallbackContext callbackContext, JSONObject options, Context context) throws JSONException {
		if (options != null && options.has("time")) {
			JSONObject time = options.getJSONObject("time");
			int key = 0;
			if (options.has("key")) {
				key = options.getInt("key");
			}
			Calendar alarmTime = WakeupPlugin.getOneTimeAlarmDate(time);

			AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, WakeupReceiver.class);
			if (options.has("seconds")) {
				intent.putExtra("seconds", options.getInt("seconds"));
			}
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context, key, intent, PendingIntent.FLAG_IMMUTABLE);
			if (alarmMgr != null) {
				alarmMgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), alarmIntent);
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sendPluginCallback(callbackContext, key, "setting alarm at " + sdf.format(alarmTime.getTime()));
		} else {
			callbackContext.error("Expected time argument.");
		}
	}


	protected static void cancelAlarms(CallbackContext callbackContext, JSONObject options, Context context) throws JSONException {
		Log.d(LOG_TAG, "canceling alarms");
		int key = 0;
		if (options.has("key")) {
			key = options.getInt("key");
		}
		Intent intent = new Intent(context, WakeupReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, key, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Log.d(LOG_TAG, "cancelling alarm id: " + key);
		assert alarmManager != null;
		alarmManager.cancel(sender);

		sendPluginCallback(callbackContext, key, "cancelling alarm id: " + key);
	}

	private static void sendPluginCallback(CallbackContext callbackContext, int key, String message) {
		callbackContext.success(message);
		WakeupPlugin.connectionCallbackContext = callbackContext;
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
		pluginResult.setKeepCallback(true);
		callbackContext.sendPluginResult(pluginResult);
	}


	protected static Calendar getOneTimeAlarmDate( JSONObject time) throws JSONException {
		TimeZone defaultz = TimeZone.getDefault();
		Calendar calendar = new GregorianCalendar(defaultz);
		Calendar now = new GregorianCalendar(defaultz);
		now.setTime(new Date());
		calendar.setTime(new Date());

		int hour=(time.has("hour")) ? time.getInt("hour") : -1;
		int minute=(time.has("minute")) ? time.getInt("minute") : 0;

		if(hour>=0){
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND,0);

			if (calendar.before(now)){
				calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
			}
		}else{
			calendar=null;
		}
		return calendar;
	}
}
