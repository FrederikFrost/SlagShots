package com.example.bruger.slagshots;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PrepGameAdapter extends BaseAdapter {
    private Context mContext;
    private GameModel model;
    private int selectedPosition = -1;
    private boolean isPlayerOne;

    public PrepGameAdapter(Context c, GameModel model, boolean isPlayerOne) {
        mContext = c;
        this.model = model;
        this.isPlayerOne = isPlayerOne;
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
        } else {
            imageView = (SquareImageView) convertView;
        }
        imageView.setImageResource(mThumbIds[model.getView(position,isPlayerOne)]);
        if (position == selectedPosition) {
            Log.i("SKUD","Jeg sætter markerings farven");
            imageView.setColorFilter(Color.rgb(255,0,255));
        } else {
            imageView.setColorFilter(Color.TRANSPARENT);
        }

        return imageView;
    }

    public Integer[] mThumbIds = {
            R.drawable.blank, R.drawable.blankhit,
            R.drawable.enemyhit, R.drawable.own,
    };
}
