package com.ziver.bluetoothnavigation.bluetooth;

public interface IBluetoothScanManager {
    void setBluetoothScanListener(BluetoothScanListener listener);
    void startScan();
    void stopScan();
}
