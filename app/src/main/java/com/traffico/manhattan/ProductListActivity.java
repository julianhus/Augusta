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
import com.traffico.manhattan.entidades.Producto;

import java.util.ArrayList;

public class ProductListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        getSupportActionBar().setTitle(R.string.products);
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadProduct(db, dbHelper);
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddProduct);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iProduct = new Intent(ProductListActivity.this, ProductActivity.class);
                iProduct.putExtra("Llamada","ProductListActivity");
                startActivity(iProduct);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(ProductListActivity.this, MenuActivity.class);
        startActivity(iMenu);
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
                    Intent iUpdateProduct = new Intent(ProductListActivity.this, UpdateProductActivity.class);
                    Producto producto = (Producto) lvProduct.getItemAtPosition(position);
                    iUpdateProduct.putExtra("Product",producto);
                    startActivity(iUpdateProduct);

                }
            });
            //
        } catch (Exception e) {
            Toast.makeText(getBaseContext(),R.string.empty_products, Toast.LENGTH_SHORT).show();
            //Log.e("Error", "loadProduct: " + e.getMessage(), null);
        }
    }
}
