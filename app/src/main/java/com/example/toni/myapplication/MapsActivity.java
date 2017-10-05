package com.example.toni.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;

import com.google.android.gms.location.LocationListener;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationsDatabaseHandler locationsDatabaseHandler;
    private int i = 1;
    private Button saveBtn;
    private DatabaseReference databaseReference;
    private Cursor cursor;
    private List<String> lat = new ArrayList<String>();
    private List<String> lng = new ArrayList<String>();
    private Students students;
    private String notes;
    private String rideNum;
    private CheckForInternetAndGPS checkForInternetAndGPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Button saveBtn = (Button) findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRide();
            }
        });
        locationsDatabaseHandler = new LocationsDatabaseHandler(this);
        checkForInternetAndGPS = new CheckForInternetAndGPS();

        if (googleServicesAvailable()) {
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public boolean googleServicesAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int isAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(isAvailable)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to play services.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(this, R.raw.google_style);
        mMap.setMapStyle(styleOptions);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onDestroy() {
        lat.clear();
        lng.clear();
        locationsDatabaseHandler.deleteAllLocations();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null){
            Toast.makeText(this, "Your GPS may have turned off.", Toast.LENGTH_LONG).show();
        }else {

            Polyline line;

            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, 15);
            mMap.animateCamera(cameraUpdate);

            locationsDatabaseHandler.addLocation(location.getLatitude(), location.getLongitude());
            cursor = locationsDatabaseHandler.getAllLocations();

            if (i == 1){
                line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude()))
                        .width(5)
                        .color(Color.RED));
                lat.add(String.valueOf(location.getLatitude()));
                lng.add(String.valueOf(location.getLongitude()));
                i++;
            }else {
                cursor.moveToPosition(i-2);
                line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(cursor.getDouble(1), cursor.getDouble(2)), new LatLng(location.getLatitude(), location.getLongitude()))
                        .width(5)
                        .color(Color.RED));
                lat.add(String.valueOf(cursor.getDouble(1)));
                lng.add(String.valueOf(cursor.getDouble(2)));
                i++;
            }
        }
    }

    private void saveRide() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.custom_notes_dialog, null);
        final EditText notesEt = (EditText) view.findViewById(R.id.notesEt);
        final EditText rideNumEt = (EditText) view.findViewById(R.id.rideNumEt);
        builder.setNegativeButton("Poništi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Potvrdi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                notes = notesEt.getText().toString();
                rideNum = rideNumEt.getText().toString();
                if (rideNum.trim().length() < 1){
                    Toast.makeText(MapsActivity.this, "Potrebno je upisati broj vožnje.", Toast.LENGTH_SHORT).show();
                    saveRide();
                }else{
                    if (checkForInternetAndGPS.isGPSEnabled(MapsActivity.this) == true && checkForInternetAndGPS.isInternetConnected(MapsActivity.this) == true){
                        Bundle bundle = getIntent().getExtras();
                        String studentName = bundle.getString("studentName");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                        String timestamp = sdf.format(new Date());
                        students = new Students(studentName, notes, timestamp, lat, lng, rideNum);
                        databaseReference = FirebaseDatabase.getInstance().getReference().child(studentName).child(rideNum);
                        databaseReference.setValue(students);
                        finish();
                        Toast.makeText(MapsActivity.this, "Vožnja je spremljena.", Toast.LENGTH_SHORT).show();
                    }else{

                    }


                }

            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
