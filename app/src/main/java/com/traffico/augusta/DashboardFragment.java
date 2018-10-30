package com.traffico.augusta;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class DashboardFragment extends Fragment {

    View view;
    Button bRecordPrice;
    Button bShopping;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        bRecordPrice = view.findViewById(R.id.bRecordPrice);
        bRecordPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), R.string.record_price, Toast.LENGTH_LONG).show();
                Intent iRecordPrice = new Intent(getActivity(), RecordPriceStoreActivity.class);
                getActivity().startActivity(iRecordPrice);
            }
        });
        bShopping = view.findViewById(R.id.bShopping);
        bShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), R.string.shopping, Toast.LENGTH_LONG).show();
                Intent iShopping = new Intent(getActivity(), ShoppingStoreActivity.class);
                getActivity().startActivity(iShopping);
            }
        });
        return view;
    }

}
