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
import com.traffico.manhattan.entidades.MercadoProducto;
import com.traffico.manhattan.entidades.Producto;
import com.traffico.manhattan.entidades.TiendaProducto;
import com.traffico.manhattan.entidades.ValorProducto;
import com.traffico.manhattan.google.zxing.integration.android.IntentIntegrator;
import com.traffico.manhattan.google.zxing.integration.android.IntentResult;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.facebook.FacebookSdk.getApplicationContext;


public class CompareProductsFragment extends Fragment {

    View view;
    private ImageButton bScann, ibSearch, ibSearchTrademark, ibSearchProduct;
    private EditText etBarCode, ettrademark, etProduct;
    private Producto producto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_compare_products, container, false);
        //
        producto = getArguments() != null ? (Producto) getArguments().getSerializable("Producto") : producto;
        if (producto != null) {
            loadInfoCompare(producto.getBarCode());
        }
        //
        etBarCode = view.findViewById(R.id.etBarCode);
        ettrademark = view.findViewById(R.id.ettrademark);
        etProduct = view.findViewById(R.id.etProduct);
        bScann = view.findViewById(R.id.iBScan);
        bScann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(CompareProductsFragment.this);
                scanIntegrator.initiateScan();
            }
        });
        //
        ibSearch = view.findViewById(R.id.ibSearch);
        ibSearch.setBackgroundColor(Color.parseColor("#FF008577"));
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etBarCode.getText().toString().isEmpty()) {
                    loadInfoCompare(etBarCode.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Codigo de Barras sin informacion", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //
        ibSearchTrademark = view.findViewById(R.id.ibSearchTrademark);
        ibSearchTrademark.setBackgroundColor(Color.parseColor("#FF008577"));
        ibSearchTrademark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProductForSelect(ettrademark.getText().toString(), 0);
            }
        });
        //
        ibSearchProduct = view.findViewById(R.id.ibSearchProduct);
        ibSearchProduct.setBackgroundColor(Color.parseColor("#FF008577"));
        ibSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProductForSelect(etProduct.getText().toString(), 1);
            }
        });
        //
        autocomplete();
        return view;
    }

    private void loadProductForSelect(String flagProduct, int i) {
        try {

            MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (db != null) {
                ArrayList<Producto> productoList = dbHelper.getProductos(db, flagProduct, i);
                if(productoList.size() > 0){
                    Fragment selectProductFragment = new SelectProductFragment();
                    Bundle arg = new Bundle();
                    arg.putSerializable("Llamada", "CompareProductsFragment");
                    arg.putSerializable("ProductoList", productoList);
                    selectProductFragment.setArguments(arg);
                    ((MenuActivity) getActivity()).loadFragment(selectProductFragment);
                }else{
                    Toast.makeText(getApplicationContext(), R.string.product_no_found, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT);
        }
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

    public void loadInfoCompare(String scanContent) {
        MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (db != null) {
                Producto producto = new Producto();
                producto = dbHelper.getProductoValorProducto(db, scanContent, producto);
                TextView tvProduct = view.findViewById(R.id.tvProduct);
                TextView tvHPrice = view.findViewById(R.id.tvHPrice);
                TextView tvHDate = view.findViewById(R.id.tvHDate);
                TextView tvHStore = view.findViewById(R.id.tvHStore);
                TextView tvHLastPurchase = view.findViewById(R.id.tvHLastPurchase);

                TextView tvLPrice = view.findViewById(R.id.tvLPrice);
                TextView tvLDate = view.findViewById(R.id.tvLDate);
                TextView tvLStore = view.findViewById(R.id.tvLStore);
                TextView tvLLastPurchase = view.findViewById(R.id.tvLLastPurchase);
                //
                tvProduct.setText("");
                tvHPrice.setText("");
                tvHDate.setText("");
                tvHStore.setText("");
                tvHLastPurchase.setText("");

                tvLPrice.setText("");
                tvLDate.setText("");
                tvLStore.setText("");
                tvLLastPurchase.setText("");
                //

                if (producto.getId() != 0) {
                    tvProduct.setText(producto.getDescripcion() + " " + producto.getMarca() + " " + producto.getValorMedida() + " " + producto.getMedida());
                    Iterator<TiendaProducto> iTiendaProducto = producto.getTiendaProductos().iterator();
                    ValorProducto valorProductoHigher = new ValorProducto();
                    ValorProducto valorProductolower = null;
                    while (iTiendaProducto.hasNext()) {
                        TiendaProducto tiendaProducto = iTiendaProducto.next();
                        Iterator<ValorProducto> iValorProducto = tiendaProducto.getValorProductos().iterator();
                        while (iValorProducto.hasNext()) {
                            ValorProducto valorProducto = iValorProducto.next();
                            if (valorProducto.getValor() > valorProductoHigher.getValor()) {
                                valorProductolower = valorProductoHigher;
                                valorProductoHigher = valorProducto;
                            }
                            if (valorProducto.getValor() < valorProductoHigher.getValor()) {
                                valorProductolower = valorProducto;
                            }
                        }
                    }
                    //
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                    String dateTimeH, dateTimeL;
                    //
                    try {
                        if (valorProductoHigher.getValor() > 0) {
                            tvHPrice.setText("$" + String.valueOf(NumberFormat.getInstance().format(valorProductoHigher.getValor())));
                        }
                        dateTimeH = dateFormat.format(valorProductoHigher.getFechaRegistro());
                        tvHDate.setText(dateTimeH);
                        //
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Date dateH = new Date();
                    boolean flagDate = false;
                    try {
                        Iterator<MercadoProducto> iMercadoProducto = valorProductoHigher.getMercadoProductos().iterator();
                        while (iMercadoProducto.hasNext()) {
                            MercadoProducto tMercadoProducto = iMercadoProducto.next();
                            if (tMercadoProducto.getMercado().getId() != 0) {
                                //if (dateH.before(tMercadoProducto.getMercado().getFechaRegistro())) {
                                dateH = tMercadoProducto.getMercado().getFechaRegistro();
                                flagDate = true;
                                //}
                            }
                        }
                        //
                        if (flagDate == true) {
                            dateTimeH = dateFormat.format(dateH);
                            tvHLastPurchase.setText(dateTimeH);
                        }
                        //
                        tvHStore.setText(String.valueOf(valorProductoHigher.getIdTiendaProducto().getTienda().getDescripcion() + " " + valorProductoHigher.getIdTiendaProducto().getTienda().getDireccion()));
                        //
                        if (valorProductolower.getId() > 0) {
                            tvLPrice.setText("$" + String.valueOf(NumberFormat.getInstance().format(valorProductolower.getValor())));
                            dateTimeL = dateFormat.format(valorProductolower.getFechaRegistro());
                            tvLDate.setText(dateTimeL);
                            Date dateL = new Date();

                            flagDate = false;
                            iMercadoProducto = valorProductolower.getMercadoProductos().iterator();
                            while (iMercadoProducto.hasNext()) {
                                MercadoProducto tMercadoProducto = iMercadoProducto.next();
                                if (tMercadoProducto.getMercado().getId() != 0) {
                                    //if (dateH.before(tMercadoProducto.getMercado().getFechaRegistro())) {
                                    dateH = tMercadoProducto.getMercado().getFechaRegistro();
                                    flagDate = true;
                                    //}
                                }
                            }
                            //
                            if (flagDate == true) {
                                dateTimeH = dateFormat.format(dateH);
                                tvLLastPurchase.setText(dateTimeH);
                            }
                            //

                            tvLStore.setText(String.valueOf(valorProductolower.getIdTiendaProducto().getTienda().getDescripcion() + " " + valorProductolower.getIdTiendaProducto().getTienda().getDireccion()));
                            //
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        etBarCode.setText("");
                        ettrademark.setText("");
                        etProduct.setText("");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.product_no_found, Toast.LENGTH_SHORT).show();
                    tvProduct.setText(R.string.product);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Log.e("CompareProductsFragment", "loadInfoCompare: " + e);
        }
    }

    private void autocomplete() {
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
            ArrayAdapter<String> aTrademark = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, marcas);
            AutoCompleteTextView ettrademark = (AutoCompleteTextView) view.findViewById(R.id.ettrademark);
            ettrademark.setAdapter(aTrademark);
            //
            String[] sProductos = new String[producto.size()];
            sProductos = producto.toArray(sProductos);
            ArrayAdapter<String> aProduct = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, sProductos);
            AutoCompleteTextView etProduct = (AutoCompleteTextView) view.findViewById(R.id.etProduct);
            etProduct.setAdapter(aProduct);
            //
            String[] sBarcode = new String[barcode.size()];
            sBarcode = barcode.toArray(sBarcode);
            ArrayAdapter<String> abarcode = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, sBarcode);
            AutoCompleteTextView etBarCode = (AutoCompleteTextView) view.findViewById(R.id.etBarCode);
            etBarCode.setAdapter(abarcode);
        }
        //
    }

}
