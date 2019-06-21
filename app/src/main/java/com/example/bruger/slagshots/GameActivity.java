package com.example.bruger.slagshots;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;


public class GameActivity extends AppCompatActivity {

    private GameModel model;
    private boolean positionSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new GameModel();
        setContentView(R.layout.activity_game);

        GridView gridView1 = (GridView) findViewById(R.id.gridView1);
        gridView1.setAdapter(new GridViewAdapter(this, model, true));

        final GridView gridView2 = (GridView) findViewById(R.id.gridView2);
        final GridViewAdapter adapter = new GridViewAdapter(this, model, false);
        gridView2.setAdapter(adapter);

        gridView2.setOnItemClickListener(getListener(adapter));

        Button fireShot = (Button) findViewById(R.id.fireShot);
        fireShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!positionSelected) {
                    Toast.makeText(getApplicationContext(),"Intet felt valgt at skyde på", Toast.LENGTH_LONG).show();

                } else {
                    Log.i("SKUD", "Jeg affryrer skud.");
                    model.getPos(adapter.getSelectedPosition(), false).hit();
                    adapter.setSelectedPosition(-1);
                    positionSelected = false;

                    // TODO: Make turn based
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    @NonNull
    private OnItemClickListener getListener(final GridViewAdapter adapter) {
        return new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Log.i("SKUD", "Jeg sætter markerings positionen");
                positionSelected = true;
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();
            }
        };
    }


}
