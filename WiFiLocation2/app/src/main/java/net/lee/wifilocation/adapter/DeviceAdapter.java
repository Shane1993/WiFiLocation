package net.lee.wifilocation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lenovo.wifilocation.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2015/5/4.
 */
public class DeviceAdapter extends BaseAdapter {

    private ArrayList<String> devicesList;
    private Context context;
    private TextView userName;

    public DeviceAdapter(Context context, ArrayList<String> devicesList)
    {
        this.context = context;
        this.devicesList = devicesList;
    }

    @Override
    public int getCount() {
        return devicesList.size();
    }

    @Override
    public Object getItem(int position) {
        return devicesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflator = LayoutInflater.from(context);
        convertView = inflator.inflate(R.layout.device_list_item,null);

        userName = (TextView) convertView.findViewById(R.id.deviceNameListItemTv);

        userName.setText(getItem(position).toString());

        return convertView;
    }
}
