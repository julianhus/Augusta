package com.traffico.manhattan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.traffico.manhattan.clases.CustomAdapterListViewProduct;
import com.traffico.manhattan.entidades.Producto;

import java.util.ArrayList;


public class SelectProductFragment extends Fragment {

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_select_product, container, false);

        ArrayList<Producto> productoList = new ArrayList<Producto>();
        productoList = getArguments() != null ? (ArrayList<Producto>) getArguments().getSerializable("ProductoList") : productoList;

        CustomAdapterListViewProduct adapter;
        int imageEdit = R.drawable.btn_radio_off;

        final ListView lvSelectProduct = view.findViewById(R.id.lvSelectProduct);

        adapter = new CustomAdapterListViewProduct(view.getContext(), productoList, imageEdit);
        lvSelectProduct.setAdapter(adapter);
        lvSelectProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Producto producto = (Producto) lvSelectProduct.getItemAtPosition(position);
                Fragment shoppingProductFragment = new ShoppingProductFragment();
                Bundle arg = new Bundle();
                arg.putSerializable("Producto", producto);
                shoppingProductFragment.setArguments(arg);
                ((ShoppingActivity) getActivity()).loadFragment(shoppingProductFragment);
            }
        });

        //
        return view;
    }

}
