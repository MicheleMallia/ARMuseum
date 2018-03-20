package com.michele.testQuadriAR;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Main2Activity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //get intent from previous activity
        Intent i = getIntent();

        //get data from intent
        String data = i.getExtras().getString("CURL_RES");
        String[] separated = data.split(",");
        
        //separate data
        separated[1] = separated[1].trim();
        separated[0] = separated[0].trim();
        separated[2] = separated[2].trim();
        
        //set API for Google Images
        String myUrl = "https://www.google.it/search?q="+separated[1]+"+painting&tbs=isz:m&tbm=isch&source=lnt&sa=X&ved=0ahUKEwjA0IzA0N_ZAhWJ1RQKHWbPDC0QpwUIHg&biw=1105&bih=659&dpr=1";
        String result ="";
       

        //make get request
        HttpGetRequest getRequest = new HttpGetRequest();
        try {
            result = getRequest.execute(myUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //split html body to take ou tags with links
        String[] array = result.split("\"ou\":\"");
        List<String> arrayDue = new ArrayList<String>();
        Integer cont = 0;
        for(String s:array){
            if(s.contains(".jpg")) {
                if(cont==20){
                    break;
                }
                String[] temp = s.split("\"");
                arrayDue.add(temp[0]);
                System.out.println(temp[0]);
                cont++;
            }
        }

        //declare listview
        ListView lv = (ListView)findViewById(R.id.listview);
        List<String> arrayLink = new ArrayList<String>();
        Integer contDue = 0;
        Integer contTre = 0;

        //check if link is reachable
        for(String s:arrayDue){
            if(contTre == Integer.parseInt(separated[2])){
                break;
            }
            MyTask task = new MyTask();
            task.execute(arrayDue.get(contDue));
            try {
                if(task.get()){
                    arrayLink.add(arrayDue.get(contDue));
                    contDue++;
                    contTre++;
                }
                else{
                    contDue++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        //populate listview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,arrayLink);
        lv.setAdapter(adapter);

        //store links into array
        final String link[] = arrayLink.toArray(new String[0]);
        

        //declare button and set listener
        Button btnClick = (Button)findViewById(R.id.startUnity);
        btnClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            	//make intent with data
                Intent intent = new Intent(getBaseContext(), UnityPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("DATA", link);

                //send intent go UnityPlayerActivity
                startActivity(intent);

            }
        });

    }


    //class to do GET request
    public class HttpGetRequest extends AsyncTask<String, Void, String> {

    	//set request attribute
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;


        //run background task
        @Override
        protected String doInBackground(String... params){

        	//store data in this variable
            String stringUrl = params[0];
            String result = "";
            String inputLine;
            try {

                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.4.4; HUAWEI H891L Build/HuaweiH891L) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36");
                connection.connect();
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            } catch(IOException e){
                e.printStackTrace();
                result = null;
            }
            return result;

        }

        //store data after finish the request
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }


    //class to check if image is online
    private class MyTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {

        }


        //background async task
        @Override
        protected Boolean doInBackground(String... params) {


        	//check connection
            try {
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection con =  (HttpURLConnection) new URL(params[0]).openConnection();
                con.setRequestMethod("HEAD");
                System.out.println(con.getResponseCode());
                return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
            }
            catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
        }
    }

}