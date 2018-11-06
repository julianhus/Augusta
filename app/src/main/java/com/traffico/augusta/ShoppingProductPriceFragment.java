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
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.MercadoProducto;

import java.util.ArrayList;
import java.util.Iterator;


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
            //
            ((ShoppingActivity) getActivity()).tienda.setMercadoActivo(dbHelper.getMercadoActivo(db, ((ShoppingActivity) getActivity()).tienda));
            ArrayList<MercadoProducto> mercadoProductoList = (ArrayList<MercadoProducto>) ((ShoppingActivity) getActivity()).tienda.getMercadoActivo().getMercadoProductos();
            ListView lvMercadoProducto = view.findViewById(R.id.lvShoppingProductPrice);
            ArrayAdapter<MercadoProducto> aMercadoProducto = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, mercadoProductoList);
            lvMercadoProducto.setAdapter(aMercadoProducto);
            //
            TextView tvTotalVal = ((ShoppingActivity) getActivity()).findViewById(R.id.tvTotalVal);
            tvTotalVal.setText(String.valueOf(((ShoppingActivity) getActivity()).tienda.getMercadoActivo().getTotal()));
            TextView tvTotalPr = ((ShoppingActivity) getActivity()).findViewById(R.id.tvTotalPr);
            Iterator<MercadoProducto> iMercadoProducto = mercadoProductoList.iterator();
            int totalPr = 0;
            while (iMercadoProducto.hasNext()){
                MercadoProducto mercadoProducto = iMercadoProducto.next();
                totalPr += mercadoProducto.getCantidad();
            }
            tvTotalPr.setText(String.valueOf(totalPr));
            //
        } catch (Exception e) {
            Toast.makeText(view.getContext(), R.string.empty_shopping, Toast.LENGTH_SHORT).show();
            Log.e("ShopProductPriceFrag", "loadShoppingProductPrice: " + e.getMessage(), null);
        }
    }


}
