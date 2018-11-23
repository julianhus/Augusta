package com.traffico.manhattan;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    View view;
    Button bProfile;
    Button bStore;
    Button bProduct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        bProfile = view.findViewById(R.id.bProfile);
        bProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.profile, Toast.LENGTH_LONG).show();
                Intent iProfile = new Intent(getActivity(), EditProfileActivity.class);
                getActivity().startActivity(iProfile);
            }
        });
        bStore = view.findViewById(R.id.bStore);
        bStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.store, Toast.LENGTH_LONG).show();
                Intent iStore = new Intent(getActivity(),StoreListActivity.class);
                getActivity().startActivity(iStore);
            }
        });
        bProduct = view.findViewById(R.id.bProduct);
        bProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), R.string.product, Toast.LENGTH_LONG).show();
                Intent iProduct = new Intent(getActivity(),ProductListActivity.class);
                getActivity().startActivity(iProduct);
            }
        });
        return view;
    }
}
