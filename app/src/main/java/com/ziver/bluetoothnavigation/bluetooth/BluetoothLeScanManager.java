package com.ziver.bluetoothnavigation.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.neovisionaries.bluetooth.ble.advertising.ADPayloadParser;
import com.neovisionaries.bluetooth.ble.advertising.ADStructure;
import com.neovisionaries.bluetooth.ble.advertising.EddystoneUID;
import com.ziver.bluetoothnavigation.Logger;
import com.ziver.bluetoothnavigation.bluetooth.model.Device;
import com.ziver.bluetoothnavigation.bluetooth.model.ScanState;
import com.ziver.bluetoothnavigation.math.Point;
import com.ziver.bluetoothnavigation.ui.fragment.model.EddistoneCalc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public final class BluetoothLeScanManager implements IBluetoothScanManager {

    private static final ParcelUuid EDDYSTONE_SERVICE_UUID = ParcelUuid.fromString("0000FEAA-0000-1000-8000-00805F9B34FB");

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothScanListener mBluetoothScanListener;
    private boolean isBluetoothDiscovering;
    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (mBluetoothScanListener != null) {
                BluetoothDevice device = result.getDevice();
                onLeScan(device, result.getRssi(), result.getScanRecord().getBytes());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    public BluetoothLeScanManager(@NonNull Context context) {
        mBluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Logger.exception(new RuntimeException("BluetoothAdapter not found"));
            return;
        }
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    @Override
    public void setBluetoothScanListener(BluetoothScanListener listener) {
        mBluetoothScanListener = listener;
    }

    @Override
    public void startScan() {
        if (this.isBluetoothDiscovering) {
            return;
        }
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Logger.exception(new RuntimeException("BluetoothAdapter not found or disabled"));
            return;
        }
        this.isBluetoothDiscovering = true;
        if (mBluetoothScanListener != null) {
            mBluetoothScanListener.onChangeScanSate(ScanState.SCAN);
        }
        List<ScanFilter> l = new ArrayList<>();
        List<ScanFilter> scanFilters = new ArrayList<>();
        scanFilters.add(new ScanFilter.Builder()
                .setServiceUuid(EDDYSTONE_SERVICE_UUID)
                .build()
        );
        ScanSettings.Builder builderScanSettings = new ScanSettings.Builder();
        builderScanSettings.setScanMode(ScanSettings.SCAN_MODE_BALANCED);
        builderScanSettings.setReportDelay(TimeUnit.SECONDS.toMillis(0));
        mBluetoothLeScanner.startScan(scanFilters, builderScanSettings.build(), mLeScanCallback);
    }

    @Override
    public void stopScan() {
        if (!this.isBluetoothDiscovering) {
            return;
        }
        this.isBluetoothDiscovering = false;
        if (mBluetoothScanListener != null) {
            mBluetoothScanListener.onChangeScanSate(ScanState.UNSCAN);
        }
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Logger.exception(new RuntimeException("BluetoothAdapter not found or disabled"));
            return;
        }
        mBluetoothLeScanner.stopScan(mLeScanCallback);
    }

    private void onLeScan(@NonNull BluetoothDevice device, int rssi, byte[] scanRecord) {
        List<ADStructure> structures = ADPayloadParser.getInstance().parse(scanRecord);
        for (ADStructure structure : structures) {
            if (structure instanceof EddystoneUID) {
                EddystoneUID es = (EddystoneUID) structure;
                String devId = es.getInstanceIdAsString();
                Device device1 = new Device(
                        device.getAddress(),
                        device.getName(),
                        rssi + 24);
                int id = Integer.parseInt(devId.substring(4), 16);
                int x = Integer.parseInt(devId.substring(4, 8), 16) / 100;
                int y = Integer.parseInt(devId.substring(8, 12), 16) / 100;
                Point point = new Point(x, y);
                device1.setEddistoneCalc(new EddistoneCalc(
                        id,
                        point));
                mBluetoothScanListener.onScanFind(device1);
            }
        }
    }
}
