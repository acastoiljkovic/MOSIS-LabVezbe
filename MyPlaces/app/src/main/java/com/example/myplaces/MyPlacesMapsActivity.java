package com.example.myplaces;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

public class MyPlacesMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static int NEW_PLACE=1;
    static final int PERMISSION_ACCESS_FINE_LOCATION=1;

    public static final int SHOW_MAP=0;
    public static final int CENTER_PLACE_ON_MAP=1;
    public static final int SELECT_COORDINATES=1;

    private int state=0;
    private boolean selCoorsEnabled=false;
    private LatLng placeLoc;

    private HashMap<Marker,Integer> markerPlaceIdMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyPlacesData.getInstance().setEventListener(new ListUpdatedEventListener() {
            @Override
            public void onListUpdated() {
                addMyPlaceMarkers();
            }
        });

        try{
            Intent mapIntent=getIntent();
            Bundle mapBundle=mapIntent.getExtras();
            if(mapBundle!=null){
                state=mapBundle.getInt("state");
                if(state==CENTER_PLACE_ON_MAP){
                    String placeLat=mapBundle.getString("lat");
                    String placeLon=mapBundle.getString("lon");
                    placeLoc=new LatLng(Double.parseDouble(placeLat),Double.parseDouble(placeLon));
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_my_places_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(state!=SELECT_COORDINATES){
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MyPlacesMapsActivity.this,EditMyPlaceActvity.class);
                    startActivityForResult(i,NEW_PLACE);
                }
            });
        }
        else
        {
            ViewGroup layout=(ViewGroup) fab.getParent();
            if(layout!=null){
                layout.removeView(fab);
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(state==SELECT_COORDINATES && !selCoorsEnabled){
            menu.add(0,1,1,"Select Coordinates");
            menu.add(0,2,2,"Cancel");
            return super.onCreateOptionsMenu(menu);
        }
        else
        {
            getMenuInflater().inflate(R.menu.menu_my_places_maps,menu);
            return true;
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(state==SELECT_COORDINATES && !selCoorsEnabled){
            if(id==1){
                selCoorsEnabled=true;
                Toast.makeText(this,"Select coordinates",Toast.LENGTH_SHORT).show();
            }
            else if(id==2){
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }
        else
        {
            if(id==R.id.new_place_item){
                startActivity(new Intent(this,EditMyPlaceActvity.class));
            }
            else if(id==R.id.about_item){
                startActivity(new Intent(this,About.class));
            }
            else
            {
                finish();
            }

        }


        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_ACCESS_FINE_LOCATION);
        }
        else
        {
            if(state==SHOW_MAP){
                mMap.setMyLocationEnabled(true);
            }
            else if(state==CENTER_PLACE_ON_MAP){
                this.setOnMapClickListener();
            }
            else
            {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLoc,15));
            }

            this.addMyPlaceMarkers();


        }


    }

    private void setOnMapClickListener(){
        if(mMap!=null){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    if(state==SELECT_COORDINATES && selCoorsEnabled){
                        String lon=Double.toString(latLng.longitude);
                        String lat=Double.toString(latLng.latitude);

                        Intent locationIntent=new Intent();
                        locationIntent.putExtra("lon",lon);
                        locationIntent.putExtra("lat",lat);
                        setResult(Activity.RESULT_OK,locationIntent);
                        finish();
                    }

                }
            });
        }
    }

    private void addMyPlaceMarkers(){
        ArrayList<MyPlace> places=MyPlacesData.getInstance().getMyPlaces();
        markerPlaceIdMap=new HashMap<Marker, Integer>((int)((double)places.size()*1.2));
        for(int i=0;i<places.size();i++){
            MyPlace place=places.get(i);
            String lat=place.getLatitude();
            String lon=place.getLongitude();
            LatLng loc=new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(loc);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon));
            markerOptions.title(place.getName());
            Marker marker=mMap.addMarker(markerOptions);
            markerPlaceIdMap.put(marker,i);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent=new Intent(MyPlacesMapsActivity.this,ViewMyPlaceActivity.class);
                int i=markerPlaceIdMap.get(marker);
                intent.putExtra("position",i);
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case PERMISSION_ACCESS_FINE_LOCATION: {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(state==SHOW_MAP){
                        mMap.setMyLocationEnabled(true);
                    }
                    else if(state==CENTER_PLACE_ON_MAP){
                        this.setOnMapClickListener();
                    }
                    else
                    {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(placeLoc,15));
                    }

                    this.addMyPlaceMarkers();
                }
                return;
            }
        }
    }
}
