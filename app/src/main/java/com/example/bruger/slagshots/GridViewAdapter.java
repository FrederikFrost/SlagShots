package com.example.bruger.slagshots;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.widget.ImageView;
import android.widget.GridView;


/**
 * Created by erknt on 18-06-2019.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private GameModel model;

    public GridViewAdapter(Context c, GameModel model) {
        mContext = c;
        this.model = model;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(33, 33));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(1,2,1,2);
        }
        else
        {
            imageView = (ImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    public Integer[] mThumbIds = {
            R.drawable.blank, R.drawable.blankhit,
            R.drawable.enemyhit, R.drawable.own,
            R.drawable.blank, R.drawable.blankhit,
            R.drawable.enemyhit, R.drawable.own,
            R.drawable.blank, R.drawable.blankhit,
            R.drawable.enemyhit, R.drawable.own,
            R.drawable.blank, R.drawable.blankhit,
            R.drawable.enemyhit, R.drawable.own,
    };
}
