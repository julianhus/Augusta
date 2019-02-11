package com.traffico.manhattan;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Producto;
import com.traffico.manhattan.entidades.Tienda;
import com.traffico.manhattan.entidades.TiendaProducto;
import com.traffico.manhattan.entidades.ValorProducto;

import java.util.ArrayList;

public class RecordPriceActivity extends AppCompatActivity {

    Tienda tienda;
    Producto producto;
    EditText etPrice, etEquivalentPrice;
    TextView tvPrice, tvEquivalentPrice;

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

        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadProductPrice(db, dbHelper);
        }

        final float weight;
        weight = producto.getValorMedida();
        etPrice = (EditText) findViewById(R.id.etPrice);
        //
        etPrice.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText etPrice = (EditText) findViewById(R.id.etPrice);
                EditText etEquivalentPrice = (EditText) findViewById(R.id.etEquivalentPrice);
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
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.product_without_price, Toast.LENGTH_SHORT).show();
            //Log.e("Error", "loadProductPrice: " + e.getMessage(), null);
        }
    }

    public void recordPrice(View view) {
        try {
            if (validate()) {
                ValorProducto valorProducto = new ValorProducto();
                float valor = Float.parseFloat(etPrice.getText().toString());
                valorProducto.setValor(valor);
                //
                try {
                    float valorEquivalente = Float.parseFloat(etEquivalentPrice.getText().toString());
                    valorProducto.setValorEquivalente(valorEquivalente);
                }catch (Exception e){
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
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
            //Log.e("RecordPriceActivity", "recordPrice: " + e.getMessage(), null);
        }
    }

    private boolean validate() {
        etPrice = findViewById(R.id.etPrice);
        tvPrice = findViewById(R.id.tvPrice);
        etEquivalentPrice = findViewById(R.id.etEquivalentPrice);
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
}
