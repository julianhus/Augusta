package com.traffico.manhattan;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.List;
import java.util.Locale;


public class MapsActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMapClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    //
    LocationManager locationManager;
    //
    String llamada, llamadaMaps, lat, lon;
    //
    boolean flagLatLng = false;
    //
    Tienda tienda;
    //
    double longitudeGPS, latitudeGPS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //
        getUbicacionGPS();
        //
        Intent iMapsActivity = getIntent();
        llamada = (String) iMapsActivity.getSerializableExtra("Llamada");
        llamadaMaps = (String) iMapsActivity.getSerializableExtra("LlamadaMaps");
        tienda = (Tienda) iMapsActivity.getSerializableExtra("Store");
        //
        titulo();
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

    private void titulo() {
        switch (llamadaMaps) {
            case "MainActivity":
                getSupportActionBar().setTitle(R.string.app_name);
                break;
            case "EditProfileActivity":
                getSupportActionBar().setTitle(R.string.profile);
                break;
            case "StoreActivity":
                getSupportActionBar().setTitle(R.string.store);
                break;
            case "UpdateStoreActivity":
                getSupportActionBar().setTitle(R.string.update_store);
                break;
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
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            enableMyLocation();
            allMarker();
            //LatLng colombia = new LatLng(4.6420884, -72.834157);
            LatLng ubicacion = new LatLng(latitudeGPS, longitudeGPS);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 5));
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
                    //colombia = new LatLng(4.6420884, -72.834157);
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(colombia, 5));
                    //ubicacion = colombia;
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
            dialog.setMessage(getAddress(latLng) + "\n\n" + getString(R.string.select_this_place));
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (llamadaMaps) {
                        case "MainActivity":
                            Intent iMaps;
                            iMaps = new Intent(MapsActivity.this, MainActivity.class);
                            iMaps.putExtra("Ubicacion", latLng);
                            iMaps.putExtra("Address", getAddress(latLng));
                            startActivity(iMaps);
                            finish();
                            break;
                        case "EditProfileActivity":
                            iMaps = new Intent(MapsActivity.this, EditProfileActivity.class);
                            iMaps.putExtra("Ubicacion", latLng);
                            iMaps.putExtra("Address", getAddress(latLng));
                            startActivity(iMaps);
                            finish();
                            break;
                        case "StoreActivity":
                            iMaps = new Intent(MapsActivity.this, StoreActivity.class);
                            iMaps.putExtra("Llamada", llamada);
                            iMaps.putExtra("Ubicacion", latLng);
                            iMaps.putExtra("Address", getAddress(latLng));
                            startActivity(iMaps);
                            finish();
                            break;
                        case "UpdateStoreActivity":
                            iMaps = new Intent(MapsActivity.this, UpdateStoreActivity.class);
                            iMaps.putExtra("Store", tienda);
                            iMaps.putExtra("Ubicacion", latLng);
                            iMaps.putExtra("Address", getAddress(latLng));
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

    private String getAddress(LatLng latLng) {
        //
        String address = "";
        try {
            Geocoder geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.isEmpty()) {
                address = "Waiting for Location";
                Toast.makeText(getApplicationContext(), "Waiting for Location", Toast.LENGTH_LONG).show();
            } else {
                if (addresses.size() > 0 && addresses.get(0).getThoroughfare() != null && addresses.get(0).getSubThoroughfare() != null) {
                    address = addresses.get(0).getThoroughfare() + " # " + addresses.get(0).getSubThoroughfare();
                    Toast.makeText(getApplicationContext(), addresses.get(0).getThoroughfare() + " # " + addresses.get(0).getSubThoroughfare(), Toast.LENGTH_LONG).show();
                } else {
                    address = getString(R.string.location_without_address);
                    Toast.makeText(getApplicationContext(), getString(R.string.location_without_address), Toast.LENGTH_LONG).show();
                }
            }
            return address;
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
        //
        return address;
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
                if (usuario.getId() > 0 && !usuario.getCoordenadas().isEmpty()) {
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
                    if (flagPos > 0) {
                        lat = tienda.getCoordenadas().substring(0, flagPos);
                        lon = tienda.getCoordenadas().substring(flagPos + 1, tienda.getCoordenadas().length());
                        iLat = Double.parseDouble(lat);
                        iLon = Double.parseDouble(lon);
                        lTienda = new LatLng(iLat, iLon);
                        markerOptions.position(lTienda).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        markerOptions.title(tienda.getDescripcion());
                        mMap.addMarker(markerOptions);
                    }
                    //
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //
    private void getUbicacionGPS() {
        if (!checkLocation()) return;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 20 * 1000, 10, locationListenerGPS);

    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private final LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!flagLatLng)
                        onMapReady(mMap);
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    };

//
}
