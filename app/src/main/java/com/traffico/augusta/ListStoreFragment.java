package com.traffico.augusta;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Tienda;

import java.util.ArrayList;


public class ListStoreFragment extends Fragment{

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list_store, container, false);
        MyOpenHelper dbHelper = new MyOpenHelper(view.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadStores(db, dbHelper);
        }
        return view;
    }

    private void loadStores(SQLiteDatabase db, MyOpenHelper dbHelper) {
        try {
            ArrayList<Tienda> tiendaList = dbHelper.getTiendas(db);
            final ListView lvStores = view.findViewById(R.id.lvStores);
            ArrayAdapter<Tienda> aTienda = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, tiendaList);
            lvStores.setAdapter(aTienda);
        } catch (Exception e) {
            Toast.makeText(view.getContext(),R.string.empty_stores, Toast.LENGTH_SHORT).show();
            Log.e("ListStoreFragment", "loadStores: " + e.getMessage(), null);
        }
    }
}
