package com.traffico.augusta;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.MercadoProducto;

import java.util.ArrayList;
import java.util.Iterator;


public class ShoppingProductPriceFragment extends Fragment {

    View view;
    View vAlertDialog;
    LayoutInflater inflaterTemp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflaterTemp = inflater;
        view = inflater.inflate(R.layout.fragment_shopping_product_price, container, false);
        //
        //vAlertDialog = inflater.inflate(R.layout.alert_dialog, null);
        //
        MyOpenHelper dbHelper = new MyOpenHelper(view.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadShoppingProductPrice(db, dbHelper);
        }
        return view;
    }

    private void loadShoppingProductPrice(final SQLiteDatabase db, final MyOpenHelper dbHelper) {
        try {
            //
            ((ShoppingActivity) getActivity()).tienda.setMercadoActivo(dbHelper.getMercadoActivo(db, ((ShoppingActivity) getActivity()).tienda));
            //
            if (((ShoppingActivity) getActivity()).tienda.getMercadoActivo().getMercadoProductos().get(0).getId() > 0) {
                //
                ((ShoppingActivity) getActivity()).fabFinishShopping.setEnabled(true);
                //
                ArrayList<MercadoProducto> mercadoProductoList = (ArrayList<MercadoProducto>) ((ShoppingActivity) getActivity()).tienda.getMercadoActivo().getMercadoProductos();
                //
                final ListView lvMercadoProducto = view.findViewById(R.id.lvShoppingProductPrice);
                ArrayAdapter<MercadoProducto> aMercadoProducto = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, mercadoProductoList);
                lvMercadoProducto.setAdapter(aMercadoProducto);
                TextView tvTotalVal = ((ShoppingActivity) getActivity()).findViewById(R.id.tvTotalVal);
                tvTotalVal.setText(String.valueOf(((ShoppingActivity) getActivity()).tienda.getMercadoActivo().getTotal()));
                TextView tvTotalPr = ((ShoppingActivity) getActivity()).findViewById(R.id.tvTotalPr);
                Iterator<MercadoProducto> iMercadoProducto = mercadoProductoList.iterator();
                int totalPr = 0;
                while (iMercadoProducto.hasNext()) {
                    MercadoProducto mercadoProducto = iMercadoProducto.next();
                    totalPr += mercadoProducto.getCantidad();
                }
                tvTotalPr.setText(String.valueOf(totalPr));
                //
                lvMercadoProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        vAlertDialog = inflaterTemp.inflate(R.layout.alert_dialog, null);

                        final MercadoProducto mercadoProducto = (MercadoProducto) lvMercadoProducto.getItemAtPosition(position);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setTitle(R.string.do_you_want);
                        TextView tvUse = vAlertDialog.findViewById(R.id.tvUse);
                        EditText etTotal = (EditText) vAlertDialog.findViewById(R.id.etTotal);
                        tvUse.setText(R.string.modify_or_delete);
                        etTotal.setText(String.valueOf(mercadoProducto.getCantidad()));
                        dialog.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText etTotal = (EditText) vAlertDialog.findViewById(R.id.etTotal);
                                final int cantidad = Integer.parseInt(etTotal.getText().toString());
                                if (cantidad > 0 && mercadoProducto.getCantidad() != cantidad) {
                                    updateMercadoProducto(mercadoProducto, cantidad, db, dbHelper);
                                }
                            }
                        });
                        dialog.setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteMercadoProducto(mercadoProducto, db, dbHelper);
                            }
                        });
                        dialog.setView(vAlertDialog).show();
                    }
                });
            } else {
                //
                ArrayList<MercadoProducto> mercadoProductoList = new ArrayList<>();
                ListView lvMercadoProducto = view.findViewById(R.id.lvShoppingProductPrice);
                ArrayAdapter<MercadoProducto> aMercadoProducto = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, mercadoProductoList);
                lvMercadoProducto.setAdapter(aMercadoProducto);
                //
                TextView tvTotalVal = ((ShoppingActivity) getActivity()).findViewById(R.id.tvTotalVal);
                tvTotalVal.setText("0");
                TextView tvTotalPr = ((ShoppingActivity) getActivity()).findViewById(R.id.tvTotalPr);
                tvTotalPr.setText("0");
                //
            }
            //
        } catch (Exception e) {
            Toast.makeText(view.getContext(), R.string.empty_shopping, Toast.LENGTH_SHORT).show();
            Log.e("ShopProductPriceFrag", "loadShoppingProductPrice: " + e.getMessage(), null);
        }
    }

    private void deleteMercadoProducto(MercadoProducto mercadoProducto, SQLiteDatabase db, MyOpenHelper dbHelper) {
        float flagDelete = dbHelper.deleteMercadoProducto(db, mercadoProducto);
        if (flagDelete > 0) {
            Toast.makeText(view.getContext(), R.string.deleted, Toast.LENGTH_SHORT).show();
            loadShoppingProductPrice(db, dbHelper);
        }
    }

    private void updateMercadoProducto(MercadoProducto mercadoProducto, int cantidad, SQLiteDatabase db, MyOpenHelper dbHelper) {
        float falgUpdate = dbHelper.updateMercadoProducto(db, mercadoProducto, cantidad);
        if (falgUpdate > 0) {
            Toast.makeText(view.getContext(), R.string.updated, Toast.LENGTH_SHORT).show();
            loadShoppingProductPrice(db, dbHelper);
        }
    }
}
