package sdpd.com.blereminderapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ReminderInfoActivity extends AppCompatActivity {

    private Reminder rem;
    private String mKey;
    private TextView remTitle,remDate,remSTime,remETime,remLocation,remNotes,remAlert;
    private Button doneButton;

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
    }
}
