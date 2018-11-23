package com.traffico.manhattan;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Tienda;

import java.util.ArrayList;

public class RecordPriceStoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_price_store);
        getSupportActionBar().setTitle(R.string.record_price_store);
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadStores(db, dbHelper);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddStore);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iStore = new Intent(RecordPriceStoreActivity.this, StoreActivity.class);
                iStore.putExtra("Llamada","RecordPriceStoreActivity");
                startActivity(iStore);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(RecordPriceStoreActivity.this, MenuActivity.class);
        startActivity(iMenu);
    }

    private void loadStores(SQLiteDatabase db, MyOpenHelper dbHelper) {
        try {
            ArrayList<Tienda> tiendaList = dbHelper.getTiendas(db);
            final ListView lvStores = findViewById(R.id.lvStores);
            ArrayAdapter<Tienda> aTienda = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tiendaList);
            lvStores.setAdapter(aTienda);
            lvStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent iRecordPriceProduct = new Intent(RecordPriceStoreActivity.this, RecordPriceProductActivity.class);
                    Tienda tienda = (Tienda) lvStores.getItemAtPosition(position);
                    iRecordPriceProduct.putExtra("Store",tienda);
                    startActivity(iRecordPriceProduct);

                }
            });
        } catch (Exception e) {
            Toast.makeText(getBaseContext(),R.string.empty_stores, Toast.LENGTH_SHORT).show();
            Log.e("Error", "loadStores: " + e.getMessage(), null);
        }
    }
}
