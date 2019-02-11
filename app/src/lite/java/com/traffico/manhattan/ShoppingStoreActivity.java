package com.traffico.manhattan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.traffico.manhattan.clases.CustomAdapterListViewStore;
import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Tienda;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class ShoppingStoreActivity extends AppCompatActivity {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_store);
        getSupportActionBar().setTitle(R.string.stores);
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        fab = (FloatingActionButton) findViewById(R.id.fabAddProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iStore = new Intent(ShoppingStoreActivity.this, StoreActivity.class);
                iStore.putExtra("Llamada", "ShoppingStoreActivity");
                startActivity(iStore);
            }
        });

        if (db != null) {
            loadStores(db, dbHelper);
        }
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(ShoppingStoreActivity.this, MenuActivity.class);
        startActivity(iMenu);
        finish();
    }

    private void loadStores(SQLiteDatabase db, MyOpenHelper dbHelper) {
        try {
            CustomAdapterListViewStore adapter;
            int imageEdit = R.drawable.ic_input_get;

            ArrayList<Tienda> tiendaList = dbHelper.getTiendas(db);
            final ListView lvStores = findViewById(R.id.lvStores);
            adapter = new CustomAdapterListViewStore(this, tiendaList, imageEdit);
            lvStores.setAdapter(adapter);
            lvStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent iShopping = new Intent(ShoppingStoreActivity.this, ShoppingActivity.class);
                    Tienda tienda = (Tienda) lvStores.getItemAtPosition(position);
                    iShopping.putExtra("Store", tienda);
                    startActivity(iShopping);

                }
            });

            if (tiendaList.size() == 0) {
                Toast.makeText(getBaseContext(), R.string.empty_stores, Toast.LENGTH_SHORT).show();
                fab.callOnClick();
            }

            final android.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            if (tiendaList.size() == 3) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                    }
                });
            }

            /*
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
            });*/
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.empty_stores, Toast.LENGTH_SHORT).show();
            //Log.e("Error", "loadStores: " + e.getMessage(), null);
        }
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
