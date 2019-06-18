package com.example.bruger.slagshots;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class HomeActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String inputName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("prefs",MODE_PRIVATE);
        showStartDialog();

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
        Button opretSpilButton = (Button) findViewById(R.id.opret_spil_button);
        opretSpilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opretSpil();
            }
        });

        Button joinSpilButton = (Button) findViewById(R.id.join_spil_button);
        joinSpilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinSpil();
            }
        });


    }
    private void showStartDialog(){
        final EditText editText = new EditText(HomeActivity.this);
        new AlertDialog.Builder(this)
                .setTitle("SlagShots!")
                .setMessage('\n' + "Indtast dit navn")
                .setView(editText)
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("Bekræft", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputName = editText.getText().toString();
                        dialog.dismiss();
                    }
                })
                .create().show();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void opretSpil(){
      Intent intent = new Intent(HomeActivity.this,NewGameActivity.class);
        intent.putExtra("inputName",inputName);
      startActivity(intent);
    }

    private void joinSpil(){
        finish();
    }
}
