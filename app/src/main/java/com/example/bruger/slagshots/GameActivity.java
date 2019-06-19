package com.example.bruger.slagshots;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;


public class GameActivity extends AppCompatActivity {

    private GameModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new GameModel();
        setContentView(R.layout.activity_game);

        GridView gridview1 = (GridView) findViewById(R.id.gridView1);
        GridViewAdapter adapter = new GridViewAdapter(this, model);
        gridview1.setAdapter(adapter);

        GridView gridview2 = (GridView) findViewById(R.id.gridView2);
        gridview2.setAdapter(adapter);
    }

}
