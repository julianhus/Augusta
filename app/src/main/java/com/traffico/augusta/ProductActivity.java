package com.traffico.augusta;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Producto;
import com.traffico.augusta.google.zxing.integration.android.IntentIntegrator;
import com.traffico.augusta.google.zxing.integration.android.IntentResult;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton bScann;
    private EditText etBarCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setTitle(R.string.product);
        etBarCode = findViewById(R.id.etBarCode);
        bScann = findViewById(R.id.iBScan);
        bScann.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(ProductActivity.this, MenuActivity.class);
        startActivity(iMenu);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.iBScan) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(ProductActivity.this);
            scanIntegrator.initiateScan();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            etBarCode.setText(scanContent);
            loadProduct();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void loadProduct() {
        Button bProduct = findViewById(R.id.bProduct);
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            Producto producto = dbHelper.getProducto(db, etBarCode.getText().toString());
            if (producto.getId() != 0) {
                etBarCode.setEnabled(false);
                EditText ettrademark = findViewById(R.id.ettrademark);
                ettrademark.setText(producto.getMarca());
                ettrademark.setEnabled(false);
                EditText etDescription = findViewById(R.id.etProduct);
                etDescription.setText(producto.getDescripcion());
                etDescription.setEnabled(false);
                EditText etMeasure = findViewById(R.id.etMeasure);
                etMeasure.setText(producto.getMedida());
                etMeasure.setEnabled(false);
                EditText etWeight = findViewById(R.id.etWeight);
                etWeight.setText("" + producto.getValorMedida());
                etWeight.setEnabled(false);
                bProduct.setEnabled(false);
            } else {
                bProduct.setEnabled(true);
            }
        }
    }

    public void insertProduct(View view) {
        loadProduct();
        Button bProduct = findViewById(R.id.bProduct);
        if (bProduct.isEnabled()) {
            Producto producto = new Producto();
            producto.setBarCode(etBarCode.getText().toString());
            EditText ettrademark = findViewById(R.id.ettrademark);
            producto.setMarca(ettrademark.getText().toString());
            EditText etDescripcion = findViewById(R.id.etProduct);
            producto.setDescripcion(etDescripcion.getText().toString());
            EditText etMeasure = findViewById(R.id.etMeasure);
            producto.setMedida(etMeasure.getText().toString());
            EditText etWeight = findViewById(R.id.etWeight);
            producto.setValorMedida(Float.parseFloat(etWeight.getText().toString()));
            MyOpenHelper dbHelper = new MyOpenHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                long flagInsert = dbHelper.insertProduct(db, producto);
                if (flagInsert > 0) {
                    Toast.makeText(getBaseContext(), R.string.created, Toast.LENGTH_SHORT).show();
                    Intent iProduct = new Intent(this, ProductListActivity.class);
                    startActivity(iProduct);
                } else {
                    Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
