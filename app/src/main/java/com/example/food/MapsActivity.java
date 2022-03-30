package com.example.food;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener{

    private static final String TAG = "MapsActivity";
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient client;
    private GoogleMap mMap;
    private Geocoder geocoder;
    private EditText CurrentLoc;
    private EditText CurrentLoc2;
    private EditText CurrentLoc3;
    private Button CurrentLocation;
    private EditText EtSearch;
    private ImageView SrcIcon;
    String mes1;
    String mes2;
    String mes3;
    public static final String teks1 = "lat";
    public static final String teks2 = "lon";
    public static final String teks3 = "loc";

    public static final String teks4 = "pname";
    public static final String teks5 = "pic";
    public static final String teks6 = "panel";
    public static final String teks7 = "lamp";
    public static final String teks8 = "pole";
    public static final String teks9 = "others";

    public static final String teks10 = "pname2";
    public static final String teks11 = "pic2";
    public static final String teks12 = "panel2";
    public static final String teks13 = "lamp2";
    public static final String teks14 = "pole2";
    public static final String teks15 = "others2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent ekspor2 = getIntent();
        final String mes4 = ekspor2.getStringExtra(teks4);
        final String mes5 = ekspor2.getStringExtra(teks5);
        final String mes6 = ekspor2.getStringExtra(teks6);
        final String mes7 = ekspor2.getStringExtra(teks7);
        final String mes8 = ekspor2.getStringExtra(teks8);
        final String mes9 = ekspor2.getStringExtra(teks9);

        CurrentLoc = (EditText) findViewById(R.id.currentloc);
        CurrentLoc2 = (EditText) findViewById(R.id.currentloc2);
        CurrentLoc3 = (EditText) findViewById(R.id.currentloc3);
        EtSearch = (EditText) findViewById(R.id.et_search);
        SrcIcon = (ImageView) findViewById(R.id.search_icon);
        SrcIcon.setOnClickListener(this::geoLocate);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        client = LocationServices.getFusedLocationProviderClient(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        CurrentLocation = findViewById(R.id.btn_currentloc);
        CurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mes1 = CurrentLoc.getText().toString();
                mes2 = CurrentLoc2.getText().toString();
                mes3 = CurrentLoc3.getText().toString();
                Intent ekspor = new Intent(MapsActivity.this, MainActivity.class);
                ekspor.putExtra(teks1,mes1);
                ekspor.putExtra(teks2,mes2);
                ekspor.putExtra(teks3,mes3);
                ekspor.putExtra(teks10,mes4);
                ekspor.putExtra(teks11,mes5);
                ekspor.putExtra(teks12,mes6);
                ekspor.putExtra(teks13,mes7);
                ekspor.putExtra(teks14,mes8);
                ekspor.putExtra(teks15,mes9);
                startActivity(ekspor);
            }
        });
    }

    private void geoLocate(View view) {
        String LocationName = EtSearch.getText().toString();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocationName(LocationName, 1);

            if (addressList.size() > 0){
                Address address = addressList.get(0);

                gotoLocation(address.getLatitude(),address.getLongitude());

//                mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())));

                Toast.makeText(this, address.getLocality(), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gotoLocation(double latitude, double longitude) {
        LatLng LatLng = new LatLng(latitude, longitude);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng, 16);
        mMap.moveCamera(cameraUpdate);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("Default Position").draggable(true);

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.setMyLocationEnabled(true);
        //mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);

        try {
            List<Address> addresses = geocoder.getFromLocationName("Surabaya",1);
            if (addresses.size() > 0){
                Address address = addresses.get(0);
                LatLng surabaya = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {
        Log.d(TAG,"onMarkerDrag: ");
    }

    //    @SuppressLint("SetTextI18n")
    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        Log.d(TAG,"onMarkerDragEnd: ");
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0){
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                String latitude = String.valueOf(address.getLatitude());
                String longitude = String.valueOf(address.getLongitude());
                marker.setTitle("Current Position");
                CurrentLoc.setText(latitude);
                CurrentLoc2.setText(longitude);
                CurrentLoc3.setText(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {
        Log.d(TAG,"onMarkerDragStart: ");
    }
}