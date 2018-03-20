package com.michele.testQuadriAR;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class Main3Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        //get intent from previous activity 
        Intent i = getIntent();

        //get data from intent
        String data = i.getExtras().getString("CURL_RES");
        String[] separated = data.split(",");
        
        //separate data
        separated[1] = separated[1].trim();
        separated[0] = separated[0].trim();
        separated[2] = separated[2].trim();

        //set API for Wikimedia
        String myUrl = "https://commons.wikimedia.org/w/api.php?format=json&action=query&list=categorymembers&cmtitle="+separated[1]+"&cmlimit="+Integer.parseInt(separated[2])+"";
        String result ="";

        //make the GET request to retrieve fileslist
        Main3Activity.HttpGetRequest getRequest = new Main3Activity.HttpGetRequest();
        try {
            result = getRequest.execute(myUrl).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        //store data into array
        ArrayList<String> listFiles = new ArrayList<String>();

        //access to JSON data
        try {
            JSONObject jsonObj = new JSONObject(result);
            JSONArray jsonArray = jsonObj.getJSONObject("query").getJSONArray("categorymembers");
            if (jsonArray != null) {
                for (int j=0;j<jsonArray.length();j++){
                    listFiles.add(jsonArray.getJSONObject(j).getString("title"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //store data in this array
        ArrayList<String> listFiles2 = new ArrayList<String>();
        for(int k = 0; k<listFiles.size();k++){

        	//encode string in this variable
            String resultFile ="";

            //use URL encoding
            String fileEncoded = URLEncoder.encode(listFiles.get(k));
            String urlFiles = "https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&format=json&&iiprop=url%7Cextmetadata&iilimit=10&titles="+fileEncoded+"&iiurlwidth=800";
            

            //get JSON file
            Main3Activity.HttpGetRequest getJsonFile = new Main3Activity.HttpGetRequest();
            try {
                resultFile = getJsonFile.execute(urlFiles).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            //parse JSON File
            try {
                JSONObject jsonObj2 = new JSONObject(resultFile);
                JSONObject jsonPages = jsonObj2.getJSONObject("query").getJSONObject("pages");
                String key = jsonPages.keys().next();
                JSONArray jsonArray2 = jsonObj2.getJSONObject("query").getJSONObject("pages").getJSONObject(key).getJSONArray("imageinfo");
                if (jsonArray2 != null) {
                    for (int j=0;j<jsonArray2.length();j++){
                        listFiles2.add(jsonArray2.getJSONObject(j).getString("thumburl"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //create listview
        ListView lv = (ListView) findViewById(R.id.listView2);
        
        //populate listview
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,listFiles2);
        lv.setAdapter(adapter);


        //memorize list of file in this array
        final String files[] = listFiles2.toArray(new String[0]);
        
        //declare button and set listener
        Button btnClick = (Button)findViewById(R.id.startUnity);
        btnClick.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            	//set up intent
                Intent intent = new Intent(getBaseContext(), UnityPlayerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("DATA", files);

                //start activity and send data
                startActivity(intent);

            }
        });

    }


    //class to do GET request
    public class HttpGetRequest extends AsyncTask<String, Void, String> {

    	//set request parameters
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;


        //background async task
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

        //memorize result after finish async process
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
        }
    }
}
