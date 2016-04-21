package sdpd.com.blereminderapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Akul on 20-04-2016.
 */
public  class Utils {

    public static ArrayList<Reminder> getReminderList(Context context,String locationId)
    {
        Gson gson=new Gson();
        SharedPreferences sharedpreferences = context.getSharedPreferences(AppConstants.SHARED_PREF, Context.MODE_PRIVATE);
        switch (locationId)
        {
            case "0":
                ArrayList<Reminder> rem_a=new ArrayList<>();
                String json=sharedpreferences.getString(AppConstants.REM_A,null);
                if(json!=null)
                {
                    Type type = new TypeToken<ArrayList<Reminder>>(){}.getType();
                    rem_a= gson.fromJson(json, type);

                }
                return  rem_a;
            case "1":
                ArrayList<Reminder> rem_b=new ArrayList<>();
                json=sharedpreferences.getString(AppConstants.REM_B,null);
                if(json!=null)
                {
                    Type type = new TypeToken<ArrayList<Reminder>>(){}.getType();
                    rem_b= gson.fromJson(json, type);
                }
                return rem_b;
            case "2": json=sharedpreferences.getString(AppConstants.REM_C,null);
                ArrayList<Reminder> rem_c=new ArrayList<>();
                if(json!=null)
                {
                    Type type = new TypeToken<ArrayList<Reminder>>(){}.getType();
                    rem_c= gson.fromJson(json, type);
                }
                return rem_c;
        }
        return null;
    }
    public static String getLocationName(String locId) {
        int id = Integer.parseInt(locId);
        switch (id) {
            case 0:
                return "A Wing";
            case 1:
                return "B Wing";
            case 2:
                return "C Wing";
        }
        return "No location set";
    }

    public static int getLocIdFromBeaconAddress(String address){
        if (address.equals("BC:6A:29:AC:99:F3")){
            return 0;
        } else if (address.equals("BC:6A:29:AB:78:5E")){
            return 1;
        } else {
            return 2;
        }
    }

    public static void displayNotification(Context context, ArrayList<Reminder> reminderList, String key){
        if (reminderList == null || reminderList.size() == 0){
            return;
        }
        if (reminderList.size() == 1){
            displayNotification(context, reminderList.get(0), key, false, null);
        } else {
            displayNotification(context, getMultipleReminderObject(reminderList.get(0).locationId), key, true, reminderList);
        }
    }

    public static Reminder getMultipleReminderObject(String locationId){
        Reminder reminder = new Reminder();
        reminder.title = "Reminders scheduled here.";
        reminder.locationId = locationId;
        return reminder;
    }

    public static void displayNotification(Context context, Reminder rem, String key){
        displayNotification(context, rem, key, false, null);
    }

    public static void displayNotification(Context context, Reminder rem, String key, boolean isMultiple, ArrayList<Reminder> reminders) {
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle(rem.title);
        builder.setContentText(Utils.getLocationName(rem.locationId));
        builder.setSmallIcon(R.drawable.abc_ic_search_api_mtrl_alpha);

        Intent intent;
        if (isMultiple) {
            intent = new Intent(context, MainActivity.class);
            intent.putExtra(AppConstants.INTENT_FROM_NOTIF, true);
            intent.putParcelableArrayListExtra(AppConstants.REMINDER_LIST, reminders);
        }
        else {
            intent = new Intent(context, ReminderInfoActivity.class);
            intent.putExtra(AppConstants.REMINDER_INTENT, rem);
            intent.putExtra(AppConstants.KEY_INTENT, key);
        }
        intent.putExtra(AppConstants.REMINDER_INTENT, rem);
        intent.putExtra(AppConstants.KEY_INTENT, key);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(AppConstants.NOTIFICATION_ID, notification);
    }


}
