package com.traffico.augusta.clases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.traffico.augusta.entidades.Departamento;
import com.traffico.augusta.entidades.Municipio;
import com.traffico.augusta.entidades.Producto;
import com.traffico.augusta.entidades.Tienda;
import com.traffico.augusta.entidades.Usuario;
import com.traffico.augusta.interfaces.StringCreacion;

import java.util.ArrayList;
import java.util.Iterator;

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
        Cursor cUsuario = db.rawQuery(QRY_USUARIOS, null);
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

    public Usuario getUsuario(SQLiteDatabase db) {
        Usuario usuario = new Usuario();
        Cursor cUsuario = db.rawQuery(QRY_USUARIOS, null);
        while (cUsuario.moveToNext()) {
            usuario.setId(cUsuario.getInt(0));
            usuario.setNombre(cUsuario.getString(1));
            usuario.setApellido(cUsuario.getString(2));
            usuario.setDireccion(cUsuario.getString(3));
            usuario.setCoordenadas(cUsuario.getString(4));
            usuario.setEmail(cUsuario.getString(5));
            usuario.setFacebook(cUsuario.getString(6));
            usuario.setGoogle(cUsuario.getString(7));
            Municipio municipio = new Municipio();
            municipio.setId(cUsuario.getInt(8));
            usuario.setMunicipio(municipio);
        }
        return usuario;
    }

    public void updateUsuario(SQLiteDatabase db, Usuario usuario) {
        ContentValues cv = new ContentValues();
        cv.put("nombre", usuario.getNombre());
        cv.put("apellido", usuario.getApellido());
        cv.put("direccion", usuario.getDireccion());
        cv.put("coordenadas", usuario.getCoordenadas());
        cv.put("email", usuario.getEmail());
        db.update("usuario", cv, "id = " + usuario.getId(), null);
    }

    public ArrayList<Tienda> getTiendas(SQLiteDatabase db) {
        ArrayList<Tienda> tiendas = new ArrayList<>();
        Cursor cTiendas = db.rawQuery(QRY_TIENDAS, null);
        while (cTiendas.moveToNext()) {
            //
            Departamento departamento = new Departamento();
            departamento.setId(cTiendas.getInt(6));
            departamento.setDescripcion(cTiendas.getString(7));
            //
            Municipio municipio = new Municipio();
            municipio.setId(cTiendas.getInt(4));
            municipio.setDescripcion(cTiendas.getString(5));
            municipio.setDepartamento(departamento);
            //
            Tienda tienda = new Tienda();
            tienda.setId(cTiendas.getInt(0));
            tienda.setDescripcion(cTiendas.getString(1));
            tienda.setDireccion(cTiendas.getString(2));
            tienda.setCoordenadas(cTiendas.getString(3));
            tienda.setMunicipio(municipio);
            //
            tiendas.add(tienda);
            //
        }
        return tiendas;
    }

    public ArrayList<Departamento> getDepartamentos(SQLiteDatabase db) {
        ArrayList<Departamento> departamentos = new ArrayList<Departamento>();
        ArrayList<Municipio> municipios = new ArrayList<Municipio>();
        Cursor cDepartamentos = db.rawQuery(QRY_DEPARTAMENTO_MUNICIPIOS, null);
        while (cDepartamentos.moveToNext()) {
            //
            Departamento departamento = new Departamento();
            departamento.setId(cDepartamentos.getInt(0));
            departamento.setDescripcion(cDepartamentos.getString(1));
            //
            Municipio municipio = new Municipio();
            municipio.setId(cDepartamentos.getInt(2));
            municipio.setDescripcion(cDepartamentos.getString(3));
            municipio.setDepartamento(departamento);
            //
            Iterator<Departamento> iDepartamento = departamentos.iterator();
            boolean fDepartamento = false;
            while (iDepartamento.hasNext()) {
                Departamento tDepartamento = iDepartamento.next();
                if (departamento.getId() == tDepartamento.getId()) {
                    fDepartamento = true;
                    Iterator<Municipio> iMunicipio = tDepartamento.getMunicipios().iterator();
                    boolean fMunicipio = false;
                    while (iMunicipio.hasNext()) {
                        Municipio tMunicipio = iMunicipio.next();
                        if (tMunicipio.getId() == municipio.getId()) {
                            fMunicipio = true;
                        }
                    }
                    if (fMunicipio == false) {
                        municipios.add(municipio);
                    }
                }
            }
            if (fDepartamento == false) {
                municipios = new ArrayList<Municipio>();
                municipios.add(municipio);
                departamento.setMunicipios(municipios);
                departamentos.add(departamento);
            }
        }
        return departamentos;
    }

    public long insertTienda(SQLiteDatabase db, Tienda tienda) {
        ContentValues cv = new ContentValues();
        cv.put("descripcion", tienda.getDescripcion());
        cv.put("direccion", tienda.getDireccion());
        cv.put("coordenadas", tienda.getCoordenadas());
        cv.put("id_municipio", tienda.getMunicipio().getId());
        return db.insert("tienda", null, cv);
    }

    public int updateTienda(SQLiteDatabase db, Tienda tienda) {
        ContentValues cv = new ContentValues();
        cv.put("descripcion", tienda.getDescripcion());
        cv.put("direccion", tienda.getDireccion());
        cv.put("coordenadas", tienda.getCoordenadas());
        cv.put("id_municipio", tienda.getMunicipio().getId());
        return db.update("tienda", cv, " id = ?", new String[]{String.valueOf(tienda.getId())});
    }

    public ArrayList<Producto> getProductos(SQLiteDatabase db) {
        ArrayList<Producto> productos = new ArrayList<>();
        Cursor cProductos = db.rawQuery(QRY_PRODUCTO, null);
        while (cProductos.moveToNext()) {
            Producto producto = new Producto();
            producto.setId(cProductos.getInt(0));
            producto.setBarCode(cProductos.getString(1));
            producto.setMarca(cProductos.getString(2));
            producto.setDescripcion(cProductos.getString(3));
            producto.setMedida(cProductos.getString(4));
            producto.setValorMedida(cProductos.getFloat(5));
            productos.add(producto);
        }
        return productos;
    }

    public Producto getProducto(SQLiteDatabase db, String scanContent) {
        Producto producto = new Producto();
        String[] args = new String[]{scanContent};
        Cursor cursor = db.rawQuery(QRY_PRODUCTO_BARCODE, args);
        while (cursor.moveToNext()) {
            producto.setId(cursor.getInt(0));
            producto.setBarCode(cursor.getString(1));
            producto.setMarca(cursor.getString(2));
            producto.setDescripcion(cursor.getString(3));
            producto.setMedida(cursor.getString(4));
            producto.setValorMedida(cursor.getFloat(5));
        }
        return producto;
    }

    public long insertProduct(SQLiteDatabase db, Producto producto) {
        ContentValues cv = new ContentValues();
        cv.put("barcode", producto.getBarCode());
        cv.put("marca", producto.getMarca());
        cv.put("descripcion", producto.getDescripcion());
        cv.put("medida", producto.getMedida());
        cv.put("valor_medida", producto.getValorMedida());
        return db.insert("producto", null, cv);
    }
}
