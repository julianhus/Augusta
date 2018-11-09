package com.traffico.augusta;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Producto;
import com.traffico.augusta.google.zxing.integration.android.IntentIntegrator;
import com.traffico.augusta.google.zxing.integration.android.IntentResult;

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
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
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
        }
    }

}
