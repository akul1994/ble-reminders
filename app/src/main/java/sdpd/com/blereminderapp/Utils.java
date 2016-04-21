package sdpd.com.blereminderapp;

import android.content.Context;
import android.content.SharedPreferences;

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
}
