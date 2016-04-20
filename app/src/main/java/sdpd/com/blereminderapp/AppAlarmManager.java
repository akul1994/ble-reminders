package sdpd.com.blereminderapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Aman on 4/19/2016.
 */
public class AppAlarmManager {

    Context mContext;
    AlarmManager mAlarmManager;

    public AppAlarmManager(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(Reminder reminder) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date;
        try {
            date = df.parse(reminder.date + " " + reminder.startTime);
            long time = date.getTime();
            time = time - (reminder.alertTime == -1 ? 0 : reminder.alertTime * 60);
            Log.d("AlarmManager", "Setting alarm for time " + time);

            Intent intent = new Intent(mContext, AlarmReceiver.class);
            intent.putExtra(AppConstants.TITLE, reminder.title);
            intent.putExtra(AppConstants.LOCATION_ID, reminder.locationId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 123, intent, 0);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
