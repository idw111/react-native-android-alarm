package com.reactlibrary.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            SharedPreferences pref = context.getSharedPreferences("efilsleep_alarm", Context.MODE_PRIVATE);
            Boolean on = pref.getBoolean("on", false);
            if (on) {
                long currentTime = System.currentTimeMillis();
                long startTime = pref.getLong("startTime", 0);
                while (startTime < currentTime) startTime += AndroidAlarmReceiver.DAY;
                String title = pref.getString("title", "efil sleep");
                String message = pref.getString("message", "Alarm");
                AndroidAlarmReceiver.addAlarm(context, startTime, title, message);

                SharedPreferences.Editor editor = pref.edit();
                editor.putLong("startTime", startTime);
                editor.commit();
            }
        }
    }
}
