package com.traffico.manhattan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.manhattan.clases.CustomAdapterListViewShoppingRecordPrice;
import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Producto;
import com.traffico.manhattan.entidades.Tienda;
import com.traffico.manhattan.entidades.TiendaProducto;
import com.traffico.manhattan.entidades.ValorProducto;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ShoppingRecordPriceFragment extends Fragment {

    View view;
    View vAlertDialog = null;
    TextView tvProduct, tvPrice;
    EditText etPrice;
    Tienda tienda;
    Producto producto;
    ImageButton bRecordPrice;
    ImageButton ibBack;
    //
    LayoutInflater inflaterTemp;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflaterTemp = inflater;
        view = inflater.inflate(R.layout.fragment_shopping_record_price, container, false);
        //
        //vAlertDialog = inflater.inflate(R.layout.alert_dialog, null);
        //
        tvProduct = view.findViewById(R.id.tViewProduct);
        producto = new Producto();
        producto = getArguments() != null ? (Producto) getArguments().getSerializable("Producto") : producto;
        tvProduct.setText(producto.toDescripcion());
        tienda = ((ShoppingActivity) getActivity()).tienda;

        MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadProductPrice(db, dbHelper);
        }

        final float weight;
        weight = producto.getValorMedida();
        EditText etPrice = (EditText) view.findViewById(R.id.etPrice);
        //
        etPrice.setOnKeyListener(new EditText.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                EditText etPrice = (EditText) view.findViewById(R.id.etPrice);
                EditText etEquivalentPrice = (EditText) view.findViewById(R.id.etEquivalentPrice);
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
        bRecordPrice = view.findViewById(R.id.bRecordPrice);
        bRecordPrice.setBackgroundColor(Color.parseColor("#FF008577"));
        bRecordPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordPrice();
            }
        });
        ibBack = view.findViewById(R.id.ibBack);
        ibBack.setBackgroundColor(Color.parseColor("#FF008577"));
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ShoppingActivity) getActivity()).loadFragment(new ShoppingProductFragment());
            }
        });
        return view;
    }

    private void loadProductPrice(SQLiteDatabase db, MyOpenHelper dbHelper) {
        try {
            CustomAdapterListViewShoppingRecordPrice adapter;
            int imageEdit = R.drawable.ic_menu_send;

            ArrayList<ValorProducto> valorProductoList = dbHelper.getValorProductos(db, tienda, producto);
            final ListView lvValorProducto = view.findViewById(R.id.lvProductPrice);
            adapter = new CustomAdapterListViewShoppingRecordPrice(view.getContext(), valorProductoList, imageEdit);
            lvValorProducto.setAdapter(adapter);
            /*
            ArrayList<ValorProducto> valorProductoList = dbHelper.getValorProductos(db, tienda, producto);
            final ListView lvValorProducto = view.findViewById(R.id.lvProductPrice);
            ArrayAdapter<ValorProducto> aValorProducto = new ArrayAdapter<ValorProducto>(getApplicationContext(), android.R.layout.simple_list_item_1, valorProductoList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Get the Item from ListView
                    View view = super.getView(position, convertView, parent);
                    // Initialize a TextView for ListView each Item
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.BLACK);
                    return view;
                }
            };
            lvValorProducto.setAdapter(aValorProducto);*/
            lvValorProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (position == 0) {
                        //
                        vAlertDialog = inflaterTemp.inflate(R.layout.alert_dialog, null);
                        final ValorProducto valorProducto = (ValorProducto) lvValorProducto.getItemAtPosition(position);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(R.string.do_you_want);
                        //
                        //Insertar Validacion de Precios, imprimir el mensaje en tvCompare
                        String messageCompare = comparePrice(valorProducto);
                        TextView tvCompare = vAlertDialog.findViewById(R.id.tvCompare);
                        tvCompare.setText(messageCompare);
                        //
                        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText etTotal = (EditText) vAlertDialog.findViewById(R.id.etTotal);
                                aceptar(etTotal, valorProducto);

                            }
                        });
                        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancelar();
                            }
                        });
                        dialog.setView(vAlertDialog);
                        dialog.show();
                        //
                    }
                }
            });

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.product_without_price, Toast.LENGTH_SHORT).show();
            //Log.e("Error", "loadProductPrice: " + e.getMessage(), null);
        }
    }


    private String comparePrice(ValorProducto valorProductoSelected) {
        MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Producto producto = new Producto();
            producto = dbHelper.getProductoValorProducto(db, this.producto.getBarCode(), producto);
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
            if (valorProductolower.getValor() < valorProductoSelected.getValor()) {
                String message = valorProductolower.getIdTiendaProducto().getTienda().getDescripcion() + " " +
                        valorProductolower.getIdTiendaProducto().getTienda().getDireccion();
                return getString(R.string.this_product_can_be_cheaper, NumberFormat.getInstance().format(valorProductolower.getValor()), message);
            } else {
                return null;
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }
    }

    private void aceptar(EditText etTotal, ValorProducto valorProducto) {
        MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            long flagVP = dbHelper.insertMercadoProducto(db, tienda, valorProducto, etTotal);
            if (flagVP > 0) {
                Toast.makeText(getApplicationContext(), R.string.created, Toast.LENGTH_SHORT).show();
                //
                ((ShoppingActivity) getActivity()).loadFragment(new ShoppingProductPriceFragment());
                //
            } else {
                Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void recordPrice() {
        try {
            if (validate()) {
                EditText etPrice = (EditText) view.findViewById(R.id.etPrice);
                EditText etEquivalentPrice = (EditText) view.findViewById(R.id.etEquivalentPrice);
                ValorProducto valorProducto = new ValorProducto();
                float valor = Float.parseFloat(etPrice.getText().toString());
                valorProducto.setValor(valor);
                //
                float valorEquivalente = Float.parseFloat(etEquivalentPrice.getText().toString());
                valorProducto.setValorEquivalente(valorEquivalente);
                //
                MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                TiendaProducto tiendaProducto = new TiendaProducto();
                if (db != null) {
                    tiendaProducto = dbHelper.getTiendaProducto(db, tienda, producto);
                    valorProducto.setIdTiendaProducto(tiendaProducto);
                    long flagVP = dbHelper.insertValorProducto(db, valorProducto);
                    if (flagVP > 0) {
                        loadProductPrice(db, dbHelper);
                        etPrice.setText(String.valueOf(0));
                        etEquivalentPrice.setText(String.valueOf(0));
                        //
                        Toast.makeText(getApplicationContext(), R.string.created, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
            //Log.e("RecordPriceActivity", "recordPrice: " + e.getMessage(), null);
        }
    }

    private boolean validate() {
        etPrice = view.findViewById(R.id.etPrice);
        tvPrice = view.findViewById(R.id.tvPrice);
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
