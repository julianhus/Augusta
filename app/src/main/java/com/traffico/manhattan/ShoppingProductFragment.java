package com.traffico.manhattan;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Producto;
import com.traffico.manhattan.google.zxing.integration.android.IntentIntegrator;
import com.traffico.manhattan.google.zxing.integration.android.IntentResult;
import com.traffico.manhattan.interfaces.StringCreacion;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ShoppingProductFragment extends Fragment implements View.OnClickListener, StringCreacion {

    View view;
    private EditText etBarCode;
    private ImageButton bScann;
    private ImageButton ibBack;
    private ImageButton ibSafe;
    private ImageButton ibForward;
    private ImageButton ibSearch;
    private Producto producto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_shopping, container, false);
        etBarCode = view.findViewById(R.id.etBarCode);
        bScann = view.findViewById(R.id.iBScan);
        bScann.setOnClickListener((View.OnClickListener) this);
        ibBack = view.findViewById(R.id.ibBack);
        ibBack.setOnClickListener((View.OnClickListener) this);
        ibSafe = view.findViewById(R.id.ibSafe);
        ibSafe.setOnClickListener((View.OnClickListener) this);
        ibSafe.setEnabled(false);
        ibForward = view.findViewById(R.id.ibForward);
        ibForward.setOnClickListener((View.OnClickListener) this);
        ibForward.setEnabled(false);
        ibSearch = view.findViewById(R.id.ibSearch);
        ibSearch.setOnClickListener((View.OnClickListener) this);
        autocomplete();

        return view;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.iBScan) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(ShoppingProductFragment.this);
            scanIntegrator.initiateScan();
        }
        if (view.getId() == R.id.ibBack) {
            ((ShoppingActivity) getActivity()).loadFragment(new ShoppingProductPriceFragment());
        }
        if (view.getId() == R.id.ibSafe) {
            insertProduct();
        }
        if (view.getId() == R.id.ibForward) {
            Fragment shoppingRecordPriceFragment = new ShoppingRecordPriceFragment();
            Bundle arg = new Bundle();
            arg.putSerializable("Producto", producto);
            shoppingRecordPriceFragment.setArguments(arg);
            ((ShoppingActivity) getActivity()).loadFragment(shoppingRecordPriceFragment);
        }
        if (view.getId() == R.id.ibSearch) {
            loadProduct();
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
                    R.string.no_scan_data_received, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void loadProduct() {
        MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            producto = dbHelper.getProducto(db, etBarCode.getText().toString());
            if (producto.getId() != 0) {
                etBarCode.setEnabled(false);
                EditText ettrademark = view.findViewById(R.id.ettrademark);
                ettrademark.setText(producto.getMarca());
                ettrademark.setEnabled(false);
                EditText etDescription = view.findViewById(R.id.etProduct);
                etDescription.setText(producto.getDescripcion());
                etDescription.setEnabled(false);
                EditText etMeasure = view.findViewById(R.id.etMeasure);
                etMeasure.setText(producto.getMedida());
                etMeasure.setEnabled(false);
                EditText etWeight = view.findViewById(R.id.etWeight);
                etWeight.setText("" + producto.getValorMedida());
                etWeight.setEnabled(false);
                ibForward.setEnabled(true);
                ibSafe.setEnabled(false);
            } else {
                ibSafe.setEnabled(true);
                Toast toast = Toast.makeText(getApplicationContext(), R.string.product_no_found, Toast.LENGTH_SHORT);
                toast.show();

            }
        }
    }

    public void insertProduct() {
        loadProduct();
        Producto producto = new Producto();
        producto.setBarCode(etBarCode.getText().toString());
        EditText ettrademark = view.findViewById(R.id.ettrademark);
        producto.setMarca(ettrademark.getText().toString());
        EditText etDescripcion = view.findViewById(R.id.etProduct);
        producto.setDescripcion(etDescripcion.getText().toString());
        EditText etMeasure = view.findViewById(R.id.etMeasure);
        producto.setMedida(etMeasure.getText().toString());
        EditText etWeight = view.findViewById(R.id.etWeight);
        producto.setValorMedida(Float.parseFloat(etWeight.getText().toString()));
        MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            long flagInsert = dbHelper.insertProduct(db, producto);
            if (flagInsert > 0) {
                producto.setId((int) flagInsert);
                Toast.makeText(getApplicationContext(), R.string.created, Toast.LENGTH_SHORT).show();
                //
                Fragment shoppingRecordPriceFragment = new ShoppingRecordPriceFragment();
                Bundle arg = new Bundle();
                arg.putSerializable("Producto", producto);
                shoppingRecordPriceFragment.setArguments(arg);
                ((ShoppingActivity) getActivity()).loadFragment(shoppingRecordPriceFragment);
                //
            } else {
                Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void autocomplete() {
        // etMeasure
        ArrayAdapter<String> aMeasure = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_dropdown_item_1line, MEASURE){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.BLACK);

                // Generate ListView Item using TextView
                return view;
            }
        };
        AutoCompleteTextView etMeasure = (AutoCompleteTextView) view.findViewById(R.id.etMeasure);
        etMeasure.setAdapter(aMeasure);
        //
        MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
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
            ArrayAdapter<String> aTrademark = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, marcas){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    // Get the Item from ListView
                    View view = super.getView(position, convertView, parent);

                    // Initialize a TextView for ListView each Item
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);

                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.BLACK);

                    // Generate ListView Item using TextView
                    return view;
                }
            };
            AutoCompleteTextView ettrademark = (AutoCompleteTextView) view.findViewById(R.id.ettrademark);
            ettrademark.setAdapter(aTrademark);
            //
            String[] sProductos = new String[producto.size()];
            sProductos = producto.toArray(sProductos);
            ArrayAdapter<String> aProduct = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, sProductos){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    // Get the Item from ListView
                    View view = super.getView(position, convertView, parent);

                    // Initialize a TextView for ListView each Item
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);

                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.BLACK);

                    // Generate ListView Item using TextView
                    return view;
                }
            };
            AutoCompleteTextView etProduct = (AutoCompleteTextView) view.findViewById(R.id.etProduct);
            etProduct.setAdapter(aProduct);
            //
            String[] sBarcode = new String[barcode.size()];
            sBarcode = barcode.toArray(sBarcode);
            ArrayAdapter<String> abarcode = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, sBarcode){
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    // Get the Item from ListView
                    View view = super.getView(position, convertView, parent);

                    // Initialize a TextView for ListView each Item
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);

                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.BLACK);

                    // Generate ListView Item using TextView
                    return view;
                }
            };
            AutoCompleteTextView etBarCode = (AutoCompleteTextView) view.findViewById(R.id.etBarCode);
            etBarCode.setAdapter(abarcode);
        }
        //
    }
}
