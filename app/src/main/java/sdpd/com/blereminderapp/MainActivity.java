package sdpd.com.blereminderapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AddReminderListener, BeaconDistanceListener {

    private FloatingActionButton mFAB;
    private AddReminderFragment addReminderFragment;
    private CoordinatorLayout coordinatorLayout;
    private Firebase reminderRef;
    private String uid;
    private ArrayList<Reminder> remList;
    private ListView remListView;
    private ReminderListAdapter reminderListAdapter;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothConnectionManager mConnectionManager;
    private boolean mIsScanning;
    private Menu mMenu;
    private AppAlarmManager mAppAlarmManager;
    SharedPreferences sharedpreferences;
    private ArrayList<String> remKeys;
    private Handler mHandler;
    private String mCurrentMinAddress = "";
    private static final String TAG = "MainActivity";

    // Map of address to pair of sum and count.
    HashMap<String, Pair<Double, Double>> deviceToAvgMap;

    private int REQUEST_ENABLE_BT = 1;
    private boolean isFromNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnectionManager = new BluetoothConnectionManager(getApplicationContext(), mLeScanCallback, this);
        mBluetoothAdapter = mConnectionManager.getBluetoothAdapter();
        mAppAlarmManager = new AppAlarmManager(getApplicationContext());
        remList = new ArrayList<>();

        deviceToAvgMap = new HashMap<>();
        mHandler = new Handler();

        isFromNotification=getIntent().getBooleanExtra(AppConstants.INTENT_FROM_NOTIF, false);
        checkForBluetooth();

        initViews();

        if(isFromNotification) {
            remList=getIntent().getParcelableArrayListExtra(AppConstants.REMINDER_LIST);
            setupListView();
        }
        else {
            initData();
            fetchReminders();
        }

        sharedpreferences = getSharedPreferences(AppConstants.SHARED_PREF, Context.MODE_PRIVATE);
        remKeys=new ArrayList<>();

    }


    public void checkForBluetooth() {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mConnectionManager.deviceAvailable(device, rssi);
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.toggle_scan:
                if (mIsScanning) {
                    mConnectionManager.stopScan();
                    mMenu.findItem(R.id.toggle_scan).setTitle(R.string.start_scan);
                } else {
                    mConnectionManager.startScan();
                    mMenu.findItem(R.id.toggle_scan).setTitle(R.string.stop_scan);
                    mHandler.postDelayed(averageRunnable, AppConstants.TIME_THRESHOLD);
                }
                mIsScanning = !mIsScanning;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void closestBeaconUpdated(String beaconAddress, double distance) {
        if (!deviceToAvgMap.containsKey(beaconAddress)){
            deviceToAvgMap.put(beaconAddress, new Pair(distance, 1D));
        }
        else {
            double sum = (double)((Pair)deviceToAvgMap.get(beaconAddress)).first;
            double count = (double)((Pair)deviceToAvgMap.get(beaconAddress)).second;
            deviceToAvgMap.put(beaconAddress, new Pair(sum + distance, count + 1));
        }
//        ArrayList<Reminder> list = Utils.getReminderList(this, "" + Utils.getLocIdFromBeaconAddress(beaconAddress));
//        Utils.displayNotification(this, list, "");
    }

    Runnable averageRunnable = new Runnable() {
        @Override
            public void run() {
            if (mIsScanning) {
                Log.d(TAG, "Running averaging handler");
                String minAddress = "";
                double minAvg = Double.MAX_VALUE;
                if (deviceToAvgMap != null) {
                    minAvg = Double.MAX_VALUE;
                    String address = "";
                    Iterator iterator = deviceToAvgMap.keySet().iterator();
                    while (iterator.hasNext()) {
                        address = (String) iterator.next();
                        Pair<Double, Double> p = deviceToAvgMap.get(address);
                        double avg = p.first / p.second;
                        if (avg < minAvg) {
                            minAvg = avg;
                            minAddress = address;
                        }
                    }
                    deviceToAvgMap.clear();
                }

                if (!mCurrentMinAddress.equals(minAddress) && minAvg <= AppConstants.DISTANCE_THRESHOLD){
                    ArrayList<Reminder> list = Utils.getReminderList(getApplicationContext(), "" + Utils.getLocIdFromBeaconAddress(minAddress));
                    Utils.displayNotification(getApplicationContext(), list, "");
                    mCurrentMinAddress = minAddress;
                }
                mHandler.postDelayed(averageRunnable, AppConstants.TIME_THRESHOLD);
            }
        }
    };

    private void initViews(){
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.setOnClickListener(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        remListView=(ListView)findViewById(R.id.reminderList);
    }

    private void initData() {
        Firebase.setAndroidContext(this);
        uid = getIntent().getStringExtra(AppConstants.UID_INTENT);
        reminderRef = new Firebase(AppConstants.URL_FIREBASE).child(AppConstants.USERS).child(uid).child(AppConstants.REMINDERS);
        reminderRef.child(uid);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                addReminderFragment = new AddReminderFragment();
                addReminderFragment.setAddReminderListener(this);
                ft.add(R.id.content_frame, addReminderFragment);
                ft.commit();
                fm.executePendingTransactions();
                break;
        }
    }

    @Override
    public void onReminderAdded(Reminder rem) {
        try {
            getSupportFragmentManager().beginTransaction().remove(addReminderFragment).commit();
            Firebase newPushRef=reminderRef.push();
            newPushRef.setValue(rem);
            remList.add(rem);
            String key=newPushRef.getKey();
            remKeys.add(key);
        reminderListAdapter.setRemList(remList);

            if (rem.alertTime != -1)
                mAppAlarmManager.setAlarm(rem,key);
            addToLocationList(rem);

            Snackbar.make(coordinatorLayout, "Reminder Succesfully added", Snackbar.LENGTH_SHORT).show();
            Log.d("info", rem.toString());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Something went wrong.Please try again later");
        }
    }

    @Override
    public void onReminderSelected(int position) {
            Intent intent=new Intent(this,ReminderInfoActivity.class);
        intent.putExtra(AppConstants.REMINDER_INTENT,remList.get(position));
        intent.putExtra(AppConstants.KEY_INTENT,remKeys.get(position));
        startActivity(intent);
    }

    @Override
    public void onReminderDone(int position) {
        reminderRef.child(remKeys.get(position)).removeValue();
        remList.remove(position);
        reminderListAdapter.setRemList(remList);
    }

    public void showError(String message) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle(R.string.error);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    public void fetchReminders() {
        remList=new ArrayList<>();
        remKeys=new ArrayList<>();
        Query queryRef = reminderRef.orderByChild("date");
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("info", postSnapshot.toString());

                    remList.add(postSnapshot.getValue(Reminder.class));
                    remKeys.add(postSnapshot.getKey());
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
        reminderListAdapter.setAddReminderListener(this);
        remListView.setAdapter(reminderListAdapter);
    }

    public void addToLocationList(Reminder rem)
    {
        Gson gson=new Gson();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        ArrayList<Reminder> rem_a=Utils.getReminderList(this, "0");
        ArrayList<Reminder> rem_b=Utils.getReminderList(this,"1");
        ArrayList<Reminder> rem_c=Utils.getReminderList(this,"2");
        String json;
        switch (rem.locationId)
        {
            case "0":rem_a.add(rem);
                json = gson.toJson(rem_a);
                editor.putString(AppConstants.REM_A, json);
                break;
            case "1":rem_b.add(rem);
                json = gson.toJson(rem_b);
                editor.putString(AppConstants.REM_B, json);
                break;
            case "2":rem_c.add(rem);
                json = gson.toJson(rem_c);
                editor.putString(AppConstants.REM_C, json);
                break;
        }
        editor.commit();
    }


}
