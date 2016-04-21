package sdpd.com.blereminderapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class ReminderInfoActivity extends AppCompatActivity {

    private Reminder rem;
    private String mKey;
    private TextView remTitle,remDate,remSTime,remETime,remLocation,remNotes,remAlert;
    private Button doneButton,delButton;
    Firebase reminderRef;
    SharedPreferences sharedPreferences;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_info);
        remTitle=(TextView)findViewById(R.id.reminderTitleText);
        remDate=(TextView)findViewById(R.id.dateText);
        remSTime=(TextView)findViewById(R.id.startTextView);
        remETime=(TextView)findViewById(R.id.endTextView);
        remLocation=(TextView)findViewById(R.id.locationText);
        remNotes=(TextView)findViewById(R.id.notesText);
        remAlert=(TextView)findViewById(R.id.alertText);
        rem=getIntent().getParcelableExtra(AppConstants.REMINDER_INTENT);
        mKey=getIntent().getStringExtra(AppConstants.KEY_INTENT);
        doneButton=(Button)findViewById(R.id.doneButton);
        delButton=(Button)findViewById(R.id.delButton);
        Firebase.setAndroidContext(this);
       sharedPreferences = getSharedPreferences(AppConstants.SHARED_PREF, Context.MODE_PRIVATE);
        userId=sharedPreferences.getString(AppConstants.UID,null);
        if(userId!=null)
        reminderRef = new Firebase(AppConstants.URL_FIREBASE).child(AppConstants.USERS).child(userId).child(AppConstants.REMINDERS);
        if(rem!=null)
        {
            remTitle.setText(rem.title);
            remDate.setText(rem.date);
            remSTime.setText(rem.startTime);
            remETime.setText(rem.endTime);
            remLocation.setText(Utils.getLocationName(rem.locationId));
            remNotes.setText(rem.notes);
            if(rem.alertTime!=-1)
                remAlert.setText(rem.alertTime+" minutes earlier and on location");
            else
                remAlert.setText("Only on location");
        }
    doneButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });
    delButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mKey!=null&&reminderRef!=null) {
                reminderRef.child(mKey).removeValue();
                setResult(2);
                finish();
            }
        }
    });
    }
}
