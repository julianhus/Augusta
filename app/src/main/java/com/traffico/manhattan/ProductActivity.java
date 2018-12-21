package com.traffico.manhattan;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Producto;
import com.traffico.manhattan.entidades.Tienda;
import com.traffico.manhattan.google.zxing.integration.android.IntentIntegrator;
import com.traffico.manhattan.google.zxing.integration.android.IntentResult;
import com.traffico.manhattan.interfaces.StringCreacion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener, StringCreacion {

    private ImageButton bScann, ibSearch, ibClean;
    private Button bProduct;
    private EditText etBarCode, ettrademark, etDescripcion, etMeasure, etWeight;
    private TextView tvBarCode, tvTrademark, tvProduct;
    String llamada;
    Tienda tienda;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        getSupportActionBar().setTitle(R.string.product);
        //
        Intent iProductActivity = getIntent();
        llamada = (String) iProductActivity.getSerializableExtra("Llamada");
        tienda = (Tienda) iProductActivity.getSerializableExtra("Store");
        //
        tvBarCode = findViewById(R.id.tvBarCode);
        tvTrademark = findViewById(R.id.tvTrademark);
        tvProduct = findViewById(R.id.tViewProduct);

        etBarCode = findViewById(R.id.etBarCode);
        ettrademark = findViewById(R.id.ettrademark);
        etDescripcion = findViewById(R.id.etProduct);
        etMeasure = findViewById(R.id.etMeasure);
        etWeight = findViewById(R.id.etWeight);
        //
        etBarCode = findViewById(R.id.etBarCode);
        bScann = findViewById(R.id.iBScan);
        bScann.setOnClickListener((View.OnClickListener) this);
        ibSearch = findViewById(R.id.ibSearch);
        ibSearch.setBackgroundColor(Color.parseColor("#FF008577"));
        ibSearch.setOnClickListener((View.OnClickListener) this);
        bProduct = findViewById(R.id.bProduct);
        ibClean = findViewById(R.id.ibClean);
        ibClean.setBackgroundColor(Color.parseColor("#FF008577"));
        ibClean.setOnClickListener((View.OnClickListener) this);
        autocomplete();
        //
        etBarCode.setInputType(InputType.TYPE_NULL);
        etBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etBarCode.setInputType(InputType.TYPE_CLASS_NUMBER);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(ProductActivity.this, MenuActivity.class);
        startActivity(iMenu);
        finish();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.iBScan) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(ProductActivity.this);
            scanIntegrator.initiateScan();
        }
        if (view.getId() == R.id.ibSearch) {
            loadProduct();
        }
        if (view.getId() == R.id.ibClean) {
            clean();
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
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            Producto producto = dbHelper.getProducto(db, etBarCode.getText().toString());
            if (producto.getId() != 0) {
                etBarCode.setEnabled(false);
                ettrademark.setText(producto.getMarca());
                ettrademark.setEnabled(false);
                etDescripcion.setText(producto.getDescripcion());
                etDescripcion.setEnabled(false);
                etMeasure.setText(producto.getMedida());
                etMeasure.setEnabled(false);
                etWeight.setText("" + producto.getValorMedida());
                etWeight.setEnabled(false);
                bProduct.setEnabled(false);
                bScann.setEnabled(false);
                validate();
            } else {
                bProduct.setEnabled(true);
                Toast.makeText(getBaseContext(), R.string.product_no_found, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void insertProduct(View view) {
        if (validate()) {
            loadProduct();
            Button bProduct = findViewById(R.id.bProduct);
            if (bProduct.isEnabled()) {
                Producto producto = new Producto();
                producto.setBarCode(etBarCode.getText().toString());
                producto.setMarca(ettrademark.getText().toString());
                producto.setDescripcion(etDescripcion.getText().toString());
                producto.setMedida(etMeasure.getText().toString());
                try {
                    producto.setValorMedida(Float.parseFloat(etWeight.getText().toString()));
                } catch (Exception e) {
                    //Log.i("ProductActivity", "insertProduct: parceFloat Fail");
                }
                MyOpenHelper dbHelper = new MyOpenHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db != null) {
                    long flagInsert = dbHelper.insertProduct(db, producto);
                    if (flagInsert > 0) {
                        Toast.makeText(getBaseContext(), R.string.created, Toast.LENGTH_SHORT).show();
                        //
                        Intent iProduct = new Intent();
                        if (llamada.equals("ProductListActivity")) {
                            iProduct = new Intent(this, ProductListActivity.class);
                            startActivity(iProduct);
                        }
                        if (llamada.equals("RecordPriceProductActivity")) {
                            iProduct = new Intent(this, RecordPriceProductActivity.class);
                            iProduct.putExtra("Store", tienda);
                            startActivity(iProduct);
                        }
                        //

                    } else {
                        Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            Toast.makeText(getBaseContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        boolean flagBarCode, flagTradeMark, flagProduct = true;
        if (etBarCode.getText().toString().isEmpty()) {
            tvBarCode.setTextColor(Color.rgb(200, 0, 0));
            flagBarCode = false;
        } else {
            tvBarCode.setTextColor(-1979711488);
            flagBarCode = true;
        }
        if (ettrademark.getText().toString().isEmpty()) {
            tvTrademark.setTextColor(Color.rgb(200, 0, 0));
            flagTradeMark = false;
        } else {
            tvTrademark.setTextColor(-1979711488);
            flagTradeMark = true;
        }

        if (etDescripcion.getText().toString().isEmpty()) {
            tvProduct.setTextColor(Color.rgb(200, 0, 0));
            flagProduct = false;
        } else {
            tvProduct.setTextColor(-1979711488);
            flagProduct = true;
        }

        if (!flagBarCode || !flagTradeMark || !flagProduct) {
            return false;
        } else {
            return true;
        }
    }

    private void autocomplete() {
        // etMeasure
        ArrayAdapter<String> aMeasure = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, MEASURE);
        AutoCompleteTextView etMeasure = (AutoCompleteTextView) findViewById(R.id.etMeasure);
        etMeasure.setAdapter(aMeasure);
        //
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            ArrayList<Producto> productos = dbHelper.getProductos(db);
            //
            Set<String> barcode = new HashSet<>();
            Set<String> marca = new HashSet<>();
            Set<String> producto = new HashSet<>();
            for (int i = 0; i < productos.size(); i++) {
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
    }

    private void clean() {
        if (!bScann.isEnabled()) {
            bScann.setEnabled(true);
            etBarCode.setEnabled(true);
            tvBarCode.setTextColor(-1979711488);
            ettrademark.setEnabled(true);
            tvTrademark.setTextColor(-1979711488);
            etDescripcion.setEnabled(true);
            tvProduct.setTextColor(-1979711488);
            etMeasure.setEnabled(true);
            etWeight.setEnabled(true);
            bProduct.setEnabled(true);
            etBarCode.setText("");
            ettrademark.setText("");
            etDescripcion.setText("");
            etMeasure.setText("");
            etWeight.setText("");
        }
    }

}
