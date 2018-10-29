package com.traffico.augusta;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Usuario;

public class EditProfileActivity extends AppCompatActivity {

    Usuario usuario = new Usuario();

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

    public void upDate (View view){
        TextView tVName = findViewById(R.id.tvName);
        TextView tVLastName = findViewById(R.id.tvLastName);
        TextView tVEMail = findViewById(R.id.tvMail);
        EditText eTName = findViewById(R.id.etName);
        EditText eTLastName = findViewById(R.id.etLastName);
        EditText eTAddress = findViewById(R.id.etAddress);
        EditText eTLocation = findViewById(R.id.etLocation);
        EditText eTEMail = findViewById(R.id.etMail);
        if (eTName.getText().toString().isEmpty() && eTLastName.getText().toString().isEmpty() && eTEMail.getText().toString().isEmpty()) {
            tVName.setTextColor(Color.rgb(200, 0, 0));
            tVLastName.setTextColor(Color.rgb(200, 0, 0));
            tVEMail.setTextColor(Color.rgb(200, 0, 0));
        } else {
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
        }
    }
}
