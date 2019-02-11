package com.traffico.manhattan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Usuario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.regex.Pattern;

public class EditProfileActivity extends AppCompatActivity {

    Usuario usuario = new Usuario();
    //
    TextView tVName;
    TextView tVLastName;
    TextView tVEMail;
    //
    EditText eTName;
    EditText eTLastName;
    EditText eTAddress;
    EditText eTLocation;
    EditText eTEMail;
    //
    LatLng latLng;
    //
    ImageButton ibMap;
    //
    ImageView ivCheckMap;
    //
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle(R.string.profile);
        ibMap = findViewById(R.id.ibMap);
        ibMap.setBackgroundColor(Color.parseColor("#FF008577"));
        ivCheckMap =findViewById(R.id.ivCheckMap);
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            usuario = dbHelper.getUsuario(db);
            eTName = findViewById(R.id.etName);
            eTLastName = findViewById(R.id.etLastName);
            eTAddress = findViewById(R.id.etAddress);
            eTLocation = findViewById(R.id.etLocation);
            eTLocation.setEnabled(false);
            eTEMail = findViewById(R.id.etMail);
            eTName.setText(usuario.getNombre());
            eTLastName.setText(usuario.getApellido());
            eTAddress.setText(usuario.getDireccion());
            //
            Intent iMaps = getIntent();
            latLng = (LatLng) iMaps.getExtras().get("Ubicacion");
            address = (String) iMaps.getExtras().get("Address");

            if (latLng != null) {
                eTLocation.setText(latLng.latitude + ":" + latLng.longitude);
                eTLocation.setEnabled(false);
            } else {
                eTLocation.setText(usuario.getCoordenadas());
            }
            //
            if(address != null){
                eTAddress.setText(address);
            }
            //
            eTEMail.setText(usuario.getEmail());
            if (usuario.getFacebook() != null) {
                eTEMail.setEnabled(false);
            } else {
                eTEMail.setEnabled(true);
                //loginWithFacebook();
            }
            //
            if (!eTLocation.getText().toString().isEmpty()) {
                ivCheckMap.setBackgroundColor(Color.parseColor("#FF008577"));
            }
            //
        }
        eTName.setInputType(InputType.TYPE_NULL);
        eTName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTName.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(EditProfileActivity.this, MenuActivity.class);
        startActivity(iMenu);
        finish();
    }

    public void upDate(View view) {
        try {
            if (validate()) {
                usuario.setNombre(eTName.getText().toString());
                usuario.setApellido(eTLastName.getText().toString());
                usuario.setDireccion(eTAddress.getText().toString());
                usuario.setCoordenadas(eTLocation.getText().toString());
                usuario.setEmail(eTEMail.getText().toString());
                MyOpenHelper dbHelper = new MyOpenHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db != null) {
                    dbHelper.updateUsuario(db, usuario);
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.updated, Toast.LENGTH_SHORT);
                    toast.show();
                    final Intent mainActivity = new Intent(this, MainActivity.class);
                    //
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Magic here
                            startActivity(mainActivity);
                        }
                    }, 1000); // Millisecon
                }
            } else {
                Toast.makeText(getBaseContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        //
        try {
            tVName = findViewById(R.id.tvName);
            tVLastName = findViewById(R.id.tvLastName);
            tVEMail = findViewById(R.id.tvMail);
            //
            eTName = findViewById(R.id.etName);
            eTLastName = findViewById(R.id.etLastName);
            eTAddress = findViewById(R.id.etAddress);
            eTLocation = findViewById(R.id.etLocation);
            eTEMail = findViewById(R.id.etMail);
            //
            boolean flagName, flagLastName, flagEMail = true;
            if (eTName.getText().toString().isEmpty()) {
                tVName.setTextColor(Color.rgb(200, 0, 0));
                flagName = false;
            } else {
                tVName.setTextColor(-1979711488);
                flagName = true;
            }
            if (eTLastName.getText().toString().isEmpty()) {
                tVLastName.setTextColor(Color.rgb(200, 0, 0));
                flagLastName = false;
            } else {
                tVLastName.setTextColor(-1979711488);
                flagLastName = true;
            }
            Pattern pEMail = Patterns.EMAIL_ADDRESS;
            if (!pEMail.matcher(eTEMail.getText().toString()).matches() || eTEMail.getText().toString().isEmpty()) {
                tVEMail.setTextColor(Color.rgb(200, 0, 0));
                flagEMail = false;
            } else {
                tVEMail.setTextColor(-1979711488);
                flagEMail = true;
            }

            if (!flagName || !flagLastName || !flagEMail) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
            //Log.e("EditProfileActivity", "validate: " + e );
            return false;
        }
    }


    public void showMap(View view) {
        android.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.component_not_available);

        dialog.setMessage(R.string.the_full_version_has);
        dialog.setCancelable(false);
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                externalEstorageLite();
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.traffico.mercabarato");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
        /*Intent iMaps = new Intent(EditProfileActivity.this, MapsActivity.class);
        iMaps.putExtra("LlamadaMaps", "EditProfileActivity");
        eTLocation = findViewById(R.id.etLocation);
        if (!eTLocation.getText().toString().equals("")) {
            iMaps.putExtra("latLng", eTLocation.getText());
        }
        startActivity(iMaps);*/
    }

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private void externalEstorageLite() {
        try {

            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck < 0) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                File origen = new File("/data/data/com.traffico.mercabaratolite/databases/manhattan.sqlite");
                File destino = new File("/sdcard/manhattan.sqlite");
                FileChannel inChannel = new FileInputStream(origen).getChannel();
                FileChannel outChannel = new FileOutputStream(destino).getChannel();
                try
                {
                    inChannel.transferTo(0, inChannel.size(), outChannel);
                }
                finally
                {
                    if (inChannel != null)
                        inChannel.close();
                    if (outChannel != null)
                        outChannel.close();
                }
            }

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity", "onCreate: ", e.getCause());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), R.string.we_will_not_be, Toast.LENGTH_LONG).show();
                }
                return;

            }
        }
    }
}
