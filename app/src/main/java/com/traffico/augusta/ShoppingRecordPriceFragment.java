package com.traffico.augusta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Producto;
import com.traffico.augusta.entidades.Tienda;
import com.traffico.augusta.entidades.TiendaProducto;
import com.traffico.augusta.entidades.ValorProducto;

import java.util.ArrayList;

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
        tvProduct = view.findViewById(R.id.tvProduct);
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
                    Log.e("RecordPriceActivity", "onKey: " + e.getMessage(), null);
                }
                return false;
            }
        });
        bRecordPrice = view.findViewById(R.id.bRecordPrice);
        bRecordPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordPrice();
            }
        });
        ibBack = view.findViewById(R.id.ibBack);
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
            ArrayList<ValorProducto> valorProductoList = dbHelper.getValorProductos(db, tienda, producto);
            final ListView lvValorProducto = view.findViewById(R.id.lvProductPrice);
            ArrayAdapter<ValorProducto> aValorProducto = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, valorProductoList);
            lvValorProducto.setAdapter(aValorProducto);
            lvValorProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (position == 0) {
                        //
                        vAlertDialog = inflaterTemp.inflate(R.layout.alert_dialog, null);
                        final ValorProducto valorProducto = (ValorProducto) lvValorProducto.getItemAtPosition(position);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(R.string.do_you_want);
                        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText etTotal = (EditText) vAlertDialog.findViewById(R.id.etTotal);
                                //
                                //Insertar Validacion de Precios antes de confirmar
                                //
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
            Log.e("Error", "loadProductPrice: " + e.getMessage(), null);
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
            }else{Toast.makeText(getApplicationContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();}
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
            Log.e("RecordPriceActivity", "recordPrice: " + e.getMessage(), null);
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
