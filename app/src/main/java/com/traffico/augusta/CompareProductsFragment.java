package com.traffico.augusta;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Producto;
import com.traffico.augusta.entidades.TiendaProducto;
import com.traffico.augusta.entidades.ValorProducto;
import com.traffico.augusta.google.zxing.integration.android.IntentIntegrator;
import com.traffico.augusta.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Iterator;

import static com.facebook.FacebookSdk.getApplicationContext;


public class CompareProductsFragment extends Fragment {

    View view;
    private ImageButton bScann;
    private ImageButton ibSearch;
    private EditText etBarCode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_compare_products, container, false);
        //
        bScann = view.findViewById(R.id.iBScan);
        etBarCode = view.findViewById(R.id.etBarCode);
        bScann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(CompareProductsFragment.this);
                scanIntegrator.initiateScan();
            }
        });
        //
        ibSearch = view.findViewById(R.id.ibSearch);
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadInfoCompare(etBarCode.getText().toString());
            }
        });
        //
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            etBarCode.setText(scanContent);
            loadInfoCompare(scanContent);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    private void loadInfoCompare(String scanContent) {
        Button bProduct = view.findViewById(R.id.bProduct);
        MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            Producto producto = new Producto();
            producto = dbHelper.getProductoValorProducto(db, scanContent, producto);
            TextView tvProduct = view.findViewById(R.id.tvProduct);
            TextView tvHPrice = view.findViewById(R.id.tvHPrice);
            TextView tvHDate = view.findViewById(R.id.tvHDate);
            TextView tvHStore = view.findViewById(R.id.tvHStore);
            TextView tvHLastPurshase = view.findViewById(R.id.tvHLastPurshase);
            /*TextView tvProduct = view.findViewById(R.id.tvProduct);
            TextView tvProduct = view.findViewById(R.id.tvProduct);
            TextView tvProduct = view.findViewById(R.id.tvProduct);
            TextView tvProduct = view.findViewById(R.id.tvProduct);*/

            if (producto.getId() != 0) {
                tvProduct.setText(producto.getDescripcion() + " " + producto.getMarca() + " " + producto.getValorMedida() + " " + producto.getMedida());

                Iterator<TiendaProducto> iTiendaProducto = producto.getTiendaProductos().iterator();
                ValorProducto valorProductoHigher = new ValorProducto();
                ValorProducto valorProductolower;
                while (iTiendaProducto.hasNext()){
                    TiendaProducto tiendaProducto = iTiendaProducto.next();
                    Iterator<ValorProducto> iValorProducto = tiendaProducto.getValorProductos().iterator();
                    while (iValorProducto.hasNext()){
                        ValorProducto valorProducto = iValorProducto.next();
                        if(valorProducto.getValor() > valorProductoHigher.getValor()){
                            valorProductolower = valorProductoHigher;
                            valorProductoHigher = valorProducto;
                        }
                        if(valorProducto.getValor() < valorProductoHigher.getValor()){
                            valorProductolower = valorProducto;
                        }
                    }
                }
                tvHPrice.setText(String.valueOf(valorProductoHigher.getValor()));
                tvHDate.setText(String.valueOf(valorProductoHigher.getFechaRegistro()));
                tvHStore.setText(String.valueOf(valorProductoHigher.getIdTiendaProducto().getTienda().toString()));


            }else{
                Toast toast = Toast.makeText(getApplicationContext(), R.string.product_no_found, Toast.LENGTH_SHORT);
                toast.show();
                tvProduct.setText(R.string.product);
            }
        }
    }

}
