package com.example.yousuf.labworkjsonfetch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "http://anontech.info/courses/cse491/employees.json";

    static ArrayList<HashMap<String, String>> contactList;

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);
        if (database == null) {
            Log.e("db","db: "+database);
            database = openOrCreateDatabase("lushandb", Context.MODE_PRIVATE, null);
            Log.e("db","db: "+database);
            database.execSQL("CREATE TABLE IF NOT EXISTS Employee(Name VARCHAR,latitude VARCHAR," +
                    "longitude VARCHAR);");
            new GetContacts().execute();
        }//else {
            Cursor c = database.rawQuery("select DISTINCT * from Employee", null);
            int current_offline_post = c.getCount();
            Log.e("db",current_offline_post+" count");
            if(current_offline_post>0){
//                Load Offline first
//                Cursor cursor = database.rawQuery("Select * from Employee", null);
                String name = null, latitude = null, logitude = null;
                c.moveToFirst();

                contactList.clear();
                while (c.moveToNext()) {
                    HashMap<String, String> contact = new HashMap<>();
                    name = c.getString(0);
                    latitude = c.getString(1);
                    logitude = c.getString(2);
                    //Do something Here with values
                    contact.put("name", name);
                    contact.put("latitude", latitude);
                    contact.put("longitude", logitude);
                    Log.d("buggy",name+latitude+logitude);
                    // adding contact to contact list
                    contactList.add(contact);
                }
                c.close();
                for (int i=0;i<contactList.size();i++){
                    Log.e("problem",contactList.get(i).get("name"));
                    Log.e("problem",contactList.get(i).get("latitude"));
                    Log.e("problem",contactList.get(i).get("longitude"));
                }
                ListAdapter adapter = new SimpleAdapter(
                        MainActivity.this, contactList,
                        R.layout.list_item, new String[]{"name", "longitude",
                        "latitude"}, new int[]{R.id.name,
                        R.id.longitude, R.id.latitude});
                lv.setAdapter(adapter);

//                Check Silently If There Is New Post Online
            }
//        }


    }

    public void showMap(View view) {
        startActivity(new Intent(MainActivity.this,EmployeeMapsActivity.class));
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
//                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = new JSONArray(jsonStr);
                    Log.e("length",contacts.length()+"");
                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        // String id = c.getString("id");
                        String name = c.getString("name");
                        // String email = c.getString("email");
                        //  String address = c.getString("address");
                        //  String gender = c.getString("gender");

                        // Phone node is JSON Object
                        JSONObject location;
                        String latitude;
                        String longitude;
                        try {
                            location = c.getJSONObject("location");
                            latitude = location.getString("latitude");
                            longitude = location.getString("longitude");
                        }catch (JSONException e){
                            latitude = null;
                            longitude = null;
                        }
                        database.execSQL("INSERT INTO Employee (Name, latitude, longitude) VALUES ('" + name + "', '" + latitude + "', '" + longitude + "');");
                        // tmp hash map for single contact
//                        HashMap<String, String> contact = new HashMap<>();
                        // adding each child node to HashMap key => value
                        // contact.put("id", id);
//                        contact.put("name", name);
//                        contact.put("latitude", latitude);
//                        contact.put("longitude", longitude);

                        // adding contact to contact list
//                        contactList.add(contact);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
//            ListAdapter adapter = new SimpleAdapter(
//                    MainActivity.this, contactList,
//                    R.layout.list_item, new String[]{"name", "longitude",
//                    "latitude"}, new int[]{R.id.name,
//                    R.id.longitude, R.id.latitude});
//
//            lv.setAdapter(adapter);

        }

    }

}
