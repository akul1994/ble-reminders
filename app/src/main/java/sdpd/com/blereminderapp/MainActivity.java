package sdpd.com.blereminderapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements BeaconDistanceListener {

    BluetoothAdapter mBluetoothAdapter;
    BluetoothConnectionManager mConnectionManager;
    private boolean mIsScanning;
    private Menu mMenu;

    private int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mConnectionManager = new BluetoothConnectionManager(getApplicationContext(), mLeScanCallback, this);
        mBluetoothAdapter = mConnectionManager.getBluetoothAdapter();

        checkForBluetooth();
    }

    public void checkForBluetooth(){
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

        switch (item.getItemId()){
            case R.id.toggle_scan:
                if (mIsScanning) {
                    mConnectionManager.stopScan();
                    mMenu.findItem(R.id.toggle_scan).setTitle(R.string.start_scan);
                } else {
                    mConnectionManager.startScan();
                    mMenu.findItem(R.id.toggle_scan).setTitle(R.string.stop_scan);
                }
                mIsScanning = !mIsScanning;
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void closestBeaconUpdated(String beaconAddress) {
        // TODO
    }
}
