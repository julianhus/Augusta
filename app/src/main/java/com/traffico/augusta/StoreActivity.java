package com.traffico.augusta;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Departamento;
import com.traffico.augusta.entidades.Municipio;
import com.traffico.augusta.entidades.Tienda;

import java.util.ArrayList;

public class StoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        getSupportActionBar().setTitle(R.string.store);
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadSpinners(db, dbHelper);
        }
    }

    @Override
    public void onBackPressed() {
        Intent iStoreList = new Intent(StoreActivity.this, StoreListActivity.class);
        startActivity(iStoreList);
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
            Log.e("Error", "loadSpinners: " + e.getMessage(), null);
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
                    final Intent storeIntent = new Intent(this, StoreListActivity.class);
                    //
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Magic here
                            startActivity(storeIntent);
                        }
                    }, 1000); // Millisecon
                    //

                }else {
                    Toast.makeText(getBaseContext(), "Fail", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private boolean validate(boolean flagCheck) {
        EditText eTDescription = findViewById(R.id.etDescription);
        EditText eTAddress = findViewById(R.id.etAddress);
        EditText eTLocation = findViewById(R.id.etLocation);
        TextView tvCity = findViewById(R.id.tvCity);
        TextView tvDescription = findViewById(R.id.tvDesciption);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvLocation = findViewById(R.id.tvLocation);
        Spinner sMunicipio = findViewById(R.id.sCity);

        if (sMunicipio.getSelectedItemPosition() <= 0) {
            tvCity.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvCity.setTextColor(-1979711488);
        }
        if (eTLocation.getText().toString().isEmpty()) {
            tvLocation.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvLocation.setTextColor(-1979711488);
        }
        if (eTAddress.getText().toString().isEmpty()) {
            tvAddress.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvAddress.setTextColor(-1979711488);
        }
        if (eTDescription.getText().toString().isEmpty()) {
            tvDescription.setTextColor(Color.rgb(200, 0, 0));
        } else {
            tvDescription.setTextColor(-1979711488);
        }
        return flagCheck;
    }
}
