package com.ziver.bluetoothnavigation.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.ziver.bluetoothnavigation.Logger;
import com.ziver.bluetoothnavigation.bluetooth.model.Device;
import com.ziver.bluetoothnavigation.bluetooth.model.ScanState;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public final class BluetoothScanManager implements IBluetoothScanManager {

    private final Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isBluetoothDiscovering;
    private BluetoothDeviceReceiver mBluetoothBroadcastReceiver;
    private BluetoothScanListener mBluetoothScanListener;

    private TimerTask mTimerTask;
    private Timer mTimer;

    public BluetoothScanManager(@NonNull Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Logger.exception(new RuntimeException("BluetoothAdapter not found"));
        }
    }

    @Override
    public void setBluetoothScanListener(BluetoothScanListener listener) {
        mBluetoothScanListener = listener;
    }

    @Override
    public void startScan() {
        mDeviceList.clear();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Logger.exception(new RuntimeException("BluetoothAdapter not found or disabled"));
            return;
        }

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        this.isBluetoothDiscovering = true;
        initBroadcastListener();
        if (mBluetoothScanListener != null) {
            mBluetoothScanListener.onChangeScanSate(ScanState.SCAN);
        }
        initTimerScan();
        mBluetoothAdapter.startDiscovery();
    }

    @Override
    public void stopScan() {
        this.isBluetoothDiscovering = false;
//        unregisterBluetoothReceiver();
        if (mBluetoothScanListener != null) {
            mBluetoothScanListener.onChangeScanSate(ScanState.UNSCAN);
        }
        cancelTimerScan();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Logger.exception(new RuntimeException("BluetoothAdapter not found or disabled"));
            return;
        }
        mBluetoothAdapter.cancelDiscovery();
    }

    private void initTimerScan() {
        cancelTimerScan();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (!mBluetoothAdapter.isDiscovering() && isBluetoothDiscovering)
                    stopScan();
            }
        };
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask,
                TimeUnit.SECONDS.toMillis(1),
                TimeUnit.SECONDS.toMillis(1)
        );
    }

    private void cancelTimerScan() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    private void initBroadcastListener() {
        if (mBluetoothBroadcastReceiver == null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.bluetooth.device.action.FOUND");
            filter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
            filter.addAction("android.bluetooth.device.action.UUID");
            mBluetoothBroadcastReceiver = new BluetoothDeviceReceiver();
            try {
                mContext.registerReceiver(mBluetoothBroadcastReceiver, filter);
            } catch (Exception e) {
                Logger.exception(e);
            }
        }
    }

    private void unregisterBluetoothReceiver() {
        if (mBluetoothBroadcastReceiver != null) {
            try {
                mContext.unregisterReceiver(mBluetoothBroadcastReceiver);
            } catch (IllegalArgumentException e) {
                Logger.exception(e);
                mBluetoothBroadcastReceiver = null;
            }
        }
    }

    List<BluetoothDevice> mDeviceList = new ArrayList<>();

    private final class BluetoothDeviceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(BluetoothDevice.ACTION_FOUND, action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                if (mBluetoothScanListener != null) {
                    Device device1 = new Device(address, name, rssi);
                    mDeviceList.add(device);
                    mBluetoothScanListener.onScanFind(device1);
                }
            } else if (TextUtils.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED, action)) {
                for (BluetoothDevice device : mDeviceList) {
                    if (TextUtils.isEmpty(device.getName())) {
                        device.fetchUuidsWithSdp();
                    }
                }
            } else if (TextUtils.equals(BluetoothDevice.ACTION_UUID, action)) {
                BluetoothDevice deviceExtra = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
            }
        }
    }
}
