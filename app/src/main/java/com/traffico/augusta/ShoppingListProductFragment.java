package com.traffico.augusta;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.traffico.augusta.entidades.Mercado;
import com.traffico.augusta.entidades.MercadoProducto;

import java.util.ArrayList;


public class ShoppingListProductFragment extends Fragment {

    View view;
    ListView lvShoppingProductPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_shopping_list_product, container, false);
        Mercado mercado = new Mercado();
        mercado = getArguments() != null ? (Mercado) getArguments().getSerializable("Mercado") : mercado;
        lvShoppingProductPrice = view.findViewById(R.id.lvShoppingProductPrice);
        ArrayList<MercadoProducto> mercadoProductos = (ArrayList<MercadoProducto>) mercado.getMercadoProductos();
        ArrayAdapter<MercadoProducto> aMercadoProducto = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, mercadoProductos);
        lvShoppingProductPrice.setAdapter(aMercadoProducto);
        return view;
    }

}
