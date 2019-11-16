package com.ziver.bluetoothnavigation.ui.fragment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziver.bluetoothnavigation.R;
import com.ziver.bluetoothnavigation.math.Point;
import com.ziver.bluetoothnavigation.ui.fragment.model.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder> {

    private List<BluetoothDevice> mList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater from = LayoutInflater.from(parent.getContext());
        View view = from.inflate(R.layout.item_device_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getData(position));
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    private BluetoothDevice getData(int position) {
        return mList != null && position < mList.size() ? mList.get(position) : null;
    }

    public void replaceDataSet(List<BluetoothDevice> list) {
        mList = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setData(@NonNull BluetoothDevice data) {
            ((TextView) this.itemView.findViewById(R.id.deviceNameView)).setText(data.getName());
            ((TextView) this.itemView.findViewById(R.id.deviceAddressView)).setText(data.getAddress());
            ((TextView) this.itemView.findViewById(R.id.deviceRssiView)).setText(String.valueOf(data.getRSSI()) + " dBm");
            ((TextView) this.itemView.findViewById(R.id.distanceView)).setText(String.valueOf(data.getDistance()) + " m");
            Point point = data.getEddistoneCalc().getPoint();
            ((TextView) this.itemView.findViewById(R.id.xView)).setText("X " + String.valueOf(point.getX()));
            ((TextView) this.itemView.findViewById(R.id.yView)).setText("Y " + String.valueOf(point.getY()));
        }
    }
}
