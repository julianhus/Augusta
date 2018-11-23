package com.traffico.manhattan;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Tienda;
import com.traffico.manhattan.entidades.Usuario;

public class ShoppingActivity extends AppCompatActivity {

    Tienda tienda;
    Usuario usuario;
    FloatingActionButton fabFinishShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        getSupportActionBar().setTitle(R.string.shopping);
        Intent iShopping = getIntent();
        tienda = (Tienda) iShopping.getSerializableExtra("Store");
        TextView tvStore = findViewById(R.id.tvStore);
        tvStore.setText(tienda.toString());
        final MyOpenHelper dbHelper = new MyOpenHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            usuario = dbHelper.getUsuario(db);
        }
        //
        loadFragment(new ShoppingProductPriceFragment());
        //
        FloatingActionButton fabAddProduct = findViewById(R.id.fabAddProduct);
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                loadFragment(new ShoppingProductFragment());
                //
            }
        });
        fabFinishShopping = findViewById(R.id.fabFinishShopping);
        fabFinishShopping.setEnabled(false);
        fabFinishShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateMercadoProducto(db, dbHelper);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(this, MenuActivity.class);
        startActivity(iMenu);
    }

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.commit();
    }

    private void validateMercadoProducto(final SQLiteDatabase db, final MyOpenHelper dbHelper) {
        if (tienda.getMercadoActivo().getEstadoMercado() == 1 && tienda.getMercadoActivo().getMercadoProductos().get(0).getId() > 0) {

            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.finalized_shopping);
            TextView tvTotalVal = findViewById(R.id.tvTotalVal);
            dialog.setMessage(getString(R.string.total_of_shopping, tvTotalVal.getText().toString()));
            dialog.setCancelable(false);
            dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    float flagUpdate = dbHelper.updateMercado(db, tienda.getMercadoActivo().getId());
                    if (flagUpdate > 0) {
                        Toast.makeText(getApplicationContext(), R.string.finalized_shopping, Toast.LENGTH_SHORT).show();
                        Intent menu = new Intent(getApplicationContext(), MenuActivity.class);
                        startActivity(menu);
                    }
                }
            });
            dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        }
    }

}
