package com.traffico.augusta;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import com.traffico.augusta.clases.MyOpenHelper;
import com.traffico.augusta.entidades.Usuario;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    //
    String userIdFacebook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        loginWithFacebook();
        //
        MyOpenHelper dbHelper = new MyOpenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean flagUserExists = false;
        if (db != null) {
            flagUserExists = dbHelper.searchUsuario(db);
            if (flagUserExists == true) {
                Button bSingIn = findViewById(R.id.bSingIn);
                bSingIn.setEnabled(false);
                Intent menu = new Intent(this, MenuActivity.class);
                startActivity(menu);
            }
        }
    }

    private void loginWithFacebook() {
        try {
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(this);
            //
            callbackManager = CallbackManager.Factory.create();

            //
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions("email");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    Log.i("onSuccess", "UserId: " + loginResult.getAccessToken().getUserId());
                    Log.i("onSuccess", "Token: " + loginResult.getAccessToken().getToken());
                    Log.i("onSuccess", "Recently Granted Permissions " + loginResult.getRecentlyGrantedPermissions());
                    setFacebookData(loginResult);
                }

                @Override
                public void onCancel() {
                    Log.i("onCancel", "Cancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Fallo la conexión con Facebook, Intente nuevamente", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.e("Error", "loginWithFacebook_onError: " + error.getMessage(), null);
                }

                private void setFacebookData(final LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        Log.i("Response", response.toString());
                                        String email = response.getJSONObject().getString("email");
                                        String firstName = response.getJSONObject().getString("first_name");
                                        String lastName = response.getJSONObject().getString("last_name");
                                        Profile profile = Profile.getCurrentProfile();
                                        /*String id = profile.getId();
                                        String link = profile.getLinkUri().toString();
                                        Log.i("Link", link);
                                        if (Profile.getCurrentProfile() != null) {
                                            Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                                        }*/
                                        Log.i("Login" + "Email", email);
                                        EditText eTEMail = findViewById(R.id.etMail);
                                        eTEMail.setText(email);
                                        Log.i("Login" + "FirstName", firstName);
                                        EditText eTName = findViewById(R.id.etName);
                                        eTName.setText(firstName);
                                        Log.i("Login" + "LastName", lastName);
                                        EditText eTLastName = findViewById(R.id.etLastName);
                                        eTLastName.setText(lastName);
                                        Button singIn = findViewById(R.id.bSingIn);
                                        userIdFacebook = loginResult.getAccessToken().getUserId().toString();
                                        singIn.callOnClick();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Fallo la conexión con Facebook, Intente nuevamente", Toast.LENGTH_SHORT);
                                        toast.show();
                                        Log.e("Error", "loginWithFacebook_setFacebookData: " + e.getMessage(), null);
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
            Toast toast = Toast.makeText(getApplicationContext(), "Fallo la aplicacion, Intente nuevamente", Toast.LENGTH_SHORT);
            toast.show();
            Log.e("Error", "loginWithFacebook: " + e.getMessage(), null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void register(View view) {
        TextView tVName = findViewById(R.id.tvName);
        TextView tVLastName = findViewById(R.id.tvLastName);
        TextView tVEMail = findViewById(R.id.tvMail);
        //
        EditText eTName = findViewById(R.id.etName);
        EditText eTLastName = findViewById(R.id.etLastName);
        EditText eTAddress = findViewById(R.id.etAddress);
        EditText eTLocation = findViewById(R.id.etLocation);
        EditText eTEMail = findViewById(R.id.etMail);
        //
        if (eTName.getText().toString().isEmpty() && eTLastName.getText().toString().isEmpty() && eTEMail.getText().toString().isEmpty()) {
            tVName.setTextColor(Color.rgb(200, 0, 0));
            tVLastName.setTextColor(Color.rgb(200, 0, 0));
            tVEMail.setTextColor(Color.rgb(200, 0, 0));
        } else {
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
                    Toast toast = Toast.makeText(getApplicationContext(), "Usuario Registrado", Toast.LENGTH_SHORT);
                    toast.show();
                    final Intent mainActivity = new Intent(this, MainActivity.class);
                    //
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Magic here
                            startActivity(mainActivity);
                        }
                    }, 1000); // Millisecond 1000 = 1 sec
                    //
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Fallo el Registro", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }
}
