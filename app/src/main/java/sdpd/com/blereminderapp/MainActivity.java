package sdpd.com.blereminderapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AddReminderListener {

    private FloatingActionButton mFAB;
    private AddReminderFragment addReminderFragment;
    private CoordinatorLayout coordinatorLayout;
    private Firebase reminderRef;
    private String uid;
    private ArrayList<Reminder> remList;
    private ListView remListView;
    private ReminderListAdapter reminderListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        fetchReminders();
    }

    private void initialize()
    {
        remList=new ArrayList<Reminder>();
        mFAB=(FloatingActionButton)findViewById(R.id.fab);
        mFAB.setOnClickListener(this);
        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        Firebase.setAndroidContext(this);
        uid=getIntent().getStringExtra(AppConstants.UID_INTENT);
        reminderRef=new Firebase(AppConstants.URL_FIREBASE).child(AppConstants.USERS).child(uid).child(AppConstants.REMINDERS);;
        reminderRef.child(uid);
        remListView=(ListView)findViewById(R.id.reminderList);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab:
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                addReminderFragment = new AddReminderFragment();
                addReminderFragment.setAddReminderListener(this);
                ft.add(R.id.content_frame,addReminderFragment);
                ft. commit();
                fm.executePendingTransactions();
            break;
        }
    }

    @Override
    public void onReminderAdded(Reminder rem) {
        try {

        getSupportFragmentManager().beginTransaction().remove(addReminderFragment).commit();
       reminderRef.push().setValue(rem);
            remList.add(rem);
            reminderListAdapter.notifyDataSetChanged();
        Snackbar.make(coordinatorLayout, "Reminder Succesfully added", Snackbar.LENGTH_SHORT).show();
        Log.d("info",rem.toString());
            }
            catch(Exception e)
            {
                e.printStackTrace();
                showError("Something went wrong.Please try again later");
            }
    }

    public void showError(String message)
    {
        AlertDialog dialog;
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(R.string.error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog=builder.create();
        dialog.show();
    }

    public void fetchReminders()
    {
        Query queryRef = reminderRef.orderByChild("date");
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("info", postSnapshot.toString());
                    remList.add(postSnapshot.getValue(Reminder.class));
                    setupListView();
                }
                Log.d("info", remList.size() + "");


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    void setupListView()
    {
        reminderListAdapter=new ReminderListAdapter(this,remList);
        remListView.setAdapter(reminderListAdapter);
    }
}
