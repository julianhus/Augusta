package com.traffico.augusta;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Producto;
import com.traffico.augusta.entidades.Tienda;

public class UpdateProductActivity extends AppCompatActivity {

    Producto producto;
    EditText etBarCode;
    EditText ettrademark;
    EditText etProduct;
    EditText etMeasure;
    EditText etWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        getSupportActionBar().setTitle(R.string.update_product);
        Intent iUpdateProduct = getIntent();
        producto = (Producto) iUpdateProduct.getSerializableExtra("Product");
        etBarCode = findViewById(R.id.etBarCode);
        etBarCode.setText(producto.getBarCode());
        ettrademark = findViewById(R.id.ettrademark);
        ettrademark.setText(producto.getMarca());
        etProduct = findViewById(R.id.etProduct);
        etProduct.setText(producto.getDescripcion());
        etMeasure = findViewById(R.id.etMeasure);
        etMeasure.setText(producto.getMedida());
        etWeight = findViewById(R.id.etWeight);
        etWeight.setText(producto.getValorMedida() + "");
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(this, MenuActivity.class);
        startActivity(iMenu);
    }

    public void updateProduct(View view) {
        try {
            MyOpenHelper dbHelper = new MyOpenHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                Producto producto = dbHelper.getProducto(db, etBarCode.getText().toString());
                if (producto.getId() != 0 && producto.getId() != this.producto.getId()) {
                    Toast.makeText(getBaseContext(), R.string.already_assigned, Toast.LENGTH_SHORT).show();
                } else {
                    boolean flagCheck = validate(true);
                    if (!flagCheck) {
                        Toast.makeText(getBaseContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();
                    } else {
                        producto.setBarCode(etBarCode.getText().toString());
                        producto.setMarca(ettrademark.getText().toString());
                        producto.setDescripcion(etProduct.getText().toString());
                        producto.setMedida(etMeasure.getText().toString());
                        if (etWeight.getText().toString().isEmpty()) {
                            producto.setValorMedida(0);
                        } else {
                            producto.setValorMedida(Float.parseFloat(etWeight.getText().toString()));
                        }
                        if (db != null) {
                            int flagInsert = dbHelper.updateProducto(db, producto);
                            Toast.makeText(getBaseContext(), R.string.update, Toast.LENGTH_SHORT).show();
                            Intent iProductListActivity = new Intent(UpdateProductActivity.this, ProductListActivity.class);
                            startActivity(iProductListActivity);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("UpdateProductActivity", "updateProduct: ", null);
        }
    }

    private boolean validate(boolean flagCheck) {
        TextView tvBarCode = findViewById(R.id.tvBarCode);
        TextView tvTrademark = findViewById(R.id.tvTrademark);
        TextView tvProduct = findViewById(R.id.tvProduct);
        if (etBarCode.getText().toString().isEmpty()) {
            tvBarCode.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvBarCode.setTextColor(-1979711488);
        }
        if (ettrademark.getText().toString().isEmpty()) {
            tvTrademark.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvTrademark.setTextColor(-1979711488);
        }
        if (etProduct.getText().toString().isEmpty()) {
            tvProduct.setTextColor(Color.rgb(200, 0, 0));
            flagCheck = false;
        } else {
            tvProduct.setTextColor(-1979711488);
        }
        return flagCheck;
    }
}
