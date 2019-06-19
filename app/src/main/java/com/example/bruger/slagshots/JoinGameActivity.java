package com.example.bruger.slagshots;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

public class JoinGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EditText editText = (EditText) findViewById(R.id.editText) ;
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(4); //Filter to 10 characters
        editText.setFilters(filters);

    }
}


