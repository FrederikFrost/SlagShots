package com.example.bruger.slagshots;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

        Button skiftNavnButton = (Button) findViewById(R.id.skift_navn_button);
        skiftNavnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skiftNavn();
            }
        });


    }
    private void showStartDialog(){
        final EditText editText = new EditText(HomeActivity.this);
        editText.setSingleLine(true); //kun en linje for navn
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(14); //maksimal længde på navn er 15 characters
        editText.setFilters(filters);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("SlagShots!")
                .setMessage('\n' + "Indtast dit navn")
                .setView(editText)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Bekræft", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        inputName = editText.getText().toString();
                        if (inputName != null && !inputName.trim().equals((""))) {
                            dialog.dismiss();
                            TextView nameView = (TextView) findViewById(R.id.nameView);
                            nameView.setText(getResources().getString(R.string.title_activity_username) + "   " + inputName);
                        } else if (inputName == null || inputName.trim().equals("")) {
                            Toast.makeText(getApplicationContext(), "Dit navn må ikke være tomt", Toast.LENGTH_SHORT).show();
                            showStartDialog();

                        }
                    }
                })
                .create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

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
        Intent intent = new Intent(HomeActivity.this, JoinGameActivity.class);
        startActivity(intent);
        finish();
    }
    private void skiftNavn(){
        showStartDialog();
    }
}
