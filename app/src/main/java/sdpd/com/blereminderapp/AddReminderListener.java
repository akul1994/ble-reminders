package sdpd.com.blereminderapp;

/**
 * Created by Akul on 18-04-2016.
 */
public interface AddReminderListener {
    public void onReminderAdded(Reminder rem);
    public void onReminderSelected(int position);
    public void onReminderDone(int position);
}
