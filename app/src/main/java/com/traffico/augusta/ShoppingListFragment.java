package com.traffico.augusta;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import com.traffico.augusta.entidades.Mercado;
import com.traffico.augusta.entidades.Tienda;

import java.util.ArrayList;


public class ShoppingListFragment extends Fragment {

    View view;
    ListView lvShopping;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        MyOpenHelper dbHelper = new MyOpenHelper(view.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            loadShopping(db, dbHelper);
        }
        lvShopping = view.findViewById(R.id.lvShopping);
        lvShopping.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mercado mercado = (Mercado) lvShopping.getItemAtPosition(position);
                Bundle arg = new Bundle();
                arg.putSerializable("Mercado", mercado);
                Fragment shoppingListProductFragment = new ShoppingListProductFragment();
                shoppingListProductFragment.setArguments(arg);
                ((MenuActivity)getActivity()).loadFragment(shoppingListProductFragment);
            }
        });
        return view;
    }

    private void loadShopping(SQLiteDatabase db, MyOpenHelper dbHelper) {
        try {
            ArrayList<Mercado> mercadoList = dbHelper.getMercado(db);
            ListView lvShopping = view.findViewById(R.id.lvShopping);
            ArrayAdapter<Mercado> aMercado = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, mercadoList);
            lvShopping.setAdapter(aMercado);
        } catch (Exception e) {
            Toast.makeText(view.getContext(),R.string.empty_shopping, Toast.LENGTH_SHORT).show();
            Log.e("ShoppingListFragment", "loadShopping: " + e.getMessage(), null);
        }
    }
}
