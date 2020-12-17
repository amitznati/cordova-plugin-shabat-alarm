package com.az.cordova.plugin.shabatalarms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import android.content.Context;



/**
 * This class echoes a string called from JavaScript.
 */
public class ShabatAlarms extends CordovaPlugin {

    protected static final String LOG_TAG = "ShabatAlarms";
    protected static final int ID_ONETIME_OFFSET = 10000;
    public static CallbackContext connectionCallbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        } else if (action.equals("setAlarm")) {
            JSONObject options = args.getJSONObject(0);
            this.setAlarm(options, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void setAlarm(JSONObject options, CallbackContext callbackContext) throws JSONException {
        if (options != null && options.has("time")) {
            Context context = cordova.getActivity().getApplicationContext();
            JSONObject time = options.getJSONObject("time");
            Calendar alarmTime = ShabatAlarms.getOneTimeAlarmDate(time);
            Intent intent = new Intent(context, ShabatAlarms.class);
            ShabatAlarms.setNotification(context, alarmTime, intent, ID_ONETIME_OFFSET);
            callbackContext.success(true);
        }
        callbackContext.error("Expected time argument.");
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

    @SuppressLint({ "SimpleDateFormat", "NewApi" })
    protected static void setNotification(Context context, Calendar alarmDate, Intent intent, int id) throws JSONException{
        if(alarmDate!=null){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Log.d(LOG_TAG,"setting alarm at " + sdf.format(alarmDate.getTime()) + "; id " + id);

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmDate.getTimeInMillis(), sender);

                if(ShabatAlarms.connectionCallbackContext!=null) {
                    JSONObject o=new JSONObject();
                    o.put("type", "set");
                    o.put("alarm_type", "set");
                    o.put("alarm_date", alarmDate.getTimeInMillis());

                    Log.d(LOG_TAG, "alarm time in millis: " + alarmDate.getTimeInMillis());

                    PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, o);
                    pluginResult.setKeepCallback(true);
                    ShabatAlarms.connectionCallbackContext.sendPluginResult(pluginResult);
                }
            }
        }
    }
}
