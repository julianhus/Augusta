package com.traffico.manhattan;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Usuario;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().setTitle(R.string.profile);
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            usuario = dbHelper.getUsuario(db);
            EditText name = findViewById(R.id.etName);
            EditText lastName = findViewById(R.id.etLastName);
            EditText address = findViewById(R.id.etAddress);
            EditText location = findViewById(R.id.etLocation);
            EditText email = findViewById(R.id.etMail);
            name.setText(usuario.getNombre());
            lastName.setText(usuario.getApellido());
            address.setText(usuario.getDireccion());
            location.setText(usuario.getCoordenadas());
            email.setText(usuario.getEmail());
            if (usuario.getFacebook() != null) {
                email.setEnabled(false);
            } else {
                email.setEnabled(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent iMenu = new Intent(EditProfileActivity.this, MenuActivity.class);
        startActivity(iMenu);
    }

    public void upDate(View view) {
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
            Log.e("EditProfileActivity", "validate: " + e );
            return false;
        }
    }


    public void showMap(View view) {
        /*Uri gmmIntentUri = Uri.parse("geo:4.6750226,-74.1171807");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }*/
    }
}
