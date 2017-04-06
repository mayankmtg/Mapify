package com.example.dell.healerpre;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.BaseColumns;
import android.renderscript.ScriptGroup;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    DatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        myDB=new DatabaseHelper(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onSearch(View view){
        EditText healerName=(EditText)findViewById(R.id.editText);
        String name=healerName.getText().toString();
        Cursor markers=myDB.getData();
        if(markers.getCount()==0){
            //error
        }
        while(markers.moveToNext()){
            String newHealer=markers.getString(1);
            if(newHealer.equals(name)){
                double lat=Double.parseDouble(markers.getString(2));
                double lng=Double.parseDouble(markers.getString(3));
                LatLng point=new LatLng(lat,lng);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                Toast.makeText(getApplicationContext(),"Healer Found", Toast.LENGTH_LONG).show();
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Cursor markers=myDB.getData();
        if(markers.getCount()==0){
            //error
        }
        while(markers.moveToNext()){
            String newHealer=markers.getString(1);
            double lat=Double.parseDouble(markers.getString(2));
            double lng=Double.parseDouble(markers.getString(3));
            double newRat=Double.parseDouble(markers.getString(4));
            LatLng point=new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions()
                    .position(point)
                    .title(newHealer)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        LatLng point=new LatLng(latitude, longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    @Override
    public void onMapClick(LatLng point) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
    }

    @Override
    public void onMapLongClick(final LatLng point) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Healer");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Set up the input
        final EditText input = new EditText(this);
        final EditText rating = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        input.setHint("Healer Name");
        rating.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        rating.setHint("Healer Rating");
        layout.addView(input);
        layout.addView(rating);
        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newHealer=input.getText().toString();
                double newRat=Double.parseDouble(rating.getText().toString());
                boolean temp=myDB.insertData(newHealer,point.latitude,point.longitude,newRat);
                if(temp){
                    mMap.addMarker(new MarkerOptions()
                            .position(point)
                            .title(newHealer)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    Toast.makeText(getApplicationContext(),"New Healer Saved", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();


    }
}
