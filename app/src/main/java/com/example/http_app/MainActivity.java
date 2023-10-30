package com.example.http_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarException;

public class MainActivity extends AppCompatActivity {

    private EditText editTextField1, editTextField2;
    private TextView textViewResult;
    private Button buttonSet, buttonPost, buttonStatus;

    private String webAddress="https://api.thingspeak.com/";
    private String getApiKey="update?api_key=XVUGOCE6UCMKVH6G";
    private String field1="&field1=";
    private String field2="&field2=";
    private String field1Data, field2Data;
    private String postApiKey="api_key=XVUGOCE6UCMKVH6G";
    private String status="channels/2323329/status.json?api_key=1N3FX0Y68A4ECD2G";
    private List<Map<String, String>> jsonListData;
    private ListView listViewData;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextField1 = (EditText) findViewById(R.id.editText_field1);
        editTextField2 = (EditText) findViewById(R.id.editText_field2);
        textViewResult = (TextView) findViewById(R.id.textView_result);
        textViewResult.setText("");

        buttonSet = (Button) findViewById(R.id.button_set);
        buttonPost = (Button) findViewById(R.id.button_post);
        buttonStatus = (Button) findViewById(R.id.button_status);

        buttonSet.setOnClickListener(new MyButton());
        buttonPost.setOnClickListener(new MyButton());
        buttonStatus.setOnClickListener(new MyButton());

