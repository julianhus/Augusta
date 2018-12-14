package com.traffico.manhattan;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.traffico.manhattan.clases.CustomAdapterListViewShopping;
import com.traffico.manhattan.clases.CustomAdapterListViewStore;
import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Mercado;

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
                ((MenuActivity) getActivity()).loadFragment(shoppingListProductFragment);
            }
        });
        return view;
    }

    private void loadShopping(SQLiteDatabase db, MyOpenHelper dbHelper) {
        try {
            CustomAdapterListViewShopping adapter;
            int imageEdit = R.drawable.ic_menu_view;

            ArrayList<Mercado> mercadoList = dbHelper.getMercado(db);
            if (mercadoList.size() != 0) {
                ListView lvShopping = view.findViewById(R.id.lvShopping);
                adapter = new CustomAdapterListViewShopping(view.getContext(), mercadoList, imageEdit);
                lvShopping.setAdapter(adapter);
            /*
            ArrayList<Mercado> mercadoList = dbHelper.getMercado(db);
            ListView lvShopping = view.findViewById(R.id.lvShopping);
            ArrayAdapter<Mercado> aMercado = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, mercadoList);
            lvShopping.setAdapter(aMercado);*/
            } else {
                ((MenuActivity) getActivity()).loadFragment(new NotificationFragment());
                Toast.makeText(view.getContext(), R.string.empty_shopping, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(view.getContext(), R.string.empty_shopping, Toast.LENGTH_SHORT).show();
            //Log.e("ShoppingListFragment", "loadShopping: " + e.getMessage(), null);
        }
    }
}
