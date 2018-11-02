package com.traffico.augusta;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Producto;
import com.traffico.augusta.google.zxing.integration.android.IntentIntegrator;
import com.traffico.augusta.google.zxing.integration.android.IntentResult;

import static com.facebook.FacebookSdk.getApplicationContext;


public class ShoppingProductFragment extends Fragment implements View.OnClickListener {

    View view;
    private EditText etBarCode;
    private ImageButton bScann;
    private ImageButton ibBack;
    private ImageButton ibSafe;
    private ImageButton ibForward;
    private ImageButton ibSearch;
    private Producto producto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_shopping, container, false);
        etBarCode = view.findViewById(R.id.etBarCode);
        bScann = view.findViewById(R.id.iBScan);
        bScann.setOnClickListener((View.OnClickListener) this);
        ibBack = view.findViewById(R.id.ibBack);
        ibBack.setOnClickListener((View.OnClickListener) this);
        ibSafe = view.findViewById(R.id.ibSafe);
        ibSafe.setOnClickListener((View.OnClickListener) this);
        ibSafe.setEnabled(false);
        ibForward = view.findViewById(R.id.ibForward);
        ibForward.setOnClickListener((View.OnClickListener) this);
        ibForward.setEnabled(false);
        ibSearch = view.findViewById(R.id.ibSearch);
        ibSearch.setOnClickListener((View.OnClickListener) this);

        return view;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.iBScan) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(ShoppingProductFragment.this);
            scanIntegrator.initiateScan();
        }
        if(view.getId() == R.id.ibBack){
            ((ShoppingActivity)getActivity()).loadFragment(new ShoppingProductPriceFragment());
        }
        if(view.getId() == R.id.ibSafe){

        }
        if(view.getId() == R.id.ibForward){
            Fragment shoppingRecordPriceFragment = new ShoppingRecordPriceFragment();
            Bundle arg = new Bundle();
            arg.putSerializable("Producto", producto);
            shoppingRecordPriceFragment.setArguments(arg);
            ((ShoppingActivity)getActivity()).loadFragment(shoppingRecordPriceFragment);
        }
        if(view.getId() == R.id.ibSearch){
            loadProduct();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            etBarCode.setText(scanContent);
            loadProduct();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void loadProduct() {
        MyOpenHelper dbHelper = new MyOpenHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            producto = dbHelper.getProducto(db, etBarCode.getText().toString());
            if (producto.getId() != 0) {
                etBarCode.setEnabled(false);
                EditText ettrademark = view.findViewById(R.id.ettrademark);
                ettrademark.setText(producto.getMarca());
                ettrademark.setEnabled(false);
                EditText etDescription = view.findViewById(R.id.etProduct);
                etDescription.setText(producto.getDescripcion());
                etDescription.setEnabled(false);
                EditText etMeasure = view.findViewById(R.id.etMeasure);
                etMeasure.setText(producto.getMedida());
                etMeasure.setEnabled(false);
                EditText etWeight = view.findViewById(R.id.etWeight);
                etWeight.setText("" + producto.getValorMedida());
                etWeight.setEnabled(false);
                ibForward.setEnabled(true);
            } else {

            }
        }
    }
}