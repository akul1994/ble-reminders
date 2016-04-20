package sdpd.com.blereminderapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akul on 18-04-2016.
 */
public class Reminder implements Parcelable{
    public String title;
    public String date;
    public String startTime;
    public String endTime;
    public String locationId;
    public String notes;
    public int alertTime;
    public String teamId;

    public Reminder()
    {}
    private Reminder(Parcel in)
    {

        title = in.readString();
        date = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        locationId = in.readString();
        notes = in.readString();
        alertTime = in.readInt();
        teamId = in.readString();
    }

    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(locationId);
        dest.writeString(notes);
        dest.writeInt(alertTime);
        dest.writeString(teamId);
    }
}
