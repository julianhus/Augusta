package com.traffico.manhattan;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.traffico.manhattan.clases.CustomAdapterListViewStore;
import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Tienda;

import java.util.ArrayList;

public class StoreListActivity extends AppCompatActivity {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_list);
        getSupportActionBar().setTitle(R.string.stores);
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        fab = (FloatingActionButton) findViewById(R.id.fabAddProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iStore = new Intent(StoreListActivity.this, StoreActivity.class);
                iStore.putExtra("Llamada", "StoreListActivity");
                startActivity(iStore);
            }
        });

        if (db != null) {
            loadStores(db, dbHelper);
        }
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(StoreListActivity.this, MenuActivity.class);
        startActivity(iMenu);
    }

    private void loadStores(SQLiteDatabase db, MyOpenHelper dbHelper) {
        try {
            CustomAdapterListViewStore adapter;
            int imageEdit = R.drawable.ic_menu_edit;

            ArrayList<Tienda> tiendaList = dbHelper.getTiendas(db);
            final ListView lvStores = findViewById(R.id.lvStores);
            adapter = new CustomAdapterListViewStore(this, tiendaList, imageEdit);
            lvStores.setAdapter(adapter);
            lvStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent iUpdateStore = new Intent(StoreListActivity.this, UpdateStoreActivity.class);
                    Tienda tienda = (Tienda) lvStores.getItemAtPosition(position);
                    iUpdateStore.putExtra("Store",tienda);
                    startActivity(iUpdateStore);

                }
            });
            if(tiendaList.size() == 0){
                Toast.makeText(getBaseContext(), R.string.empty_stores, Toast.LENGTH_SHORT).show();
                fab.callOnClick();
            }
            /*
            ArrayList<Tienda> tiendaList = dbHelper.getTiendas(db);
            final ListView lvStores = findViewById(R.id.lvStores);
            ArrayAdapter<Tienda> aTienda = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tiendaList);
            lvStores.setAdapter(aTienda);
            lvStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent iUpdateStore = new Intent(StoreListActivity.this, UpdateStoreActivity.class);
                    Tienda tienda = (Tienda) lvStores.getItemAtPosition(position);
                    iUpdateStore.putExtra("Store",tienda);
                    startActivity(iUpdateStore);

                }
            });
        */
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.empty_stores, Toast.LENGTH_SHORT).show();
            //Log.e("Error", "loadStores: " + e.getMessage(), null);
        }
    }
}
