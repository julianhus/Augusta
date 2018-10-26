package com.traffico.augusta.clases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.traffico.augusta.entidades.Usuario;
import com.traffico.augusta.interfaces.StringCreacion;

public class MyOpenHelper extends SQLiteOpenHelper implements StringCreacion {

    private static final String DB_NAME = "augusta.sqlite";
    private static final int DB_VERSION = 1;

    public MyOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DEPARTAMENTO_TABLE);
        db.execSQL(MUNICIPIO_TABLE);
        db.execSQL(MUNICIPIO_INDEX);
        db.execSQL(TIENDA_TABLE);
        db.execSQL(TIENDA_INDEX);
        db.execSQL(TIENDA_UNIQUE);
        db.execSQL(PRODUCTO_TABLE);
        db.execSQL(PRODUCTO_UNIQUE);
        db.execSQL(USUARIO_TABLE);
        db.execSQL(USUARIO_INDEX);
        db.execSQL(USUARIO_UNIQUE);
        db.execSQL(MERCADO_TABLE);
        db.execSQL(MERCADO_INDEX_USUARIO);
        db.execSQL(MERCADO_INDEX_TIENDA);
        db.execSQL(TIENDA_PRODUCTO_TABLE);
        db.execSQL(TIENDA_PRODUCTO_INDEX_TIENDA);
        db.execSQL(TIENDA_PRODUCTO_INDEX_PRODUCTO);
        db.execSQL(TIENDA_PRODUCTO_UNIQUE);
        db.execSQL(VALOR_PRODUCTO_TABLE);
        db.execSQL(VALOR_PRODUCTO_INDEX);
        db.execSQL(MERCADO_PRODUCTO_TABLE);
        db.execSQL(MERCADO_PRODUCTO_INDEX_MERCADO);
        db.execSQL(MERCADO_PRODUCTO_INDEX_VALOR_PRODUCTO);
        db.execSQL(CARGA_DEPARTAMENTOS);
        db.execSQL(CARGA_MUNICIPIOS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean searchUsuario(SQLiteDatabase db) {
        Cursor cUsuario = db.rawQuery(QRY_USUARIO, null);
        if (cUsuario.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Long insertUsuario(SQLiteDatabase db, Usuario usuario) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("nombre", usuario.getNombre());
            cv.put("apellido", usuario.getApellido());
            cv.put("direccion", usuario.getDireccion());
            cv.put("coordenadas", usuario.getCoordenadas());
            cv.put("email", usuario.getEmail());
            cv.put("facebook", usuario.getFacebook());
            cv.put("google", usuario.getGoogle());
            return db.insert("usuario", null, cv);
        } catch (Exception e) {
            Log.e("Error", "insertUsuario: " + e.getMessage(), null);
            return (long) -1;
        }
    }
}
