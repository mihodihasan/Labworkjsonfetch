package com.example.yousuf.labworkjsonfetch;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;

public class EmployeeMapsActivity extends FragmentActivity implements OnMapReadyCallback {


    TestJobService testService;
    private static int kJobId = 0;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i=0;i<MainActivity.contactList.size();i++){
            HashMap<String, String> contact=MainActivity.contactList.get(i);
            final String name=contact.get("name");
            String latitude = contact.get("latitude");
            String longitude=contact.get("longitude");

            if(latitude ==null&&longitude==null){
                LayoutInflater li = LayoutInflater.from(EmployeeMapsActivity.this);
                View promptsView = li.inflate(R.layout.input_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EmployeeMapsActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInputLat = (EditText) promptsView.findViewById(R.id.latitude);

                final EditText userInputLon=(EditText)promptsView.findViewById(R.id.longitude);
                final String[] inputs = new String[2];
                alertDialogBuilder
                        .setTitle("No Latitude and Logitude Found for "+name+"Put Input If U Want!")
                        .setCancelable(true)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        inputs[0] =userInputLat.getText().toString();
                                        inputs[1]=userInputLon.getText().toString();
                                        LatLng latLng=new LatLng(Double.parseDouble(inputs[0]),Double.parseDouble(inputs[1]));

                                        mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                                        builder.include(latLng);
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();




            }
            else {
                LatLng latLng=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

                mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                builder.include(latLng);
            }
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 1);
        mMap.moveCamera(cu);
        mMap.animateCamera(cu);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EmployeeMapsActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                    }
                }, 5000);
                return true;
            }
        });
    }
}
