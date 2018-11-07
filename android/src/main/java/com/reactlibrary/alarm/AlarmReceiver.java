package com.reactlibrary.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.provider.Settings;
import java.io.IOException;
import static android.R.attr.type;

public class AlarmReceiver extends BroadcastReceiver {
    public static final String ALARM = "com.reactlibrary.AndroidAlarmReceiver.ALARM";
    public static final long MINUTE = 60 * 1000;
    public static final long DAY = 24 * 60 * MINUTE;
    final static Uri uri = Settings.System.DEFAULT_NOTIFICATION_URI;
    static MediaPlayer player = new MediaPlayer();
    static CountDownTimer timer = null;
    static PendingIntent alarmIntent = null;
    static NotificationChannel channel = null;

    public static void addAlarm(Context context, long startTime, String title, String message) {
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(AlarmReceiver.ALARM);
        intent.setClass(context, AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("startTime", startTime);
        alarmIntent = PendingIntent.getBroadcast(context, type, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 21) {
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(startTime, alarmIntent);
            alarmManager.setAlarmClock(info, alarmIntent);
        }
        else if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, startTime, alarmIntent);
        }
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, alarmIntent);
        }

        SharedPreferences pref = context.getSharedPreferences("alarm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("on", true);
        editor.putLong("startTime", startTime);
        editor.putString("title", title);
        editor.putString("message", message);
        editor.commit();
    }

    public static void removeAlarm(Context context) {
        if (alarmIntent == null) return;
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
        AlarmReceiver.stopSound();
        alarmIntent = null;

        SharedPreferences pref = context.getSharedPreferences("alarm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("on", false);
        editor.commit();
    }

    private static void startSound(Context context) {
        try {
            player.setAudioStreamType(AudioManager.STREAM_ALARM);
            player.setDataSource(context, uri);
            player.setLooping(true);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    player.start();
                    if (timer != null) AlarmReceiver.cancelTimer();
                    timer = new CountDownTimer(30000, 1000) {
                        public void onTick(long millsUntilFinished) {}
                        public void onFinish() { AlarmReceiver.stopSound(); }
                    }.start();
                }
            });
        }
        catch(IOException e) {
        }
    }

    private static void stopSound() {
        if (player.isPlaying()) {
            player.stop();
            player.reset();
        }
    }

    private static void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras().getBoolean("stop")) {
            AlarmReceiver.cancelTimer();
            AlarmReceiver.stopSound();
        }
        else {
            long startTime = intent.getLongExtra("startTime", 0);
            String title = intent.getStringExtra("title");
            String message = intent.getStringExtra("message");

            // set next alarm
            AlarmReceiver.addAlarm(context, startTime + DAY, title, message);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Resources res = context.getResources();
            String packageName = context.getPackageName();
            int smallIconId = res.getIdentifier("ic_launcher", "mipmap", packageName);
            String channelId = "react-native-android-alarm";

            Notification.Builder notificationBuilder = new Notification.Builder(context)
                    .setSmallIcon(smallIconId)
                    .setVibrate(new long[]{0, 6000})
                    .setContentTitle(title)
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setFullScreenIntent(pendingIntent, true)
                    .setContentIntent(createOnDismissedIntent(context))
                    .setDeleteIntent(createOnDismissedIntent(context))
                    .setAutoCancel(true);
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (channel == null) {
                    channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
                    manager.createNotificationChannel(channel);
                }
                notificationBuilder.setChannelId(channelId);
            }
            manager.notify(1, notificationBuilder.build());

            AlarmReceiver.startSound(context);
        }
    }

    private PendingIntent createOnDismissedIntent(Context context) {
        Intent intent = new Intent(AlarmReceiver.ALARM);
        intent.setClass(context, AlarmReceiver.class);
        intent.putExtra("stop", true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        return pendingIntent;
    }

}
