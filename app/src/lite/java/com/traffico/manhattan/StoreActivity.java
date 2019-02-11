package com.traffico.manhattan;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Departamento;
import com.traffico.manhattan.entidades.Municipio;
import com.traffico.manhattan.entidades.Tienda;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {

    String llamada, address;
    ImageButton ibMap;
    LatLng latLng;
    ImageView ivCheckMap;
    //
    EditText eTLocation, eTAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        getSupportActionBar().setTitle(R.string.store);
        ibMap = findViewById(R.id.ibMap);
        ibMap.setBackgroundColor(Color.parseColor("#FF008577"));
        ivCheckMap =findViewById(R.id.ivCheckMap);
        eTLocation = findViewById(R.id.etLocation);
        eTLocation.setEnabled(false);
        eTAddress = findViewById(R.id.etAddress);
        //
        Intent iStoreActivity = getIntent();
        llamada = (String) iStoreActivity.getSerializableExtra("Llamada");
        //
        //
        Intent iMaps = getIntent();
        latLng = (LatLng) iMaps.getExtras().get("Ubicacion");
        address = (String) iMaps.getExtras().get("Address");
        //
        if (latLng != null) {
            eTLocation.setText(latLng.latitude + ":" + latLng.longitude);
            eTLocation.setEnabled(false);
        }
        //
        if(address != null){
            eTAddress.setText(address);
        }
        //
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadSpinners(db, dbHelper);
        }
        //
        if (!eTLocation.getText().toString().isEmpty()) {
            ivCheckMap.setBackgroundColor(Color.parseColor("#FF008577"));
        }
        //
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(this, MenuActivity.class);
        startActivity(iMenu);
        finish();
    }

    private void loadSpinners(SQLiteDatabase db, MyOpenHelper dbHelper) {
        ArrayList<Departamento> departamentoList = dbHelper.getDepartamentos(db);
        try {
            if (departamentoList.get(0).getId() != 0) {
                departamentoList.add(0, new Departamento(0, getResources().getString(R.string.select)));
            }
            Spinner sDepartamento = findViewById(R.id.sState);
            ArrayAdapter<Departamento> aDepartamento = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, departamentoList);
            aDepartamento.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            sDepartamento.setAdapter(aDepartamento);
            sDepartamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if ((position != 0) && (id != 0)) {
                        Departamento departamento = (Departamento) parent.getItemAtPosition(position);
                        loadSpinnerMunicipio(departamento);
                    } else {
                        loadSpinnerMunicipio();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        } catch (Exception e) {
            //Log.e("Error", "loadSpinners: " + e.getMessage(), null);
        }
    }

    private void loadSpinnerMunicipio() {
        ArrayList<Municipio> municipioList = new ArrayList<>();
        municipioList.add(0, new Municipio(0, getResources().getString(R.string.select)));
        Spinner sMunicipio = findViewById(R.id.sCity);
        ArrayAdapter<Municipio> aMunicipio = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, municipioList);
        aMunicipio.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sMunicipio.setAdapter(aMunicipio);
    }

    private void loadSpinnerMunicipio(Departamento departamento) {
        ArrayList<Municipio> municipioList = departamento.getMunicipios();
        if (municipioList.get(0).getId() != 0) {
            municipioList.add(0, new Municipio(0, getResources().getString(R.string.select)));
        }
        Spinner sMunicipio = findViewById(R.id.sCity);
        ArrayAdapter<Municipio> aMunicipio = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, municipioList);
        aMunicipio.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sMunicipio.setAdapter(aMunicipio);
    }

    public void createStore(View view) {
        try {
            EditText eTDescription = findViewById(R.id.etDescription);
            EditText eTAddress = findViewById(R.id.etAddress);
            EditText eTLocation = findViewById(R.id.etLocation);
            Spinner sMunicipio = findViewById(R.id.sCity);
            boolean flagCheck = validate(true);
            if (!flagCheck) {
                Toast.makeText(getBaseContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();
            } else {
                Tienda tienda = new Tienda();
                tienda.setDescripcion(eTDescription.getText().toString());
                tienda.setDireccion(eTAddress.getText().toString());
                tienda.setCoordenadas(eTLocation.getText().toString());
                Municipio municipio = new Municipio();
                municipio = (Municipio) sMunicipio.getSelectedItem();
                tienda.setMunicipio(municipio);
                MyOpenHelper dbHelper = new MyOpenHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db != null) {
                    long flagInsert = dbHelper.insertTienda(db, tienda);
                    if (flagInsert > 0) {
                        Toast.makeText(getBaseContext(), R.string.created, Toast.LENGTH_SHORT).show();
                        Intent storeIntent = new Intent();
                        if (llamada.equals("StoreListActivity")) {
                            storeIntent = new Intent(this, StoreListActivity.class);
                        }
                        if (llamada.equals("RecordPriceStoreActivity")) {
                            storeIntent = new Intent(this, RecordPriceStoreActivity.class);
                        }
                        if (llamada.equals("ShoppingStoreActivity")) {
                            storeIntent = new Intent(this, ShoppingStoreActivity.class);
                        }
                        //
                        final Intent finalStoreIntent = storeIntent;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Magic here
                                startActivity(finalStoreIntent);
                            }
                        }, 1000); // Millisecon
                        //
                    } else {
                        Toast.makeText(getBaseContext(), R.string.same_address, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate(boolean flagCheck) {
        EditText eTDescription = findViewById(R.id.etDescription);
        EditText eTAddress = findViewById(R.id.etAddress);
        //EditText eTLocation = findViewById(R.id.etLocation);
        TextView tvCity = findViewById(R.id.tvCity);
        TextView tvState = findViewById(R.id.tvState);
        TextView tvDescription = findViewById(R.id.tvDesciption);
        TextView tvAddress = findViewById(R.id.tvAddress);
        //TextView tvLocation = findViewById(R.id.tvLocation);
        Spinner sMunicipio = findViewById(R.id.sCity);
        Spinner sDepartamento = findViewById(R.id.sState);

        if (sDepartamento.getSelectedItemPosition() <= 0) {
            tvState.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvState.setTextColor(-1979711488);
        }
        if (sMunicipio.getSelectedItemPosition() <= 0) {
            tvCity.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvCity.setTextColor(-1979711488);
        }
        /*
        if (eTLocation.getText().toString().isEmpty()) {
            tvLocation.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvLocation.setTextColor(-1979711488);
        }
        */
        if (eTAddress.getText().toString().isEmpty()) {
            tvAddress.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvAddress.setTextColor(-1979711488);
        }
        if (eTDescription.getText().toString().isEmpty()) {
            tvDescription.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvDescription.setTextColor(-1979711488);
        }
        return flagCheck;
    }

    public void showMap(View view) {
        android.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.component_not_available);

        dialog.setMessage(R.string.the_full_version_has);
        dialog.setCancelable(false);
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                externalEstorageLite();
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.traffico.mercabarato");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
        /*
        Intent iMaps = new Intent(StoreActivity.this, MapsActivity.class);
        iMaps.putExtra("Llamada", llamada);
        iMaps.putExtra("LlamadaMaps", "StoreActivity");
        startActivity(iMaps);
        */
    }

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    private void externalEstorageLite() {
        try {

            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck < 0) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                File origen = new File("/data/data/com.traffico.mercabaratolite/databases/manhattan.sqlite");
                File destino = new File("/sdcard/manhattan.sqlite");
                FileChannel inChannel = new FileInputStream(origen).getChannel();
                FileChannel outChannel = new FileOutputStream(destino).getChannel();
                try
                {
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                }
                finally
                {
                    if (inChannel != null)
                        inChannel.close();
                    if (outChannel != null)
                        outChannel.close();
                }
            }

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity", "onCreate: ", e.getCause());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), R.string.we_will_not_be, Toast.LENGTH_LONG).show();
                }
                return;

            }
        }
    }
}
