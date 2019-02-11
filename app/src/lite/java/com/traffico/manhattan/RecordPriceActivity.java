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

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Producto;
import com.traffico.manhattan.entidades.Tienda;
import com.traffico.manhattan.entidades.TiendaProducto;
import com.traffico.manhattan.entidades.ValorProducto;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class RecordPriceActivity extends AppCompatActivity {

    Tienda tienda;
    Producto producto;
    EditText etPrice, etEquivalentPrice;
    TextView tvPrice;
    Boolean flagLite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_price);
        getSupportActionBar().setTitle(R.string.record_price);

        Intent iRecordPrice = getIntent();
        tienda = (Tienda) iRecordPrice.getSerializableExtra("Store");
        producto = (Producto) iRecordPrice.getSerializableExtra("Product");

        TextView tvStore = findViewById(R.id.tvStore);
        tvStore.setText(tienda.toString());
        TextView tvProduct = findViewById(R.id.tViewProduct);
        tvProduct.setText(producto.toString());

        etPrice = findViewById(R.id.etPrice);
        tvPrice = findViewById(R.id.tvPrice);
        etEquivalentPrice = findViewById(R.id.etEquivalentPrice);

        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadProductPrice(db, dbHelper);
        }

        final float weight;
        weight = producto.getValorMedida();
        //
        etPrice.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                etPrice = (EditText) findViewById(R.id.etPrice);
                etEquivalentPrice = (EditText) findViewById(R.id.etEquivalentPrice);
                try {
                    if (weight != 0) {
                        float price = Float.parseFloat(etPrice.getText().toString());
                        float equivalentPrice = price / weight;
                        etEquivalentPrice.setText(String.valueOf(equivalentPrice));
                    } else {
                        etEquivalentPrice.setText("0");
                    }
                } catch (Exception e) {
                    //Log.e("RecordPriceActivity", "onKey: " + e.getMessage(), null);
                }
                return false;
            }
        });

        etPrice.setInputType(InputType.TYPE_NULL);
        etPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent iRecordPriceProduct = new Intent(RecordPriceActivity.this, RecordPriceProductActivity.class);
        iRecordPriceProduct.putExtra("Store", tienda);
        startActivity(iRecordPriceProduct);
        finish();
    }

    private void loadProductPrice(SQLiteDatabase db, MyOpenHelper dbHelper) {
        try {
            ArrayList<ValorProducto> valorProductoList = dbHelper.getValorProductos(db, tienda, producto);
            ListView lvValorProducto = findViewById(R.id.lvProductPrice);
            ArrayAdapter<ValorProducto> aValorProducto = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, valorProductoList);
            lvValorProducto.setAdapter(aValorProducto);
            if (valorProductoList.size() == 2) {
                flagLite = true;
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.product_without_price, Toast.LENGTH_SHORT).show();
            //Log.e("Error", "loadProductPrice: " + e.getMessage(), null);
        }
    }

    public void recordPrice(View view) {
        try {
            if (!flagLite) {
                if (validate()) {
                    ValorProducto valorProducto = new ValorProducto();
                    float valor = Float.parseFloat(etPrice.getText().toString());
                    valorProducto.setValor(valor);
                    //
                    try {
                        float valorEquivalente = Float.parseFloat(etEquivalentPrice.getText().toString());
                        valorProducto.setValorEquivalente(valorEquivalente);
                    } catch (Exception e) {
                        //Log.e("RecordPriceActivity", "recordPrice: ParseFloat valorEquivalente fail");
                        valorProducto.setValorEquivalente(0);
                    }
                    //
                    MyOpenHelper dbHelper = new MyOpenHelper(this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    TiendaProducto tiendaProducto = new TiendaProducto();
                    if (db != null) {
                        tiendaProducto = dbHelper.getTiendaProducto(db, tienda, producto);
                        valorProducto.setIdTiendaProducto(tiendaProducto);
                        long flagVP = dbHelper.insertValorProducto(db, valorProducto);
                        if (flagVP > 0) {
                            Toast.makeText(getBaseContext(), R.string.created, Toast.LENGTH_SHORT).show();
                            Intent iValorProducto = new Intent(this, RecordPriceActivity.class);
                            iValorProducto.putExtra("Store", tienda);
                            iValorProducto.putExtra("Product", producto);
                            startActivity(iValorProducto);
                        } else {
                            Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getBaseContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();
                }
            } else {
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
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
            //Log.e("RecordPriceActivity", "recordPrice: " + e.getMessage(), null);
        }
    }

    private boolean validate() {
        boolean flag = true;
        if (etPrice.getText().toString().isEmpty()) {
            tvPrice.setTextColor(Color.rgb(200, 0, 0));
            flag = false;
        } else {
            if (Integer.parseInt(etPrice.getText().toString()) > 0) {
                tvPrice.setTextColor(-1979711488);
            } else {
                tvPrice.setTextColor(Color.rgb(200, 0, 0));
                flag = false;
            }
        }
        return flag;
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
