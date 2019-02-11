package com.traffico.manhattan;

import android.Manifest;

import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;

import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

import com.traffico.manhattan.clases.MyOpenHelper;
import com.traffico.manhattan.entidades.Usuario;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.regex.Pattern;

import static com.traffico.manhattan.R.color.colorPrimary;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    //
    String userIdFacebook, address;
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
    Button bSingIn;
    //
    CheckBox cbPolicy;
    //
    ImageButton ibMap;
    //
    LatLng latLng;
    //
    ImageView ivCheckMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        externalEstorage();
        //
        eTName = findViewById(R.id.etName);
        eTLastName = findViewById(R.id.etLastName);
        eTAddress = findViewById(R.id.etAddress);
        eTLocation = findViewById(R.id.etLocation);
        eTEMail = findViewById(R.id.etMail);
        //
        bSingIn = findViewById(R.id.bSingIn);
        bSingIn.setEnabled(false);
        cbPolicy = findViewById(R.id.cbPolicy);
        ibMap = findViewById(R.id.ibMap);
        ibMap.setEnabled(false);
        eTAddress = findViewById(R.id.etAddress);
        eTLocation = findViewById(R.id.etLocation);
        eTLocation.setEnabled(false);
        ivCheckMap = findViewById(R.id.ivCheckMap);
        //
        Intent iMaps = getIntent();
        latLng = (LatLng) iMaps.getExtras().get("Ubicacion");
        address = (String) iMaps.getExtras().get("Address");
        //
        if (latLng != null) {
            eTLocation.setText(latLng.latitude + ":" + latLng.longitude);
            eTLocation.setEnabled(false);
        }
        //
        if (address != null) {
            eTAddress.setText(address);
        }
        //
        if (!eTLocation.getText().toString().isEmpty()) {
            ivCheckMap.setBackgroundColor(Color.parseColor("#FF008577"));
        }
        //
        loginWithFacebook();
        //
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            boolean flagUserExists = false;
            if (db != null) {
                flagUserExists = dbHelper.searchUsuario(db);
                if (flagUserExists == true) {
                    Button bSingIn = findViewById(R.id.bSingIn);
                    bSingIn.setEnabled(false);
                    Intent menu = new Intent(this, MenuActivity.class);
                    startActivity(menu);
                    finish();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
        eTName.setInputType(InputType.TYPE_NULL);
        eTName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTName.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });
    }

    private void loginWithFacebook() {
        try {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
            //
            callbackManager = CallbackManager.Factory.create();

            //
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setEnabled(false);
            loginButton.setReadPermissions("email");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    //Log.i("onSuccess", "UserId: " + loginResult.getAccessToken().getUserId());
                    //Log.i("onSuccess", "Token: " + loginResult.getAccessToken().getToken());
                    //Log.i("onSuccess", "Recently Granted Permissions " + loginResult.getRecentlyGrantedPermissions());
                    setFacebookData(loginResult);
                }

                @Override
                public void onCancel() {
                    //Log.i("onCancel", "Cancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Fallo la conexión con Facebook, Intente nuevamente", Toast.LENGTH_SHORT);
                    toast.show();
                    //Log.e("Error", "loginWithFacebook_onError: " + error.getMessage(), null);
                }

                private void setFacebookData(final LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        //Log.i("Response", response.toString());
                                        String email = null;
                                        try {
                                            email = response.getJSONObject().getString("email");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast toast = Toast.makeText(getApplicationContext(), "Facebook no compartio el eMail, Por Favor Digitelo Manualmente", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                        String firstName = response.getJSONObject().getString("first_name");
                                        String lastName = response.getJSONObject().getString("last_name");
                                        Profile profile = Profile.getCurrentProfile();
                                        /*String id = profile.getId();
                                        String link = profile.getLinkUri().toString();
                                        Log.i("Link", link);
                                        if (Profile.getCurrentProfile() != null) {
                                            Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                                        }*/
                                        //Log.i("Login" + "Email", email);
                                        EditText eTEMail = findViewById(R.id.etMail);
                                        eTEMail.setText(email);
                                        //Log.i("Login" + "FirstName", firstName);
                                        EditText eTName = findViewById(R.id.etName);
                                        eTName.setText(firstName);
                                        //Log.i("Login" + "LastName", lastName);
                                        EditText eTLastName = findViewById(R.id.etLastName);
                                        eTLastName.setText(lastName);
                                        Button singIn = findViewById(R.id.bSingIn);
                                        userIdFacebook = loginResult.getAccessToken().getUserId().toString();
                                        if (!email.isEmpty()) {
                                            singIn.callOnClick();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getApplicationContext(), "Fallo la conexión con Facebook, Intente nuevamente", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "Fallo la conexión con Facebook, Intente nuevamente", Toast.LENGTH_SHORT).show();
                                        //Log.e("Error", "loginWithFacebook_setFacebookData: " + e.getMessage(), null);
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,email,first_name,last_name");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Fallo la aplicacion, Intente nuevamente", Toast.LENGTH_SHORT).show();
            //Log.e("Error", "loginWithFacebook: " + e.getMessage(), null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void register(View view) {
        //
        try {
            if (validate()) {
                Usuario usuario = new Usuario();
                usuario.setNombre(eTName.getText().toString());
                usuario.setApellido(eTLastName.getText().toString());
                usuario.setDireccion(eTAddress.getText().toString());
                usuario.setCoordenadas(eTLocation.getText().toString());
                usuario.setEmail(eTEMail.getText().toString());
                usuario.setFacebook(userIdFacebook);
                usuario.setGoogle("");
                MyOpenHelper dbHelper = new MyOpenHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if (db != null) {
                    Long flagInsert = dbHelper.insertUsuario(db, usuario);
                    if (flagInsert > 0) {
                        Toast.makeText(getApplicationContext(), R.string.registered_user, Toast.LENGTH_SHORT).show();
                        if (eTLocation.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), R.string.remember_to_register_your_location, Toast.LENGTH_SHORT).show();
                        }
                        final Intent mainActivity = new Intent(this, MainActivity.class);
                        //
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Magic here
                                startActivity(mainActivity);
                                finish();
                            }
                        }, 1000); // Millisecond 1000 = 1 sec
                        //
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.fail, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getBaseContext(), R.string.redInfo, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.fail, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validate() {
        //
        tVName = findViewById(R.id.tvName);
        tVLastName = findViewById(R.id.tvLastName);
        tVEMail = findViewById(R.id.tvMail);
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
    }

    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        if (checked) {
            bSingIn.setEnabled(true);
            loginButton.setEnabled(true);
            ibMap.setEnabled(true);
            //ibMap.setBackgroundColor(Color.parseColor("#81C784"));
            ibMap.setBackgroundColor(Color.parseColor("#FF008577"));
        } else {
            bSingIn.setEnabled(false);
            loginButton.setEnabled(false);
            ibMap.setEnabled(false);
            ibMap.setBackgroundColor(Color.parseColor("#E0E0E0"));
        }

    }

    public void downloadPolicy(View view) {
        //
        Uri uri = Uri.parse("https://drive.google.com/file/d/1NMneDgeyDZw7BJhJ6oHiOLYrAhtHcwK3/view?usp=sharing");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
        //
    }

    public void showMap(View view) {
        Intent iMaps = new Intent(MainActivity.this, MapsActivity.class);
        iMaps.putExtra("LlamadaMaps", "MainActivity");
        startActivity(iMaps);
        finish();
    }

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    private void externalEstorage() {
        try {

            int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck < 0) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                /*
                Path origenPath = FileSystems.getDefault().getPath("/storage/emulated/0/manhattan.sqlite");
                Path destinoPath = FileSystems.getDefault().getPath("/data/data/com.traffico.mercabarato/databases/manhattan.sqlite");
                Files.copy(origenPath, destinoPath, StandardCopyOption.REPLACE_EXISTING);
                Files.deleteIfExists(origenPath);
                //
                Intent intent = new Intent("android.intent.action.DELETE");
                intent.setData(Uri.parse("package:" + "com.traffico.mercabaratolite"));
                startActivity(intent);
                */
            }

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                }
            }
        } catch (Exception e) {
            //Error
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                try {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        //
                        File origen = new File("/sdcard/manhattan.sqlite");
                        File destino = new File("/data/data/com.traffico.mercabarato/databases/manhattan.sqlite");
                        FileChannel inChannel = new FileInputStream(origen).getChannel();
                        FileChannel outChannel = new FileOutputStream(destino).getChannel();
                        try
                        {
                            inChannel.transferTo(0, inChannel.size(), outChannel);
                            origen.delete();

                        }
                        finally
                        {
                            if (inChannel != null)
                                inChannel.close();
                            if (outChannel != null)
                                outChannel.close();
                        }
                        //
                        Intent main = new Intent(this, MainActivity.class);
                        startActivity(main);
                        finish();
                        //
                    } else {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Toast.makeText(getApplicationContext(), R.string.we_will_not_be, Toast.LENGTH_LONG).show();
                    }
                    } catch (Exception e) {
                }
                return;

            }
        }
    }
}
