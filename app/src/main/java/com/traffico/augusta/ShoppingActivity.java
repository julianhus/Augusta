package com.traffico.augusta;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Tienda;
import com.traffico.augusta.entidades.Usuario;
import com.traffico.augusta.google.zxing.integration.android.IntentIntegrator;
import com.traffico.augusta.google.zxing.integration.android.IntentResult;

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
                dialogOptions();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(this, MenuActivity.class);
        startActivity(iMenu);
    }

    private void dialogOptions() {
        AlertDialog.Builder optionsforCodebar = new AlertDialog.Builder(ShoppingActivity.this);
        optionsforCodebar.setTitle(R.string.do_you_want);
        optionsforCodebar.setMessage(R.string.choose_how);
        optionsforCodebar.setPositiveButton(R.string.scanner, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ScannCodeBar();
            }
        });
        optionsforCodebar.setNegativeButton(R.string.type, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        optionsforCodebar.show();

    }

    private void ScannCodeBar() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(ShoppingActivity.this);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            Toast.makeText(getApplicationContext(), "" + scanContent, Toast.LENGTH_SHORT).show();
            //etBarCode.setText(scanContent);
            //loadProduct();
        } else {
            Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT).show();
        }

    }
}
