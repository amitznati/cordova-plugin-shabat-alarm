package com.az.cordova.plugin.wakeupplugin;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WakeupReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "WakeupReceiver";

    MediaPlayer mp;
    @SuppressLint("SimpleDateFormat")
    @Override
    public void onReceive(Context context, Intent intent) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.d(LOG_TAG, "wakeuptimer expired at " + sdf.format(new Date().getTime()));
        try {

            String packageName = context.getPackageName();
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (launchIntent != null && launchIntent.getComponent() != null) {
                String className = launchIntent.getComponent().getClassName();
                Log.d(LOG_TAG, "launching activity for class " + className);

                @SuppressWarnings("rawtypes")
                Class c = Class.forName(className);

                Intent i = new Intent(context, c);
                i.putExtra("wakeup", true);
                Bundle extrasBundle = intent.getExtras();
                Integer seconds = 10;
                if (extrasBundle != null && extrasBundle.get("seconds") != null) {
                    seconds = (Integer) extrasBundle.get("seconds");
                }
                Log.d(LOG_TAG, "seconds " + seconds);
                mp = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
                mp.setLooping(true);
                mp.start();
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                mp.stop();
                            }
                        },
                        seconds * 1000);
                Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
