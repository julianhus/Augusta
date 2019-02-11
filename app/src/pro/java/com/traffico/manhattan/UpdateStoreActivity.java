package com.traffico.manhattan;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.ArrayList;

public class UpdateStoreActivity extends AppCompatActivity {

    Tienda tienda;
    Spinner sMunicipio, sDepartamento;
    ImageButton ibMap;
    ImageView ivCheckMap;
    //
    EditText etLocation, eTAddress, etDescription;
    //
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_store);
        getSupportActionBar().setTitle(R.string.update_store);
        //
        etLocation = findViewById(R.id.etLocation);
        eTAddress = findViewById(R.id.etAddress);
        Intent iUpdateStore = getIntent();
        tienda = (Tienda) iUpdateStore.getSerializableExtra("Store");
        //
        LatLng latLng;
        latLng = (LatLng) iUpdateStore.getExtras().get("Ubicacion");
        address = (String) iUpdateStore.getExtras().get("Address");
        if (latLng != null) {
            etLocation.setText(latLng.latitude + ":" + latLng.longitude);
        }else{
            etLocation.setText(tienda.getCoordenadas());
        }
        ibMap = findViewById(R.id.ibMap);
        ibMap.setBackgroundColor(Color.parseColor("#FF008577"));
        ivCheckMap =findViewById(R.id.ivCheckMap);
        etLocation.setEnabled(false);
        //
        if (!etLocation.getText().toString().isEmpty()) {
            ivCheckMap.setBackgroundColor(Color.parseColor("#FF008577"));
        }
        //
        sDepartamento = findViewById(R.id.sState);
        sMunicipio = findViewById(R.id.sCity);
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadSpinners(db, dbHelper);
        }
        eTAddress.setText(tienda.getDireccion());
        //
        if(address != null){
            eTAddress.setText(address);
        }
        //
        etDescription = findViewById(R.id.etDescription);
        etDescription.setText(tienda.getDescripcion());
        Departamento departamento = new Departamento();
        for (int i = 0; i < sDepartamento.getAdapter().getCount(); i++) {
            departamento = (Departamento) sDepartamento.getAdapter().getItem(i);
            if (tienda.getMunicipio().getDepartamento().getId() == departamento.getId()) {
                sDepartamento.setSelection(i);
                i = sDepartamento.getAdapter().getCount();
            }
        }
        //
        loadSpinnerMunicipio(departamento);
        for (int i = 0; i < sMunicipio.getAdapter().getCount(); i++) {
            Municipio municipio = (Municipio) sMunicipio.getAdapter().getItem(i);
            if (tienda.getMunicipio().getId() == municipio.getId()) {
                sMunicipio.setSelection(i, true);
                i = sMunicipio.getAdapter().getCount();
            }
        }
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
            //Spinner sDepartamento = findViewById(R.id.sState);
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
            Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSpinnerMunicipio() {
        ArrayList<Municipio> municipioList = new ArrayList<>();
        municipioList.add(0, new Municipio(0, getResources().getString(R.string.select)));
        ArrayAdapter<Municipio> aMunicipio = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, municipioList);
        aMunicipio.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sMunicipio.setAdapter(aMunicipio);
    }

    private void loadSpinnerMunicipio(Departamento departamento) {
        ArrayList<Municipio> municipioList = departamento.getMunicipios();
        if (municipioList.get(0).getId() != 0) {
            municipioList.add(0, new Municipio(0, getResources().getString(R.string.select)));
        }
        ArrayAdapter<Municipio> aMunicipio = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, municipioList);
        aMunicipio.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sMunicipio.setAdapter(aMunicipio);
    }

    public void updateStore(View view){
        EditText eTDescription = findViewById(R.id.etDescription);
        EditText eTAddress = findViewById(R.id.etAddress);
        EditText eTLocation = findViewById(R.id.etLocation);
        Spinner sMunicipio = findViewById(R.id.sCity);
        boolean flagCheck = validate(true);
        if (!flagCheck) {
            Toast.makeText(getBaseContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();
        } else {
            tienda.setDescripcion(eTDescription.getText().toString());
            tienda.setDireccion(eTAddress.getText().toString());
            tienda.setCoordenadas(eTLocation.getText().toString());
            Municipio municipio = (Municipio) sMunicipio.getSelectedItem();
            tienda.setMunicipio(municipio);
            MyOpenHelper dbHelper = new MyOpenHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                int flagInsert = dbHelper.updateTienda(db, tienda);
                Toast.makeText(getBaseContext(), R.string.update, Toast.LENGTH_SHORT).show();
                Intent iStoreListActivity = new Intent(UpdateStoreActivity.this,StoreListActivity.class);
                startActivity(iStoreListActivity);
            }
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
        Intent iMaps = new Intent(UpdateStoreActivity.this, MapsActivity.class);
        iMaps.putExtra("LlamadaMaps", "UpdateStoreActivity");
        EditText eTLocation = findViewById(R.id.etLocation);
        if (!eTLocation.getText().toString().equals("")) {
            iMaps.putExtra("latLng", eTLocation.getText());
        }
        iMaps.putExtra("Store", tienda);
        startActivity(iMaps);
    }
}
