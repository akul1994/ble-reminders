package sdpd.com.blereminderapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Akul on 23-03-2016.
 */
public class User implements Parcelable {
    String fullName;
    String emailId;
    String phone;

    public User()
    {}

    public String getFullName() {
        return fullName;
    }


    public String getEmailId() {
        return emailId;
    }



    public String getPhone() {
        return phone;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeString(emailId);
        dest.writeString(phone);
    }

    private User(Parcel in)
    {
        fullName=in.readString();
        emailId=in.readString();
        phone=in.readString();
    }

    public static final Creator<User> CREATOR=new Creator<User>()
    {
        public User createFromParcel(Parcel in)
        {
            return new User(in);
        }
        public User[] newArray(int size)
        {
            return new User[size];
        }
    };
}
