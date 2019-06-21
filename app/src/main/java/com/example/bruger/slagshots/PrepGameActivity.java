package com.example.bruger.slagshots;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

public class PrepGameActivity extends AppCompatActivity {

    private GameModel model;
    private boolean isPlayerOne;
    private boolean positionSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prep_game);
        model = new GameModel();
        isPlayerOne = getIntent().getExtras().getBoolean("isPlayerOne",false);

        final GridView gridView = (GridView) findViewById(R.id.gridView3);
        final PrepGameAdapter adapter = new PrepGameAdapter(this, model);
        gridView.setAdapter(adapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionSelected = true;
                adapter.setSelectedPosition(position);
                adapter.notifyDataSetChanged();
            }
        });





        Button ready = (Button) findViewById(R.id.readyButton);
        ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                intent.putExtra("isPlayerOne", isPlayerOne);
            }
        });

    }

}
