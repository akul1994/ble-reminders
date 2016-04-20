package sdpd.com.blereminderapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aman on 4/14/2016.
 */
public class BluetoothConnectionManager {

    private final String TAG = "BluetoothConnectionManager";

    Context mContext;
    private final BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private ArrayList<BluetoothDevice> mAvailableDevices;
    private ArrayList<String> mKnownDeviceAddresses;
    private HashMap<String, Double> mDeviceToDistanceMap;
    private BeaconDistanceListener mBeaconDistanceListener;

    private double minDistance = Double.MAX_VALUE;
    private String closestDeviceAddress = "";

    public BluetoothConnectionManager(Context context, BluetoothAdapter.LeScanCallback callback, BeaconDistanceListener listener) {
        mContext = context;
        mLeScanCallback = callback;
        mBeaconDistanceListener = listener;
        mAvailableDevices = new ArrayList<>();
        mDeviceToDistanceMap = new HashMap<>();
        mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        mKnownDeviceAddresses = new ArrayList<>();
        mKnownDeviceAddresses.add("BC:6A:29:AC:99:F3");
        mKnownDeviceAddresses.add("");
//        mKnownDeviceAddresses.add("");
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void startScan() {
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    public void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    public void deviceAvailable(BluetoothDevice device, int rssi) {
        Log.d(TAG, "Device Available " + device.getAddress() + " Rssi = " + rssi);

        double distance = getDistanceFromRssi(rssi);
        Log.d(TAG, "Distance = " + distance + " Min = " + minDistance);

        if (mKnownDeviceAddresses.contains(device.getAddress()) && !mDeviceToDistanceMap.containsKey(device.getAddress())) {
            mDeviceToDistanceMap.put(device.getAddress(), -1.0);
        } else {
            mDeviceToDistanceMap.put(device.getAddress(), distance);
        }

        Map.Entry<String, Double> minEntry = null;

        for (Map.Entry<String, Double> entry : mDeviceToDistanceMap.entrySet()) {
            if (minEntry == null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
                minEntry = entry;
            }
        }

        if (minEntry != null) {
            minDistance = minEntry.getValue();
            String address = minEntry.getKey();
            if (!closestDeviceAddress.equals(address)) {
                mBeaconDistanceListener.closestBeaconUpdated(address);
                closestDeviceAddress = address;
            }
        }

        Log.d(TAG, "Closest Device Address = " + closestDeviceAddress);
    }

    public String getClosestDeviceAddress() {
        return closestDeviceAddress;
    }

    public double getDistanceFromRssi(double rssi) {
        return Math.pow(10.0, ((rssi - (-54.0)) / -22.5));
    }
}

interface BeaconDistanceListener {
    public void closestBeaconUpdated(String beaconAddress);
}
