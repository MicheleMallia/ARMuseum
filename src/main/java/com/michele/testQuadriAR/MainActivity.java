package com.michele.testQuadriAR;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;



public class MainActivity extends Activity {


	//check if is a valid artist name
    public final static boolean isValidName(String target) {
        return Pattern.compile("([A-Za-zàèìòù ]+)").matcher(target).matches();

    }

    //check if device is connected with data or Wi-Fi
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {

            return false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //init component of activity
        final Spinner sRepo = (Spinner)findViewById(R.id.spinRepo);
        final EditText artist = (EditText)findViewById(R.id.artist);
        final Spinner sNum = (Spinner)findViewById(R.id.spiNum);
        final Spinner sCat = (Spinner)findViewById(R.id.spinCat);


        //init variable
        final String[] selRepo = new String[1];
        final String[] selArtist = new String[1];
        final String[] selNum = new String[1];
        final String[] selCat = new String[1];
        final String[] selString = new String[1];
        final Integer[] mode = new Integer[1];


        //set visibility of categories spinner menu
        sCat.setVisibility(View.INVISIBLE);
        mode[0] = 1;
        sRepo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            //if select any element
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                Integer selectedRepo = parent.getSelectedItemPosition();
                mode[0] = selectedRepo;

                //if Wikimedia
                if(selectedRepo == 1){
                    artist.setVisibility(View.GONE);
                    sCat.setVisibility(View.VISIBLE);
                }

                //if Google Images
                else if(selectedRepo == 0){
                    artist.setVisibility(View.VISIBLE);
                    sCat.setVisibility(View.GONE);
                }

            }


            //if nothing selected
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //set listener on button
        Button btnClick = (Button)findViewById(R.id.startUnity);
        btnClick.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (isOnline()) {

                	//if Wikimedia
                    if (mode[0] == 1) {
                        try {

                        	//concatenate string
                            selCat[0] = URLEncoder.encode("Category:Google Art Project works in "+sCat.getSelectedItem().toString(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        //get number of requested images
                        selNum[0] = sNum.getSelectedItem().toString();

                        //concatenate all parameters
                        selString[0] = (String) mode[0].toString() + ", " + (String) selCat[0] + ", " + (String) selNum[0];

                        //send intent to Wikimedia Activity
                        Intent intent = new Intent(getBaseContext(), Main3Activity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("CURL_RES", selString[0]);

                        //start activity
                        startActivity(intent);

                    } 

                    //if Google Images
                    else if (mode[0] == 0) {

                    	//check name
                        if (artist.getText().toString().equals("")) {
                            artist.setHint("Insert a valid artist name!");
                        }

                        //check name
                        else if(!isValidName(artist.getText().toString())){
                            artist.setText("");
                            artist.setHint("Insert a valid artist name!");
                        }

                        //if name is ok
                        else {
                            selArtist[0] = artist.getText().toString();
                            try {
                                selArtist[0] = URLEncoder.encode(selArtist[0], "utf-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            //concatenate strings
                            selNum[0] = sNum.getSelectedItem().toString();
                            selString[0] = (String) mode[0].toString() + ", " + (String) selArtist[0] + ", " + (String) selNum[0];
                            
                            //set intent
                            Intent intent = new Intent(getBaseContext(), Main2Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("CURL_RES", selString[0]);

                            //send intent to Google Activity
                            startActivity(intent);
                        }

                    }

                }
                else{
                    Toast.makeText(getBaseContext(), "Make sure your internet connection is enabled",Toast.LENGTH_SHORT ).show();
                }
            }
        });



    }

}

