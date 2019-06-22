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
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;

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

        Button instructions = (Button) findViewById(R.id.instructions_button);
        instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog1();
            }
        });


    }
    private void showStartDialog(){
        final EditText editText = new EditText(HomeActivity.this);
        editText.setSingleLine(true); //kun en linje for navn
        editText.setRawInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(11); //maksimal længde på navn er 15 characters
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
                        } else if (inputName == null || inputName.trim().equals("") ||  inputName.substring(0,1).equals("")) {
                            Toast.makeText(getApplicationContext(), "Dit navn må ikke være tomt eller starte med et mellemrum", Toast.LENGTH_SHORT).show();
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
        /*
        Intent intent = new Intent(HomeActivity.this,NewGameActivity.class);
        intent.putExtra("inputName",inputName);
        startActivity(intent);
        */
        Intent prepGameIntent = new Intent(getApplicationContext(), PrepGameActivity.class);
        prepGameIntent.putExtra("isPlayerOne",true);
        startActivity(prepGameIntent);
    }

    private void joinSpil(){
        Intent intent = new Intent(HomeActivity.this, JoinGameActivity.class);
        intent.putExtra("inputName",inputName);
        startActivity(intent);
    }
    private void skiftNavn(){
        showStartDialog();
    }

    private void showDialog1() {
        TextView textView1 = new TextView(HomeActivity.this);
        textView1.setX(15);
        textView1.setText('\n' + "Velkommen til SlagShots!" + '\n' + "Den modificerede version af 'Sænke slagskibe', der samtidigt gør dig fuld!" + '\n' +'\n' +
                "Hvert skib repræsenterer et alkoholshot. Når et af dine skibe bliver sunket, skal" + '\n' +  "du tage et shot." + '\n' + "Skibenes længder er som følger:" +
                '\n' + '\n'+ "Carrier = 5 felter" + '\n' + "Battleship = 4 felter" + '\n' + "Cruiser = 3 felter" + '\n' + "Submarine = 3 felter" + '\n' + "Destroyer = 2 felter");
        textView1.setTextSize(18);
        new AlertDialog.Builder(this)
                .setTitle("SlagShots!")
                .setView(textView1)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("NÆSTE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog2();
                    }
                })
                .setNeutralButton("LUK",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }

    private void showDialog2() {
        TextView textView2 = new TextView(HomeActivity.this);
        textView2.setX(15);
        textView2.setText('\n' + "Dine skibe placeres ved at klikke på de" +'\n' + "to felter, som du ønsker skal være dit skibs endepunkter." + '\n' + '\n'
                +"Et eksempel kan ses med en Carrier, der" + '\n' + "fylder 5 felter, ved at trykke på NÆSTE");
        textView2.setTextSize(18);
        new AlertDialog.Builder(this)
                .setTitle("SlagShots!")
                .setView(textView2)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("NÆSTE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog3();

                    }
                })
                .setNegativeButton("FORRIGE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog1();
                    }
                })
                .setNeutralButton("LUK",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }

    private void showDialog3() {
        ImageView imageView1 = new ImageView(HomeActivity.this);
        imageView1.setImageResource(R.drawable.shipedges3);
        new AlertDialog.Builder(this)
                .setTitle("SlagShots!")
                .setView(imageView1)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("NÆSTE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog4();

                    }
                })
                .setNegativeButton("FORRIGE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog2();
                    }
                })
                .setNeutralButton("LUK",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }

    private void showDialog4() {
        TextView textView3 = new TextView(HomeActivity.this);
        textView3.setX(15);
        textView3.setText('\n' + "Når du har placeret dine skibe, er det tid til at få spillet igang." + '\n' + '\n' + "Den mindre bane i højre hjørne repræsenterer din bane, mens den store bane" +
                " i midten af skærmen, " + '\n' + "repræsenterer din modstanders bane." + '\n' + '\n' + "Når det er din tur, skal du vælge et felt" + '\n' + "på din modstanders bane, " +
                "hvor du tror" + '\n' + "at din modstander har placeret et skib" + '\n' + "Når du har fundet det ønskede felt, trykker du på AFFYR-knappen.");
        textView3.setTextSize(18);
        new AlertDialog.Builder(this)
                .setTitle("SlagShots!")
                .setView(textView3)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("NÆSTE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog5();

                    }
                })
                .setNegativeButton("TILBAGE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog3();
                    }
                })
                .setNeutralButton("LUK",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }
    public void showDialog5(){
        TextView textView4 = new TextView(HomeActivity.this);
        textView4.setX(15);
        textView4.setText('\n' + "Feltet " +
                "vil nu blive erstattet af en sort" + '\n' + "prik, med en enten hvid eller rød baggrund." + '\n' + '\n' +
                "Hvis dit skud rammer din modstanders skib, vil feltets baggrund være rød, mens en hvid baggrund repræsenterer en misser." + '\n' + '\n' +
                "En samlet oversigt over felter og symboler vises, ved at trykke" + '\n'+"på NÆSTE" );
        textView4.setTextSize(18);
        new AlertDialog.Builder(this)
                .setTitle("SlagShots!")
                .setView(textView4)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("NÆSTE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog6();

                    }
                })
                .setNegativeButton("TILBAGE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog4();
                    }
                })
                .setNeutralButton("LUK",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }

    public void showDialog6(){
        ImageView imageView2 = new ImageView(HomeActivity.this);
        imageView2.setImageResource(R.drawable.diagram);
        new AlertDialog.Builder(this)
                .setTitle("SlagShots!")
                .setView(imageView2)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("NÆSTE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog7();

                    }
                })
                .setNegativeButton("TILBAGE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog5();
                    }
                })
                .setNeutralButton("LUK",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create().show();
    }

    public void showDialog7(){
        TextView textView5 = new TextView(HomeActivity.this);
        textView5.setX(15);
        textView5.setText('\n'+ "Det var vist rigeligt af instruktioner!" + '\n' + "Nu er det tid til at spille! Tryk på LUK, for at komme i gang med at spille!");
        textView5.setTextSize(18);
        new AlertDialog.Builder(this)
                .setTitle("SlagShots!")
                .setView(textView5)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("LUK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("TILBAGE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog6();
                    }
                })
                .create().show();
    }

}

