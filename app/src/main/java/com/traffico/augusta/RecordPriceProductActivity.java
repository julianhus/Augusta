package com.traffico.augusta;

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
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Producto;
import com.traffico.augusta.entidades.Tienda;

import java.util.ArrayList;

public class RecordPriceProductActivity extends AppCompatActivity {

    Tienda tienda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_price_product);
        getSupportActionBar().setTitle(R.string.record_price_product);
        Intent iUpdateStore = getIntent();
        tienda = (Tienda) iUpdateStore.getSerializableExtra("Store");
        TextView tvStore = findViewById(R.id.tvStore);
        tvStore.setText(tienda.toString());
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadProduct(db, dbHelper);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iProduct = new Intent(RecordPriceProductActivity.this, ProductActivity.class);
                startActivity(iProduct);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent iRecordPriceStore = new Intent(RecordPriceProductActivity.this, RecordPriceStoreActivity.class);
        startActivity(iRecordPriceStore);
    }

    private void loadProduct(SQLiteDatabase db, MyOpenHelper dbHelper) {
        try {
            ArrayList<Producto> productoList = dbHelper.getProductos(db);
            final ListView lvProduct = findViewById(R.id.lvProducts);
            ArrayAdapter<Producto> aProducto = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, productoList);
            lvProduct.setAdapter(aProducto);
            //
            lvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent iRecordPrice = new Intent(RecordPriceProductActivity.this, RecordPriceActivity.class);
                    Producto producto = (Producto) lvProduct.getItemAtPosition(position);
                    iRecordPrice.putExtra("Product",producto);
                    iRecordPrice.putExtra("Store",tienda);
                    startActivity(iRecordPrice );
                }
            });
            //
        } catch (Exception e) {
            Toast.makeText(getBaseContext(),R.string.empty_products, Toast.LENGTH_SHORT).show();
            Log.e("Error", "loadProduct: " + e.getMessage(), null);
        }
    }

}
