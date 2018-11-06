package com.traffico.augusta.clases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

import com.traffico.augusta.entidades.Departamento;
import com.traffico.augusta.entidades.Mercado;
import com.traffico.augusta.entidades.MercadoProducto;
import com.traffico.augusta.entidades.Municipio;
import com.traffico.augusta.entidades.Producto;
import com.traffico.augusta.entidades.Tienda;
import com.traffico.augusta.entidades.TiendaProducto;
import com.traffico.augusta.entidades.Usuario;
import com.traffico.augusta.entidades.ValorProducto;
import com.traffico.augusta.interfaces.StringCreacion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MyOpenHelper extends SQLiteOpenHelper implements StringCreacion {

    private static final String DB_NAME = "augusta.sqlite";
    private static final int DB_VERSION = 1;
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

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

    public int updateProducto(SQLiteDatabase db, Producto producto) {
        ContentValues cv = new ContentValues();
        cv.put("barcode", producto.getBarCode());
        cv.put("marca", producto.getMarca());
        cv.put("descripcion", producto.getDescripcion());
        cv.put("medida", producto.getMedida());
        cv.put("valor_medida", producto.getValorMedida());
        String[] arg = new String[]{String.valueOf(producto.getId())};
        return db.update("producto", cv, " id = ?", arg);
    }

    public ArrayList<ValorProducto> getValorProductos(SQLiteDatabase db, Tienda tienda, Producto producto) {
        ArrayList<ValorProducto> valorProductoList = new ArrayList<>();
        String arg[] = new String[]{String.valueOf(tienda.getId()), String.valueOf(producto.getId())};
        Cursor cValorProducto = db.rawQuery(QRY_VALOR_PRODUCTO_TIENDA_PRODUCTO, arg);
        while (cValorProducto.moveToNext()) {
            ValorProducto valorProducto = new ValorProducto();
            valorProducto.setId(cValorProducto.getInt(0));
            valorProducto.setValor(cValorProducto.getFloat(1));
            valorProducto.setValorEquivalente(cValorProducto.getFloat(2));
            //
            String dtStart = cValorProducto.getString(3);
            try {
                Date date = format.parse(dtStart);
                System.out.println(date);
                valorProducto.setFechaRegistro(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //
            TiendaProducto tiendaProducto = new TiendaProducto();
            tiendaProducto.setId(cValorProducto.getInt(4));
            tiendaProducto.setTienda(tienda);
            tiendaProducto.setProducto(producto);
            valorProducto.setIdTiendaProducto(tiendaProducto);
            valorProductoList.add(valorProducto);
        }
        return valorProductoList;
    }

    public TiendaProducto getTiendaProducto(SQLiteDatabase db, Tienda tienda, Producto producto) {
        TiendaProducto tiendaProducto = new TiendaProducto();
        String[] arg = new String[]{String.valueOf(tienda.getId()), String.valueOf(producto.getId())};
        Cursor ctiendaProducto = db.rawQuery(QRY_TIENDA_PRODUCTO_TIENDA_PRODUCTO, arg);
        while (ctiendaProducto.moveToNext()) {
            tiendaProducto.setId(ctiendaProducto.getInt(0));
            tiendaProducto.setTienda(tienda);
            tiendaProducto.setProducto(producto);
        }
        if (ctiendaProducto.getCount() == 0) {
            ContentValues cv = new ContentValues();
            cv.put("id_tienda", tienda.getId());
            cv.put("id_producto", producto.getId());
            db.insert("tienda_producto", null, cv);
            tiendaProducto = getTiendaProducto(db, tienda, producto);
        }
        return tiendaProducto;
    }

    public long insertValorProducto(SQLiteDatabase db, ValorProducto valorProducto) {
        long flagInsert = 0;
        try {
            ContentValues cv = new ContentValues();
            cv.put("valor", valorProducto.getValor());
            cv.put("valor_equivalente", valorProducto.getValorEquivalente());
            //
            Date date = new Date();
            String fecha = format.format(date);
            //
            cv.put("fecha_registro", fecha + "");
            cv.put("id_tienda_producto", valorProducto.getIdTiendaProducto().getId());
            flagInsert = db.insert("valor_producto", null, cv);
        } catch (SQLException e) {
            Log.e("MyOpenHelper", "insertValorProducto: " + e.getMessage(), null);
        } catch (Exception e) {
            Log.e("MyOpenHelper", "insertValorProducto: " + e.getMessage(), null);
        } finally {
            return flagInsert;
        }
    }

    public Mercado getMercadoActivo(SQLiteDatabase db, Tienda tienda) {
        Mercado mercado = null;
        ArrayList<MercadoProducto> mercadoProductoList = new ArrayList<>();
        //
        String[] arg = new String[]{String.valueOf(tienda.getId())};
        Cursor cMercadoProducto = db.rawQuery(QRY_MERCADO_PRODUCTO_TIENDA, arg);
        //
        if (cMercadoProducto.getCount() > 0) {
            while (cMercadoProducto.moveToNext()) {
                if (mercado == null) {
                    mercado = new Mercado();
                    //mercado.setId(cMercadoProducto.getInt(0));
                    mercado.setId(cMercadoProducto.getInt(cMercadoProducto.getColumnIndex("id")));
                    mercado.setTienda(tienda);
                    //mercado.setTotal(cMercadoProducto.getInt(1));
                    mercado.setTotal(cMercadoProducto.getInt(cMercadoProducto.getColumnIndex("total")));
                    //
                    //String dtStart = cMercadoProducto.getString(2);
                    String dtStart = cMercadoProducto.getString(cMercadoProducto.getColumnIndex("fecha_registro"));
                    try {
                        Date date = format.parse(dtStart);
                        mercado.setFechaRegistro(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //
                    //mercado.setEstadoMercado(cMercadoProducto.getInt(3));
                    mercado.setEstadoMercado(cMercadoProducto.getInt(cMercadoProducto.getColumnIndex("estado_mercado")));
                }
                MercadoProducto mercadoProducto = new MercadoProducto();
                //mercadoProducto.setId(cMercadoProducto.getInt(4));
                mercadoProducto.setId(cMercadoProducto.getInt(cMercadoProducto.getColumnIndex("id_mercado_producto")));
                //mercadoProducto.setCantidad(cMercadoProducto.getInt(5));
                mercadoProducto.setCantidad(cMercadoProducto.getInt(cMercadoProducto.getColumnIndex("cantidad")));
                //mercadoProducto.setTotal(cMercadoProducto.getFloat(6));
                mercadoProducto.setTotal(cMercadoProducto.getFloat(cMercadoProducto.getColumnIndex("total_mercado_producto")));
                //
                ValorProducto valorProducto = new ValorProducto();
                //valorProducto.setId(cMercadoProducto.getInt(7));
                valorProducto.setId(cMercadoProducto.getInt(cMercadoProducto.getColumnIndex("valor_producto_id")));
                valorProducto.setValor(cMercadoProducto.getInt(cMercadoProducto.getColumnIndex("valor")));
                //
                TiendaProducto tiendaProducto = new TiendaProducto();
                //tiendaProducto.setId(cMercadoProducto.getInt(8));
                tiendaProducto.setId(cMercadoProducto.getInt(cMercadoProducto.getColumnIndex("id_tienda_producto")));
                //
                Producto producto = new Producto();
                //producto.setId(cMercadoProducto.getInt(9));
                producto.setId(cMercadoProducto.getInt(cMercadoProducto.getColumnIndex("id_producto")));
                //producto.setBarCode(cMercadoProducto.getString(10));
                producto.setBarCode(cMercadoProducto.getString(cMercadoProducto.getColumnIndex("barcode")));
                //producto.setMarca(cMercadoProducto.getString(11));
                producto.setMarca(cMercadoProducto.getString(cMercadoProducto.getColumnIndex("marca")));
                //producto.setDescripcion(cMercadoProducto.getString(12));
                producto.setDescripcion(cMercadoProducto.getString(cMercadoProducto.getColumnIndex("descripcion")));
                //producto.setMedida(cMercadoProducto.getString(13));
                producto.setMedida(cMercadoProducto.getString(cMercadoProducto.getColumnIndex("medida")));
                //producto.setValorMedida(cMercadoProducto.getFloat(14));
                producto.setValorMedida(cMercadoProducto.getFloat(cMercadoProducto.getColumnIndex("valor_medida")));
                tiendaProducto.setProducto(producto);
                //
                mercadoProducto.setMercado(mercado);
                tiendaProducto.setProducto(producto);
                valorProducto.setIdTiendaProducto(tiendaProducto);
                mercadoProducto.setValorProducto(valorProducto);
                //
                mercadoProductoList.add(mercadoProducto);
            }
            mercado.setMercadoProductos(mercadoProductoList);
        }
        //
        return mercado;
    }

    public long insertMercadoProducto(SQLiteDatabase db, Tienda tienda, ValorProducto valorProducto, EditText etTotal) {
        long flagInsert = 0;
        try {
            if (tienda.getMercadoActivo() != null) {
                ContentValues cvMP = new ContentValues();
                cvMP.put("id_mercado", tienda.getMercadoActivo().getId());
                cvMP.put("valor_producto_id", valorProducto.getId());
                //
                int cantidad = Integer.parseInt(etTotal.getText().toString());
                cvMP.put("cantidad", cantidad);
                //
                float totalMP = valorProducto.getValor() * cantidad;
                float totalM = valorProducto.getValor() * cantidad;
                cvMP.put("total", totalMP);
                //
                ArrayList<MercadoProducto> mercadoProductos = (ArrayList<MercadoProducto>) tienda.getMercadoActivo().getMercadoProductos();
                Iterator<MercadoProducto> iMercadoProducto = mercadoProductos.iterator();
                boolean flagExist = false;
                int idMercadoProducto = 0;
                while (iMercadoProducto.hasNext()) {
                    MercadoProducto mercadoProducto = iMercadoProducto.next();
                    int idProductoOld = mercadoProducto.getValorProducto().getIdTiendaProducto().getProducto().getId();
                    int idProductoNew = valorProducto.getIdTiendaProducto().getProducto().getId();
                    if (idProductoNew == idProductoOld) {
                        cantidad = cantidad + mercadoProducto.getCantidad();
                        totalMP = totalMP + mercadoProducto.getTotal();
                        idMercadoProducto = mercadoProducto.getId();
                        flagExist = true;
                    }
                }
                if (flagExist) {
                    //update
                    ContentValues cvUMP = new ContentValues();
                    cvUMP.put("cantidad", cantidad);
                    cvUMP.put("total", totalMP);
                    flagInsert = db.update("mercado_producto", cvUMP, "id = " + idMercadoProducto, null);
                } else {
                    flagInsert = db.insert("mercado_producto", null, cvMP);
                }
                //

                if (flagInsert > 0) {
                    ContentValues cvM = new ContentValues();
                    cvM.put("total", tienda.getMercadoActivo().getTotal() + totalM);
                    String[] arg = new String[]{String.valueOf(tienda.getMercadoActivo().getId())};
                    int flagUpdate = db.update("mercado", cvM, "id = ?", arg);
                }
            } else {
                ContentValues cv = new ContentValues();
                cv.put("id_tienda", tienda.getId());
                Usuario usuario = getUsuario(db);
                cv.put("id_usuario", usuario.getId());
                //
                Date date = new Date();
                String fecha = format.format(date);
                //
                cv.put("total", 0);
                //
                cv.put("fecha_registro", String.valueOf(fecha));
                cv.put("estado_mercado", 1);
                if (db.insert("mercado", null, cv) > 0) {
                    tienda.setMercadoActivo(getMercadoActivo(db, tienda));
                    flagInsert = insertMercadoProducto(db, tienda, valorProducto, etTotal);
                    flagInsert = flagInsert;
                }
            }
        } catch (SQLException e) {
            flagInsert = 0;
            Log.e("MyOpenHelper", "insertMercadoProducto: " + e.getMessage(), null);
        } catch (Exception e) {
            flagInsert = 0;
            Log.e("MyOpenHelper", "insertMercadoProducto: " + e.getMessage(), null);
        } finally {
            return flagInsert;
        }

    }

    public float updateMercadoProducto(SQLiteDatabase db, MercadoProducto mercadoProducto, int cantidad) {
        float flagUpdate = 0;
        float total;
        total = mercadoProducto.getValorProducto().getValor() * cantidad;
        ContentValues cvMP = new ContentValues();
        ContentValues cvM = new ContentValues();
        cvMP.put("cantidad", cantidad);
        cvMP.put("total", total);
        float totalMercado = mercadoProducto.getMercado().getTotal() - mercadoProducto.getTotal();
        totalMercado = totalMercado + total;
        cvM.put("total", totalMercado);
        flagUpdate = db.update("mercado_producto", cvMP, "id = " + mercadoProducto.getId(), null);
        if (flagUpdate > 0) {
            flagUpdate = db.update("mercado", cvM, "id = " + mercadoProducto.getMercado().getId(), null);
        }
        return flagUpdate;
    }

    public float deleteMercadoProducto(SQLiteDatabase db, MercadoProducto mercadoProducto) {
        float flagDelete = 0;
        float flagUpdate = 0;
        flagDelete = db.delete("mercado_producto", "id = " + mercadoProducto.getId(), null);
        float total = mercadoProducto.getMercado().getTotal() - mercadoProducto.getTotal();
        ContentValues cvM = new ContentValues();
        cvM.put("total", total);
        if (flagDelete > 0) {
            flagUpdate = db.update("mercado", cvM, "id = " + mercadoProducto.getMercado().getId(), null);
        }
        return flagUpdate;
    }
}
