package com.traffico.augusta;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Tienda;
import com.traffico.augusta.entidades.Usuario;

public class ShoppingActivity extends AppCompatActivity {

    Tienda tienda;
    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        getSupportActionBar().setTitle(R.string.shopping);
        Intent iShopping = getIntent();
        tienda = (Tienda) iShopping.getSerializableExtra("Store");
        TextView tvStore = findViewById(R.id.tvStore);
        tvStore.setText(tienda.toString());
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            usuario = dbHelper.getUsuario(db);
        }
        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
