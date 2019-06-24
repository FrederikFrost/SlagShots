package com.example.bruger.slagshots;

import android.graphics.Color;
import android.util.Log;
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
    private boolean fogOfwar;
    private boolean isPlayerOne;
    private int selectedPosition = -1;


    public GridViewAdapter(Context c, GameModel model, boolean isPlayerOne, boolean fogOfWar) {
        mContext = c;
        this.model = model;
        this.isPlayerOne = isPlayerOne;
        this.fogOfwar = fogOfWar;
    }

    @Override
    public int getCount() {
        return model.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquareImageView imageView;

        if (convertView == null) {
            imageView = new SquareImageView(mContext);
            //imageView.setPadding(0, 2, 0, 2);
        } else {
            imageView = (SquareImageView) convertView;
        }

        if (fogOfwar){
            imageView.setImageResource(mThumbIdsFog[model.getView(position, isPlayerOne)]);
        } else {
            imageView.setImageResource(mThumbIds[model.getView(position, isPlayerOne)]);
        }

        if (position == selectedPosition) {
            Log.i("SKUD","Jeg sætter markerings farven");
            imageView.setColorFilter(Color.RED);
        } else {
            imageView.setColorFilter(Color.TRANSPARENT);
        }

        return imageView;
    }

    public Integer[] mThumbIds = {
            R.drawable.blank, R.drawable.blankhit,
            R.drawable.enemyhit, R.drawable.own,
    };

    public Integer[] mThumbIdsFog = {
            R.drawable.blank, R.drawable.blankhit,
            R.drawable.enemyhit, R.drawable.blank,
    };

}
