package net.lee.wifilocation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lenovo.wifilocation.R;

import net.lee.wifilocation.model.DeviceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2015/5/5.
 */
public class DeviceLocationAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<DeviceLocation> list;
    private TextView areaNameTv,locationNameTv,timeTv;

    public DeviceLocationAdapter(Context context,ArrayList<DeviceLocation> list)
    {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.device_location_list_item,null);

        areaNameTv = (TextView) convertView.findViewById(R.id.areaNameListItemTv);
        locationNameTv = (TextView) convertView.findViewById(R.id.locationNameListItemTv);
        timeTv = (TextView) convertView.findViewById(R.id.timeListItemTv);

        DeviceLocation deviceLocation = (DeviceLocation) getItem(position);
        areaNameTv.setText(deviceLocation.getAreaName());
        locationNameTv.setText(deviceLocation.getLocationName());
        timeTv.setText(deviceLocation.getTime());

        return convertView;

    }

}
