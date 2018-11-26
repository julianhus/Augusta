package com.traffico.manhattan;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Producto;
import com.traffico.manhattan.interfaces.StringCreacion;

import java.util.ArrayList;

public class UpdateProductActivity extends AppCompatActivity implements StringCreacion {

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
        autocomplete();
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
                if (producto.getId() != 0 && producto.getBarCode() == this.producto.getBarCode()) {
                    Toast.makeText(getBaseContext(), R.string.already_assigned, Toast.LENGTH_SHORT).show();
                } else {
                    boolean flagCheck = validate(true);
                    if (!flagCheck) {
                        Toast.makeText(getBaseContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();
                    } else {
                        this.producto.setBarCode(etBarCode.getText().toString());
                        this.producto.setMarca(ettrademark.getText().toString());
                        this.producto.setDescripcion(etProduct.getText().toString());
                        this.producto.setMedida(etMeasure.getText().toString());
                        if (etWeight.getText().toString().isEmpty()) {
                            this.producto.setValorMedida(0);
                        } else {
                            this.producto.setValorMedida(Float.parseFloat(etWeight.getText().toString()));
                        }
                        if (db != null) {
                            int flagInsert = dbHelper.updateProducto(db, this.producto);
                            if (flagInsert > 0) {
                                Toast.makeText(getBaseContext(), R.string.updated, Toast.LENGTH_SHORT).show();
                                Intent iProductListActivity = new Intent(UpdateProductActivity.this, ProductListActivity.class);
                                startActivity(iProductListActivity);
                            } else {
                                Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            //Log.e("UpdateProductActivity", "updateProduct: ", null);
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
    private void autocomplete() {
        // etMeasure
        ArrayAdapter<String> aMeasure = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, MEASURE);
        AutoCompleteTextView etMeasure = (AutoCompleteTextView) findViewById(R.id.etMeasure);
        etMeasure.setAdapter(aMeasure);
        //
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            ArrayList<Producto> productos = dbHelper.getProductos(db);
            //
            ArrayList<String> barcode = new ArrayList<>();
            ArrayList<String> marca = new ArrayList<>();
            ArrayList<String> producto = new ArrayList<>();
            for(int i = 0; i < productos.size(); i++){
                barcode.add(productos.get(i).getBarCode());
                marca.add(productos.get(i).getMarca());
                producto.add(productos.get(i).getDescripcion());
            }
            //
            String[] marcas = new String[marca.size()];
            marcas = marca.toArray(marcas);
            ArrayAdapter<String> aTrademark = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, marcas);
            AutoCompleteTextView ettrademark = (AutoCompleteTextView) findViewById(R.id.ettrademark);
            ettrademark.setAdapter(aTrademark);
            //
            String[] sProductos = new String[producto.size()];
            sProductos = producto.toArray(sProductos);
            ArrayAdapter<String> aProduct = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, sProductos);
            AutoCompleteTextView etProduct = (AutoCompleteTextView) findViewById(R.id.etProduct);
            etProduct.setAdapter(aProduct);
            //
            String[] sBarcode = new String[barcode.size()];
            sBarcode = barcode.toArray(sBarcode);
            ArrayAdapter<String> abarcode = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, sBarcode);
            AutoCompleteTextView etBarCode = (AutoCompleteTextView) findViewById(R.id.etBarCode);
            etBarCode.setAdapter(abarcode);
        }
        //
    }
}
