package com.traffico.augusta;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Tienda;

import java.util.ArrayList;

public class ShoppingStoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_store);
        getSupportActionBar().setTitle(R.string.stores);
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadStores(db, dbHelper);
        }
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(ShoppingStoreActivity.this, MenuActivity.class);
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
                    Intent iShopping = new Intent(ShoppingStoreActivity.this, ShoppingActivity.class);
                    Tienda tienda = (Tienda) lvStores.getItemAtPosition(position);
                    iShopping.putExtra("Store",tienda);
                    startActivity(iShopping);

                }
            });
        } catch (Exception e) {
            Toast.makeText(getBaseContext(),R.string.empty_stores, Toast.LENGTH_SHORT).show();
            Log.e("Error", "loadStores: " + e.getMessage(), null);
        }
    }
}
