
package com.reactlibrary;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.reactlibrary.alarm.AlarmReceiver;

import java.util.Calendar;

public class RNAndroidAlarmModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNAndroidAlarmModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNAndroidAlarm";
  }

  @ReactMethod
  public void addAlarm(String time, String title, String message, final Promise promise) {
    int hour = Integer.parseInt(time.substring(0, 2));
    int minute = Integer.parseInt(time.substring(3));
    long currentTime = System.currentTimeMillis();
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(currentTime);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, 0);
    long startTime = currentTime > calendar.getTimeInMillis() ? calendar.getTimeInMillis() + AlarmReceiver.DAY : calendar.getTimeInMillis();
    AlarmReceiver.addAlarm(this.reactContext, startTime, title, message);
    promise.resolve(null);
  }

  @ReactMethod
  public void removeAlarm(final Promise promise) {
    AlarmReceiver.removeAlarm(this.reactContext);
    promise.resolve(null);
  }
}