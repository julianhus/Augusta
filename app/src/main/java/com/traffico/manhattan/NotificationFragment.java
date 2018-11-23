package com.traffico.manhattan;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class NotificationFragment extends Fragment {

    View view;
    Button bCompareBy;
    Button bShoppingList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        //
        bCompareBy = view.findViewById(R.id.bCompareBy);
        bShoppingList = view.findViewById(R.id.bShoppingList);
        bCompareBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MenuActivity)getActivity()).loadFragment(new CompareProductsFragment());
            }
        });
        bShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MenuActivity)getActivity()).loadFragment(new ShoppingListFragment());
            }
        });
        return view;
    }

}
