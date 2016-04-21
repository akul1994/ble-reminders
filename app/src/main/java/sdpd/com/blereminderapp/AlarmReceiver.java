package sdpd.com.blereminderapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Aman on 4/19/2016.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarm received");
        Reminder rem = intent.getParcelableExtra(AppConstants.REMINDER_INTENT);
        String key = intent.getStringExtra(AppConstants.KEY_INTENT);
        Utils.displayNotification(context,rem,key);
    }

}