        listViewData = (ListView) findViewById(R.id.listView_data);
        jsonListData= new ArrayList<>();
        adapter = new SimpleAdapter(MainActivity.this,jsonListData,R.layout.item_layout,
                new String[]{"created_at","entry_id"},new int[]{R.id.textView_date,R.id.textView_id});
        listViewData.setAdapter(adapter);

    }

    private class MyButton implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_set:
                    if(editTextField1.length()==0){
                        Toast.makeText(MainActivity.this, "Please input field 1", Toast.LENGTH_SHORT).show();
                        field1Data="0";
                    }else{
                        field1Data = editTextField1.getText().toString();
                    }

                    if(editTextField2.length()==0){
                        Toast.makeText(MainActivity.this, "Please input field 2", Toast.LENGTH_SHORT).show();
                        field2Data="0";
                    }else{
                        field2Data = editTextField2.getText().toString();
                    }

                    new HttpSetData().start();

                    break;

                case R.id.button_post:
                    if(editTextField1.length()==0){
                        Toast.makeText(MainActivity.this, "Please input field 1", Toast.LENGTH_SHORT).show();
                        field1Data="0";
                    }else{
                        field1Data = editTextField1.getText().toString();
                    }

                    if(editTextField2.length()==0){
                        Toast.makeText(MainActivity.this, "Please input field 2", Toast.LENGTH_SHORT).show();
                        field2Data="0";
                    }else{
                        field2Data = editTextField2.getText().toString();
                    }

                    new HttpPostData().start();
                    break;

                case R.id.button_status:
                    new HttpStatus().start();
                    break;
            }
        }
    }

    private class HttpSetData extends  Thread{
        private StringBuilder thingSpeakURL;
        private URL url;
        private HttpURLConnection conn;
        private int code;
        private InputStream inputStream;
        private String data;

        @Override
        public void run() {
            super.run();
            thingSpeakURL = new StringBuilder();
            thingSpeakURL.append(webAddress);
            thingSpeakURL.append(getApiKey+field1+field1Data+field2+field2Data);
            Log.d("main","thingSpeak url="+thingSpeakURL);

            try {
                url = new URL(thingSpeakURL.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                code = conn.getResponseCode();
                Log.d("main","code="+code);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (code==HttpURLConnection.HTTP_OK){

                try {
                    inputStream = conn.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                InputStreamReader reader = new InputStreamReader(inputStream);
                char[] buffer = new char[10];
                try {
                    int number = reader.read(buffer);
                    Log.d("main","number="+number);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                data = String.valueOf(buffer);
                Log.d("main","buffer[0]="+buffer[0]);
                Log.d("main","buffer[1]="+buffer[1]);
                Log.d("main","data="+data);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewResult.setText("number :"+data);
                    }
                });

            }//end of Http_OK

        }//end run()
    }//end thread

    private class HttpPostData extends  Thread{
        private StringBuilder thingSpeakURL;
        private URL url;
        private HttpURLConnection conn;
        private OutputStream outputStream;
        private OutputStreamWriter writer;
        private String param;
        private int code;
        private InputStream inputStream;
        private String data;

        @Override
        public void run() {
            super.run();
            thingSpeakURL = new StringBuilder();
            thingSpeakURL.append(webAddress);
            thingSpeakURL.append("update");
            Log.d("main","thing url="+thingSpeakURL);

            try {
                url = new URL(thingSpeakURL.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");

            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                outputStream = conn.getOutputStream();
                writer = new OutputStreamWriter(outputStream);
                param = postApiKey+field1+field1Data+field2+field2Data;
                Log.d("main","param="+param);
                writer.write(param);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                writer.flush();
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                code = conn.getResponseCode();
                Log.d("main","code="+code);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (code==HttpURLConnection.HTTP_OK){

                try {
                    inputStream = conn.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                InputStreamReader reader = new InputStreamReader(inputStream);
                char[] buffer = new char[10];
                try {
                    int number = reader.read(buffer);
                    Log.d("main","number="+number);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                data = String.valueOf(buffer);
                Log.d("main","buffer[0]="+buffer[0]);
                Log.d("main","buffer[1]="+buffer[1]);
                Log.d("main","data="+data);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        textViewResult.setText("number :"+data);
                    }
                });

            }//end of Http_OK

        }//run()
    }//end thread

    private class HttpStatus extends  Thread{
        private StringBuilder thingSpeakURL;
        private URL url;
        private HttpURLConnection conn;
        private int code;
        private InputStream inputStream;
        private String data;

        @Override
        public void run() {
            super.run();
            thingSpeakURL = new StringBuilder();
            thingSpeakURL.append(webAddress);
            thingSpeakURL.append(status);
            Log.d("main","thingSpeak url="+thingSpeakURL);

            try {
                url = new URL(thingSpeakURL.toString());
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                code = conn.getResponseCode();
                Log.d("main","code="+code);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (code==HttpURLConnection.HTTP_OK){

                try {
                    inputStream = conn.getInputStream();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                InputStreamReader reader = new InputStreamReader(inputStream);
                char[] buffer = new char[2048];
                try {
                    int number = reader.read(buffer);
                    Log.d("main","number="+number);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                data = String.valueOf(buffer);
                Log.d("main","data="+data);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                runOnUiThread(new Runnable() {
                    private String jsonData;
                    private JSONObject jsonObj;

                    @Override
                    public void run() {
//                        textViewResult.setText("status :\n"+data);
                        textViewResult.setText("");
                        try {
                            jsonObj = new JSONObject(data);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        JSONArray jsonName = jsonObj.names();
                        Log.d("main","json name="+jsonName);
                        int no = jsonName.length();
                        Log.d("main","json length="+no);
                        StringBuffer jsonBuffer = new StringBuffer();
                        for(int i=0; i <no;i++ ){
                            String key = jsonName.optString(i);
                            Log.d("main","key="+key);
                            try {
                                jsonData = jsonObj.getString(key);
                                Log.d("main","data="+jsonData);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            if (key.equals("channel")){
                                Log.d("main","channel");
                                Map<String, String> jsonMap = jsonTransfer(jsonData);
                            }else if(key.equals("feeds")){
                                Log.d("main","feeds");
                                jsonArrayTransfer(jsonData,jsonListData);
                                Log.d("main","jsonListData="+jsonListData);
                                adapter.notifyDataSetChanged();
                            }

                            jsonBuffer.append("key :"+key+"\n");
                            jsonBuffer.append("data ="+jsonData+"\n");
                        }
                        textViewResult.append(jsonBuffer.toString());
                    }//end of run()

                    private Map<String,String> jsonTransfer(String jsonData){

                        try{
                            JSONObject myJsonObj = new JSONObject(jsonData);
                            JSONArray myJsonName = myJsonObj.names();
                            Map<String, String> myMapData = new HashMap<String, String>();
                            for (int i=0; i <myJsonName.length();i++){
                                String key = myJsonName.optString(i);
                                myMapData.put(key,myJsonObj.getString(key));
                            }
                            Log.d("main","mapData="+myMapData);
                            return  myMapData;
                        }catch (JSONException e){
                            throw  new RuntimeException(e);
                        }
                    }// end of jsonTransfer

                    private void jsonArrayTransfer(String jsonData,List<Map<String,String>> listData){
//                    private List<Map<String, String>> jsonArrayTransfer(String jsonData){

                        try {
                            JSONArray myJsonArray = new JSONArray(jsonData);
 //

                            for (int i = 0; i <myJsonArray.length(); i++){
                                String jasonValue = myJsonArray.get(i).toString();
                                Map<String, String>mapData = jsonTransfer(jasonValue);
                                listData.add(mapData);
                            }
                            Log.d("main","myListData="+listData);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }//end of jsonArrayTransfer()
                });//end of runOnUiThread()

            }//end of Http_OK

        }//end run()
    }
}