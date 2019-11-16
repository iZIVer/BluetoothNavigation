package com.ziver.bluetoothnavigation.ui.activity;

import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.ziver.bluetoothnavigation.Logger;
import com.ziver.bluetoothnavigation.R;
import com.ziver.bluetoothnavigation.bluetooth.BluetoothLeScanManager;
import com.ziver.bluetoothnavigation.bluetooth.BluetoothScanListener;
import com.ziver.bluetoothnavigation.bluetooth.BluetoothScanManager;
import com.ziver.bluetoothnavigation.bluetooth.IBluetoothScanManager;
import com.ziver.bluetoothnavigation.bluetooth.model.Device;
import com.ziver.bluetoothnavigation.bluetooth.model.ScanState;
import com.ziver.bluetoothnavigation.math.Point;
import com.ziver.bluetoothnavigation.ui.fragment.DistanceCalculation;
import com.ziver.bluetoothnavigation.ui.fragment.adapter.DeviceListAdapter;
import com.ziver.bluetoothnavigation.ui.fragment.model.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private IBluetoothScanManager mIBluetoothScanManager;

    private FloatingActionButton mScanButton;

    private SwipeRefreshLayout mRefreshView;
    private DeviceListAdapter mDeviceListAdapter;
    private Map<String, BluetoothDevice> mList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUi();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mIBluetoothScanManager = new BluetoothLeScanManager(getApplicationContext());
        } else {
            mIBluetoothScanManager = new BluetoothScanManager(getApplicationContext());
        }
        mIBluetoothScanManager.setBluetoothScanListener(new BluetoothScanListener() {
            @Override
            public void onChangeScanSate(@NonNull final ScanState state) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.debug(state.name());
                        if (state == ScanState.UNSCAN) {
                            mRefreshView.setRefreshing(false);
                            mScanButton.setEnabled(true);
                            updateDeviceList();
                        }

                        if (state == ScanState.SCAN) {
                            mRefreshView.setRefreshing(true);
                            mScanButton.setEnabled(false);
                        }
                    }
                });
            }

            @Override
            public void onScanFind(@NonNull Device device) {
                Logger.debug(device.toString());
                BluetoothDevice bluetoothDevice = mList.get(device.getAddress());
                if (bluetoothDevice != null) {
                    bluetoothDevice.setRSSI(device.getRSSI());
                    mList.put(device.getAddress(), bluetoothDevice);
                } else
                    mList.put(device.getAddress(), new BluetoothDevice(device));
                updateDeviceList();
            }
        });
    }

    private void updateDeviceList() {
        ArrayList list1 = new ArrayList(mList.values());
        Comparator<BluetoothDevice> comparator = new Comparator<BluetoothDevice>() {
            @Override
            public int compare(BluetoothDevice b1, BluetoothDevice b2) {
                return Double.compare(b1.getDistance(), b2.getDistance());
            }
        };
        Collections.sort(list1, comparator);
        if (mList.size() == 8) {
            Point point = new DistanceCalculation().calculation(list1);
            if (point != null) {
                ((TextView) findViewById(R.id.resultView)).setText(
                        String.format("X: %s Y: %s", String.valueOf(point.getX()), String.valueOf(point.getY()))
                );

                ViewGroup grid  = findViewById(R.id.gridView);
                grid.setPadding(
                        dpToPx((int) (200 * (point.getY() / 8))),
                        dpToPx(300 -(int) (300 * (point.getX() / 15))),
                        0, 0
                );
            }
            Logger.debug(String.valueOf(point));
        }

        mDeviceListAdapter.replaceDataSet(list1);
    }

    private int dpToPx(int dp) {
        return dp * getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT;
    }

    private void initUi() {
        mRefreshView = findViewById(R.id.refreshView);
        mRefreshView.setEnabled(false);

        RecyclerView deviceRecyclerView = findViewById(R.id.deviceRecyclerView);
        mDeviceListAdapter = new DeviceListAdapter();
        deviceRecyclerView.setAdapter(mDeviceListAdapter);

        mScanButton = findViewById(R.id.fab);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIBluetoothScanManager.startScan();
            }
        });
    }

    @Override
    public void onDestroy() {
        mIBluetoothScanManager.stopScan();
        super.onDestroy();
    }
}
