package sdpd.com.blereminderapp;

/**
 * Created by Akul on 20-03-2016.
 */
public class AppConstants {

    public static String URL_FIREBASE = "https://burning-torch-2399.firebaseio.com";
    public static final String USERS = "users";
    public static final String INFO = "info";
    public static final String REMINDERS = "reminders";
    public static final String UID_INTENT = "uid_intent";
    public static final String USER_INTENT = "user_intent";

    public static final String TITLE = "title";
    public static final String LOCATION_ID = "loc_id";

    public static final int LOC_A = 0;
    public static final int LOC_B = 1;
    public static final int LOC_C = 2;

    public static final int NOTIFICATION_ID = 1;

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
        return "";
    }
}
