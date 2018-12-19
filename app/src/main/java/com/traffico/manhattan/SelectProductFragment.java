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
import android.widget.Toast;

import com.traffico.manhattan.clases.CustomAdapterListViewProduct;
import com.traffico.manhattan.entidades.Producto;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


public class SelectProductFragment extends Fragment {

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_select_product, container, false);

        ArrayList<Producto> productoList = new ArrayList<Producto>();
        productoList = getArguments() != null ? (ArrayList<Producto>) getArguments().getSerializable("ProductoList") : productoList;
        String llamada = null;
        llamada = getArguments() != null ? (String) getArguments().getSerializable("Llamada") : llamada;

        CustomAdapterListViewProduct adapter;
        int imageEdit = R.drawable.btn_radio_off;

        final ListView lvSelectProduct = view.findViewById(R.id.lvSelectProduct);

        adapter = new CustomAdapterListViewProduct(view.getContext(), productoList, imageEdit);
        lvSelectProduct.setAdapter(adapter);
        final String llamadaTemp = llamada;
        lvSelectProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Producto producto = (Producto) lvSelectProduct.getItemAtPosition(position);
                    Fragment shoppingProductFragment = new ShoppingProductFragment();
                    Bundle arg = new Bundle();
                    arg.putSerializable("Producto", producto);
                    if (llamadaTemp.equals("ShoppingProductFragment")) {
                        shoppingProductFragment.setArguments(arg);
                        ((ShoppingActivity) getActivity()).loadFragment(shoppingProductFragment);
                    }
                    Fragment compareProductsFragment = new CompareProductsFragment();
                    if (llamadaTemp.equals("CompareProductsFragment")) {
                        compareProductsFragment.setArguments(arg);
                        ((MenuActivity) getActivity()).loadFragment(compareProductsFragment);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //
        return view;
    }

}
