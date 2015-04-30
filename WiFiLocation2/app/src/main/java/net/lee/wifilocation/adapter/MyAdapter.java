package net.lee.wifilocation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.wifilocation.R;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.AreaInfo;

import java.util.List;

/**
 * Created by lenovo on 2015/4/19.
 */
public class MyAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;

    public MyAdapter(Context context, List<String> list)
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
        ViewHolder viewHolder = null;

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_item,null);

            viewHolder = new ViewHolder();
            viewHolder.nameTv = (TextView) convertView.findViewById(R.id.areaListItemTv);
            viewHolder.flagIv = (ImageView) convertView.findViewById(R.id.areaListItemIv);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String areaName = (String) getItem(position);

        viewHolder.nameTv.setText(areaName);
//        Log.i("MyAdapter","item.name " + areaListItem.getAreaName() + "\nitem.id " + areaListItem.getAreaId());
        //Set the STAR_ON only the item was select
        if(areaName == Config.valueManageSelectedAreaName)
        {
            viewHolder.flagIv.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.flagIv.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    class ViewHolder
    {
        TextView nameTv;
        ImageView flagIv;
    }

}
