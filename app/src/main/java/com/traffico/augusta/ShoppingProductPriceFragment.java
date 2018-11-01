package com.traffico.augusta;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.MercadoProducto;

import java.util.ArrayList;


public class ShoppingProductPriceFragment extends Fragment {

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_shopping_product_price, container, false);
        MyOpenHelper dbHelper = new MyOpenHelper(view.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadShoppingProductPrice(db, dbHelper);
        }
        return view;
    }

    private void loadShoppingProductPrice(SQLiteDatabase db, MyOpenHelper dbHelper) {
        try {
            ArrayList<MercadoProducto> mercadoProductoList = dbHelper.getMercadoProducto(db, ((ShoppingActivity) getActivity()).tienda);
            ListView lvMercadoProducto = view.findViewById(R.id.lvShoppingProductPrice);
            ArrayAdapter<MercadoProducto> aMercadoProducto = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, mercadoProductoList);
            lvMercadoProducto.setAdapter(aMercadoProducto);
        } catch (Exception e) {
            Toast.makeText(view.getContext(), R.string.empty_shopping, Toast.LENGTH_SHORT).show();
            Log.e("ShopProductPriceFrag", "loadShoppingProductPrice: " + e.getMessage(), null);
        }
    }


}
