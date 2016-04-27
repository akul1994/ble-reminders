package sdpd.com.blereminderapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Akul on 20-03-2016.
 */
public class AppConstants {

    public static final String KEY_INTENT ="KEY" ;
    public static String URL_FIREBASE = "https://burning-torch-2399.firebaseio.com";
    public static final String USERS = "users";
    public static final String INFO = "info";
    public static final String REMINDERS = "reminders";
    public static final String UID_INTENT = "uid_intent";
    public static final String USER_INTENT = "user_intent";
    public static final String REMINDER_INTENT="reminder_intent";
    public static final String INTENT_FROM_NOTIF = "from_notif";

    public static final String REM_A = "REM_A_WING";
    public static final String REM_B = "REM_B_DOME";
    public static final String REM_C = "REM_C_WING";
    public static final String SHARED_PREF="shared_pref";

    public static final int NOTIFICATION_ID = 1;

    public static final String REMINDER_LIST = "reminder_list";

    public static final Double DISTANCE_THRESHOLD = 10D;
    public static final long TIME_THRESHOLD = 5 * 1000;
}
