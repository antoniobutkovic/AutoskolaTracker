package com.example.toni.myapplication;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Toni on 27.9.2017..
 */

public class RideInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DatabaseReference databaseReference;
    private String rideNum;
    private String studentName;
    private ArrayList<String> lat = new ArrayList<String>();
    private ArrayList<String> lng = new ArrayList<String>();
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private CheckForInternetAndGPS checkForInternetAndGPS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_info);

        Bundle bundle = getIntent().getExtras();
        studentName = bundle.getString("studentName");
        rideNum = bundle.getString("rideNum");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_ride_map);
        checkForInternetAndGPS = new CheckForInternetAndGPS();

        if (checkForInternetAndGPS.isInternetConnected(RideInfoActivity.this) == false){
            Toast.makeText(this, "Molim omogućite Internet", Toast.LENGTH_SHORT).show();
        }


        databaseReference = FirebaseDatabase.getInstance().getReference().child(studentName).child(rideNum);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Students students = dataSnapshot.getValue(Students.class);
                for (int i = 0; i < students.getLat().size(); i++){
                    lat.add(students.getLat().get(i));
                    lng.add(students.getLng().get(i));
                }
                mapFragment.getMapAsync(RideInfoActivity.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onBackPressed() {
        lat.clear();
        lng.clear();
        super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.google_style);
        mMap.setMapStyle(styleOptions);
        LatLng ll = new LatLng(Double.valueOf(lat.get(0)), Double.valueOf(lng.get(0)));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, 15);
        mMap.animateCamera(cameraUpdate);

        Polyline line;

        for (int i = 1; i < lat.size(); i++){
            line = mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(Double.valueOf(lat.get(i-1)), Double.valueOf(lng.get(i-1))), new LatLng(Double.valueOf(lat.get(i)), Double.valueOf(lng.get(i))))
                    .width(5)
                    .color(Color.RED));
        }



    }
}
