package com.traffico.manhattan;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.location.Location;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    String llamada, lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //
        Intent iMapsActivity = getIntent();
        llamada = (String) iMapsActivity.getSerializableExtra("Llamada");
        try {
            if (iMapsActivity.getExtras().get("latLng") != null) {
                String latLng = iMapsActivity.getExtras().get("latLng").toString();
                if (latLng != null) {
                    int flagPos = latLng.indexOf(":");
                    lat = latLng.substring(0, flagPos);
                    lon = latLng.substring(flagPos + 1, latLng.length());
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
        //
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            LatLng colombia;

            if (lat == null) {
                colombia = new LatLng(4.6420884, -78.834157);
            } else {
                double iLat = Double.parseDouble(lat);
                double iLon = Double.parseDouble(lon);
                colombia = new LatLng(iLat, iLon);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(colombia);
                markerOptions.title("Residencia");
                mMap.animateCamera(CameraUpdateFactory.newLatLng(colombia));
                mMap.addMarker(markerOptions);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLng(colombia));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombia, 5));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(colombia));
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            enableMyLocation();
            //
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(final LatLng latLng) {
                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();
                    // Setting the position for the marker
                    markerOptions.position(latLng);
                    // Setting the title for the marker.
                    // This will be displayed on taping the marker
                    markerOptions.title("Residencia");
                    // Clears the previously touched position
                    mMap.clear();
                    // Animating to the touched position
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    // Placing a marker on the touched position
                    mMap.addMarker(markerOptions);
                    try {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(MapsActivity.this);
                        dialog.setTitle(R.string.do_you_want);
                        dialog.setMessage(R.string.select_this_place);
                        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent iMaps;
                                switch (llamada) {
                                    case "EditProfileActivity":
                                        iMaps = new Intent(MapsActivity.this, EditProfileActivity.class);
                                        iMaps.putExtra("Ubicacion", latLng);
                                        startActivity(iMaps);
                                        break;
                                    case "MainActivity":
                                        iMaps = new Intent(MapsActivity.this, MainActivity.class);
                                        iMaps.putExtra("Ubicacion", latLng);
                                        startActivity(iMaps);
                                        break;
                                    default:
                                        Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMap.clear();
                            }
                        });
                        dialog.show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                        //Log.e("MapsActivity", "onMapClick: " + e.getMessage(), null);
                    }
                }
            });
            //
        }
    }
}
