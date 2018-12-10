package com.traffico.manhattan;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Tienda;
import com.traffico.manhattan.entidades.Usuario;

import java.util.ArrayList;
import java.util.Iterator;


public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMapClickListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    //
    String llamada, llamadaMaps, lat, lon;
    //
    boolean flagLatLng = false;
    //
    Tienda tienda;

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
        llamadaMaps = (String) iMapsActivity.getSerializableExtra("LlamadaMaps");
        tienda = (Tienda) iMapsActivity.getSerializableExtra("Store");
        //
        try {
            if (iMapsActivity.getExtras().get("latLng") != null) {
                String latLng = iMapsActivity.getExtras().get("latLng").toString();
                if (latLng != null) {
                    int flagPos = latLng.indexOf(":");
                    lat = latLng.substring(0, flagPos);
                    lon = latLng.substring(flagPos + 1, latLng.length());
                    flagLatLng = true;
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        Intent iMaps;
        switch (llamadaMaps) {
            case "MainActivity":
                iMaps = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(iMaps);
                finish();
                break;
            case "EditProfileActivity":
                iMaps = new Intent(MapsActivity.this, EditProfileActivity.class);
                startActivity(iMaps);
                finish();
                break;
            case "StoreActivity":
                iMaps = new Intent(MapsActivity.this, StoreActivity.class);
                iMaps.putExtra("Llamada", llamada);
                startActivity(iMaps);
                finish();
                break;
            case "UpdateStoreActivity":
                iMaps = new Intent(MapsActivity.this, MainActivity.class);
                iMaps.putExtra("Store", tienda);
                startActivity(iMaps);
                finish();
                break;
            case "NotificationFragment":
                finish();
                break;
            default:
                Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            enableMyLocation();
            mMap = googleMap;
            allMarker();
            LatLng colombia = new LatLng(4.6420884, -72.834157);
            LatLng ubicacion = new LatLng(4.6420884, -72.834157);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombia, 5));
            MarkerOptions markerOptions = new MarkerOptions();
            double iLat;
            double iLon;
            LatLng residencia, lTienda;
            switch (llamadaMaps) {
                case "EditProfileActivity":
                    if (flagLatLng) {
                        iLat = Double.parseDouble(lat);
                        iLon = Double.parseDouble(lon);
                        residencia = new LatLng(iLat, iLon);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(residencia, 15));
                        markerOptions.position(residencia).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        markerOptions.title("Tu Residencia");
                        mMap.addMarker(markerOptions);
                        ubicacion = residencia;
                    }
                    break;
                case "StoreActivity":
                    break;
                case "UpdateStoreActivity":
                    if (flagLatLng) {
                        iLat = Double.parseDouble(lat);
                        iLon = Double.parseDouble(lon);
                        lTienda = new LatLng(iLat, iLon);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lTienda, 15));
                        markerOptions.position(lTienda).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        markerOptions.title(tienda.getDescripcion());
                        mMap.addMarker(markerOptions);
                        ubicacion = lTienda;
                    }
                    break;
                default:
                    colombia = new LatLng(4.6420884, -72.834157);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombia, 5));
                    ubicacion = colombia;
                    break;
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLng(ubicacion));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(ubicacion));
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
            mMap.setOnMapClickListener(this);
            enableMyLocation();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        Marker maProfile = null;
        Marker maStore = null;
        boolean flagCase = true;
        //
        switch (llamadaMaps) {
            case "MainActivity":
                MarkerOptions mProfile = new MarkerOptions();
                mProfile.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mProfile.title("Residencia");
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                maProfile = mMap.addMarker(mProfile);
                break;
            case "EditProfileActivity":
                mProfile = new MarkerOptions();
                mProfile.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mProfile.title("Residencia");
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                maProfile = mMap.addMarker(mProfile);
                break;
            case "StoreActivity":
                MarkerOptions mStore = new MarkerOptions();
                mStore.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mStore.title("Tienda");
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                maStore = mMap.addMarker(mStore);
                break;
            case "UpdateStoreActivity":
                mStore = new MarkerOptions();
                mStore.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mStore.title(tienda.getDescripcion());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                maStore = mMap.addMarker(mStore);
                break;
            default:
                flagCase = false;
                break;
        }
        if (flagCase) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MapsActivity.this);
            dialog.setCancelable(false);
            dialog.setTitle(R.string.do_you_want);
            dialog.setMessage(R.string.select_this_place);
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (llamadaMaps) {
                        case "MainActivity":
                            Intent iMaps;
                            iMaps = new Intent(MapsActivity.this, MainActivity.class);
                            iMaps.putExtra("Ubicacion", latLng);
                            startActivity(iMaps);
                            finish();
                            break;
                        case "EditProfileActivity":
                            iMaps = new Intent(MapsActivity.this, EditProfileActivity.class);
                            iMaps.putExtra("Ubicacion", latLng);
                            startActivity(iMaps);
                            finish();
                            break;
                        case "StoreActivity":
                            iMaps = new Intent(MapsActivity.this, StoreActivity.class);
                            iMaps.putExtra("Llamada", llamada);
                            iMaps.putExtra("Ubicacion", latLng);
                            startActivity(iMaps);
                            finish();
                            break;
                        case "UpdateStoreActivity":
                            iMaps = new Intent(MapsActivity.this, UpdateStoreActivity.class);
                            iMaps.putExtra("Store", tienda);
                            iMaps.putExtra("Ubicacion", latLng);
                            startActivity(iMaps);
                            finish();
                            break;
                    }
                }
            });
            final Marker finalMaProfile = maProfile;
            final Marker finalMaStore = maStore;
            dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (llamadaMaps) {
                        case "MainActivity":
                            finalMaProfile.remove();
                            break;
                        case "EditProfileActivity":
                            finalMaProfile.remove();
                            break;
                        case "StoreActivity":
                            finalMaStore.remove();
                            break;
                        case "UpdateStoreActivity":
                            finalMaStore.remove();
                            break;
                    }

                }
            });
            dialog.show();
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
            //
        }
    }

    private void allMarker() {
        try {
            MyOpenHelper dbHelper = new MyOpenHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                LatLng residencia;
                MarkerOptions markerOptions = new MarkerOptions();
                Usuario usuario = dbHelper.getUsuario(db);
                int flagPos;
                Double iLat;
                Double iLon;
                //
                if (usuario.getId() > 0) {
                    flagPos = usuario.getCoordenadas().indexOf(":");
                    String lat = usuario.getCoordenadas().substring(0, flagPos);
                    String lon = usuario.getCoordenadas().substring(flagPos + 1, usuario.getCoordenadas().length());
                    iLat = Double.parseDouble(lat);
                    iLon = Double.parseDouble(lon);
                    residencia = new LatLng(iLat, iLon);
                    markerOptions.position(residencia).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    markerOptions.title(usuario.getNombre() + " " + usuario.getApellido());
                    mMap.addMarker(markerOptions);
                }
                //
                ArrayList<Tienda> tiendas = dbHelper.getTiendas(db);
                Iterator<Tienda> iTienda = tiendas.iterator();
                while (iTienda.hasNext()) {
                    Tienda tienda = iTienda.next();
                    //
                    LatLng lTienda;
                    markerOptions = new MarkerOptions();
                    flagPos = tienda.getCoordenadas().indexOf(":");
                    lat = tienda.getCoordenadas().substring(0, flagPos);
                    lon = tienda.getCoordenadas().substring(flagPos + 1, tienda.getCoordenadas().length());
                    iLat = Double.parseDouble(lat);
                    iLon = Double.parseDouble(lon);
                    lTienda = new LatLng(iLat, iLon);
                    markerOptions.position(lTienda).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    markerOptions.title(tienda.getDescripcion());
                    mMap.addMarker(markerOptions);
                    //
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
    }
}
