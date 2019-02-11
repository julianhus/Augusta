package com.traffico.manhattan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ShoppingProductFragment extends Fragment implements View.OnClickListener, StringCreacion {

    View view;
    private EditText etBarCode, ettrademark, etDescription, etMeasure, etWeight;
    private TextView tvBarCode, tvTrademark, tvProduct;
    private ImageButton bScann, ibClean, ibBack, ibSafe, ibForward, ibSearch, ibSearchTrademark, ibSearchProduct;
    private Producto producto;
    boolean flagLite = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_shopping, container, false);
        //
        producto = getArguments() != null ? (Producto) getArguments().getSerializable("Producto") : producto;
        //
        etBarCode = view.findViewById(R.id.etBarCode);
        ettrademark = view.findViewById(R.id.ettrademark);
        etDescription = view.findViewById(R.id.etProduct);
        etMeasure = view.findViewById(R.id.etMeasure);
        etWeight = view.findViewById(R.id.etWeight);
        //
        tvBarCode = view.findViewById(R.id.tvBarCode);
        tvTrademark = view.findViewById(R.id.tvTrademark);
        tvProduct = view.findViewById(R.id.tViewProduct);
        //
        bScann = view.findViewById(R.id.iBScan);
        bScann.setOnClickListener((View.OnClickListener) this);
        ibClean = view.findViewById(R.id.ibClean);
        ibClean.setBackgroundColor(Color.parseColor("#FF008577"));
        //
        ibClean.setOnClickListener((View.OnClickListener) this);
        ibBack = view.findViewById(R.id.ibBack);
        ibBack.setBackgroundColor(Color.parseColor("#FF008577"));
        ibBack.setOnClickListener((View.OnClickListener) this);
        ibSafe = view.findViewById(R.id.ibSafe);
        ibSafe.setOnClickListener((View.OnClickListener) this);
        ibSafe.setEnabled(false);
        ibSafe.setBackgroundColor(Color.parseColor("#E0E0E0"));
        ibForward = view.findViewById(R.id.ibForward);
        ibForward.setOnClickListener((View.OnClickListener) this);
        ibForward.setEnabled(false);
        ibForward.setBackgroundColor(Color.parseColor("#E0E0E0"));
        ibSearch = view.findViewById(R.id.ibSearch);
        ibSearch.setBackgroundColor(Color.parseColor("#FF008577"));
        ibSearch.setOnClickListener((View.OnClickListener) this);
        ibSearchTrademark = view.findViewById(R.id.ibSearchTrademark);
        ibSearchTrademark.setBackgroundColor(Color.parseColor("#FF008577"));
        ibSearchTrademark.setOnClickListener((View.OnClickListener) this);
        ibSearchProduct = view.findViewById(R.id.ibSearchProduct);
        ibSearchProduct.setBackgroundColor(Color.parseColor("#FF008577"));
        ibSearchProduct.setOnClickListener((View.OnClickListener) this);
        autocomplete();
        //
        if (producto != null) {
            etBarCode.setText(producto.getBarCode());
            loadProduct();
        }
        //
        return view;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.iBScan) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(ShoppingProductFragment.this);
            scanIntegrator.initiateScan();
        }
        if (view.getId() == R.id.ibClean) {
            clean();
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
        if (view.getId() == R.id.ibSearchTrademark) {
            loadProductForSelect(ettrademark.getText().toString(), 0);
        }
        if (view.getId() == R.id.ibSearchProduct) {
            loadProductForSelect(etDescription.getText().toString(), 1);
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
        try {
            MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                //
                ArrayList<Producto> productoList = dbHelper.getProductos(db);
                if (productoList.size() == 7) {
                    flagLite = true;
                }
                //
                if (!etBarCode.getText().toString().isEmpty()) {
                    producto = dbHelper.getProducto(db, etBarCode.getText().toString());
                    if (producto.getId() > 0) {
                        etBarCode.setEnabled(false);
                        ettrademark = view.findViewById(R.id.ettrademark);
                        ettrademark.setText(producto.getMarca());
                        ettrademark.setEnabled(false);
                        etDescription = view.findViewById(R.id.etProduct);
                        etDescription.setText(producto.getDescripcion());
                        etDescription.setEnabled(false);
                        etMeasure = view.findViewById(R.id.etMeasure);
                        etMeasure.setText(producto.getMedida());
                        etMeasure.setEnabled(false);
                        etWeight = view.findViewById(R.id.etWeight);
                        etWeight.setText("" + producto.getValorMedida());
                        etWeight.setEnabled(false);
                        ibForward.setEnabled(true);
                        ibForward.setBackgroundColor(Color.parseColor("#FF008577"));
                        ibSafe.setEnabled(false);
                        ibSafe.setBackgroundColor(Color.parseColor("#E0E0E0"));
                    } else {
                        ibSafe.setEnabled(true);
                        ibSafe.setBackgroundColor(Color.parseColor("#FF008577"));
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.product_no_found, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Codigo de Barras sin informacion", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT);
        }

    }

    private void loadProductForSelect(String flagProduct, int i) {
        try {
            MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                ArrayList<Producto> productoList = dbHelper.getProductos(db, flagProduct, i);
                if (productoList.size() > 0) {
                    Fragment selectProductFragment = new SelectProductFragment();
                    Bundle arg = new Bundle();
                    arg.putSerializable("Llamada", "ShoppingProductFragment");
                    arg.putSerializable("ProductoList", productoList);
                    selectProductFragment.setArguments(arg);
                    ((ShoppingActivity) getActivity()).loadFragment(selectProductFragment);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.product_no_found, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT);
        }
    }


    public void insertProduct() {
        if (validate()) {
            loadProduct();
            if (ibSafe.isEnabled()) {
                if (!flagLite) {
                    Producto producto = new Producto();
                    producto.setBarCode(etBarCode.getText().toString());
                    EditText ettrademark = view.findViewById(R.id.ettrademark);
                    producto.setMarca(ettrademark.getText().toString());
                    EditText etDescripcion = view.findViewById(R.id.etProduct);
                    producto.setDescripcion(etDescripcion.getText().toString());
                    EditText etMeasure = view.findViewById(R.id.etMeasure);
                    producto.setMedida(etMeasure.getText().toString());
                    EditText etWeight = view.findViewById(R.id.etWeight);
                    try {
                        producto.setValorMedida(Float.parseFloat(etWeight.getText().toString()));
                    } catch (Exception e) {
                        //Log.i("ProductActivity", "insertProduct: parceFloat Fail");
                    }
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
                }else{
                    android.app.AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle(R.string.component_not_available);

                    dialog.setMessage(R.string.the_full_version_has);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            externalEstorageLite();
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.traffico.mercabarato");
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);
                        }
                    });
                    dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.show();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();
        }

    }

    private void autocomplete() {
        // etMeasure
        ArrayAdapter<String> aMeasure = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, MEASURE) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
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
            ArrayAdapter<String> aTrademark = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, marcas) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
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
            ArrayAdapter<String> aProduct = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, sProductos) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
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
            ArrayAdapter<String> abarcode = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, sBarcode) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
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

    private boolean validate() {
        tvBarCode = view.findViewById(R.id.tvBarCode);
        tvTrademark = view.findViewById(R.id.tvTrademark);
        tvProduct = view.findViewById(R.id.tViewProduct);
        //
        etBarCode = view.findViewById(R.id.etBarCode);
        ettrademark = view.findViewById(R.id.ettrademark);
        etDescription = view.findViewById(R.id.etProduct);
        //
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

        if (etDescription.getText().toString().isEmpty()) {
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

    private void clean() {
        if (ibForward.isEnabled()) {
            ibForward.setEnabled(false);
            ibForward.setBackgroundColor(Color.parseColor("#E0E0E0"));
            etBarCode.setEnabled(true);
            tvBarCode.setTextColor(-1979711488);
            ettrademark.setEnabled(true);
            tvTrademark.setTextColor(-1979711488);
            etDescription.setEnabled(true);
            tvProduct.setTextColor(-1979711488);
            etMeasure.setEnabled(true);
            etWeight.setEnabled(true);
            etBarCode.setText("");
            ettrademark.setText("");
            etDescription.setText("");
            etMeasure.setText("");
            etWeight.setText("");
        }
    }

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    private void externalEstorageLite() {
        try {

            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck < 0) {
                ActivityCompat.requestPermissions(((MenuActivity) getActivity()), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                File origen = new File("/data/data/com.traffico.mercabaratolite/databases/manhattan.sqlite");
                File destino = new File("/sdcard/manhattan.sqlite");
                FileChannel inChannel = new FileInputStream(origen).getChannel();
                FileChannel outChannel = new FileOutputStream(destino).getChannel();
                try
                {
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                }
                finally
                {
                    if (inChannel != null)
                        inChannel.close();
                    if (outChannel != null)
                        outChannel.close();
                }
            }

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(((MenuActivity) getActivity()), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(((MenuActivity) getActivity()), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity", "onCreate: ", e.getCause());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), R.string.we_will_not_be, Toast.LENGTH_LONG).show();
                }
                return;

            }
        }
    }
}
