package com.example.myfirstapplication.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.myfirstapplication.beans.Objeto;
import com.example.myfirstapplication.utilidades.Utilidades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Conexion extends SQLiteOpenHelper {

    public Conexion(Context context) {
        super(context, Utilidades.NOMBRE_BASE_DATOS, null, Utilidades.VERSION_BASE_DATOS);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Utilidades.CREAR_TABLA_CATEGORIA);
        db.execSQL(Utilidades.CREAR_TABLA_MARCA);
        db.execSQL(Utilidades.CREAR_TABLA_UNIDAD);
        db.execSQL(Utilidades.CREAR_TABLA_PRODUCTO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLA_CATEGORIA);
        db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLA_MARCA);
        db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLA_UNIDAD);
        db.execSQL("DROP TABLE IF EXISTS " + Utilidades.TABLA_PRODUCTO);
        onCreate(db);
    }

    public void insertarRegistro(Objeto objeto, String tabla) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values;
        try {
            values = new ContentValues();
            values.put("codigo", objeto.getCodigo());
            values.put("nombre", objeto.getNombre());
            if(tabla.equals("producto")) {
                values.put("precio", objeto.getPrecio());
                values.put("marca_id", objeto.getMarca_id());
                values.put("categoria_id", objeto.getCategoria_id());
                values.put("unidad_id", objeto.getUnidad_id());
            }
            db.insert(tabla, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public ArrayList<String> listaRegistros(String tabla, String cabecera) {
        //ArrayList<Objeto> lista = new ArrayList<Objeto>();
        ArrayList<String> lista = new ArrayList<String>();
        if(cabecera.equals("SI")) {
            lista.add("-- " + tabla.toUpperCase() + " --");
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String select = Utilidades.SELECT + tabla;
            Cursor cursor = db.rawQuery(select, null);
            if(cursor.getCount()>0) {
                while (cursor.moveToNext()) {
                    //String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                    lista.add(nombre);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return lista;
    }

    public int[] arrayRegistros(String tabla, int tamano) {
        //ArrayList<Objeto> lista = new ArrayList<Objeto>();
        int arrayRegistros[] = new int[tamano];
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String select = Utilidades.SELECT + tabla;
            Cursor cursor = db.rawQuery(select, null);
            if(cursor.getCount()>0) {
                int i = 0;
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    arrayRegistros[i] = id;
                    i++;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return arrayRegistros;
    }

    public StringRequest insertarRegistroWebService(final Context context, String URL, final String tabla, final Objeto objeto) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("tabla", tabla);
                parametros.put(Utilidades.CAMPO_ID_CATEGORIA, objeto.getCodigo());
                parametros.put(Utilidades.CAMPO_NOMBRE_CATEGORIA, objeto.getNombre());
                parametros.put(Utilidades.CAMPO_PRECIO_PRODUCTO, String.valueOf(objeto.getPrecio()));
                parametros.put(Utilidades.CAMPO_MARCA_PRODUCTO, String.valueOf(objeto.getMarca_id()));
                parametros.put(Utilidades.CAMPO_UNIDAD_PRODUCTO, String.valueOf(objeto.getUnidad_id()));
                parametros.put(Utilidades.CAMPO_CATEGORIA_PRODUCTO, String.valueOf(objeto.getCategoria_id()));
                return parametros;
            }
        };
        return stringRequest;
    }

    public Objeto obtenerRegistro(int idRegistro, String tabla) {
        Objeto objeto = new Objeto();
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            String select = Utilidades.SELECT + tabla + Utilidades.WHERE + idRegistro;
            Cursor cursor = db.rawQuery(select, null);
            if(cursor.getCount()>0) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex("id"));
                    String codigo = cursor.getString(cursor.getColumnIndex("codigo"));
                    String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
                    if(tabla.equals("producto")) {
                        Double precio = cursor.getDouble(cursor.getColumnIndex("precio"));
                        int marca_id = cursor.getInt(cursor.getColumnIndex("marca_id"));
                        int categoria_id = cursor.getInt(cursor.getColumnIndex("categoria_id"));
                        int unidad_id = cursor.getInt(cursor.getColumnIndex("unidad_id"));
                        objeto.setPrecio(precio);
                        objeto.setMarca_id(marca_id);
                        objeto.setCategoria_id(categoria_id);
                        objeto.setUnidad_id(unidad_id);
                    }
                    objeto.setId(id);
                    objeto.setCodigo(codigo);
                    objeto.setNombre(nombre);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return objeto;
    }

    public void actualizarRegistro(Objeto objeto, String tabla) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values;
        try {
            String[] parametros = {String.valueOf(objeto.getId())};
            values = new ContentValues();
            values.put("codigo", objeto.getCodigo());
            values.put("nombre", objeto.getNombre());
            if(tabla.equals("producto")) {
                values.put("precio", objeto.getPrecio());
                values.put("marca_id", objeto.getMarca_id());
                values.put("categoria_id", objeto.getCategoria_id());
                values.put("unidad_id", objeto.getUnidad_id());
            }
            db.update(tabla, values, "id=?", parametros);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public StringRequest actualizarRegistroWebService(final Context context, String URL, final String tabla, final Objeto objeto) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("tabla", tabla);
                parametros.put("id", String.valueOf(objeto.getId()));
                parametros.put(Utilidades.CAMPO_ID_CATEGORIA, objeto.getCodigo());
                parametros.put(Utilidades.CAMPO_NOMBRE_CATEGORIA, objeto.getNombre());
                parametros.put(Utilidades.CAMPO_PRECIO_PRODUCTO, String.valueOf(objeto.getPrecio()));
                parametros.put(Utilidades.CAMPO_MARCA_PRODUCTO, String.valueOf(objeto.getMarca_id()));
                parametros.put(Utilidades.CAMPO_UNIDAD_PRODUCTO, String.valueOf(objeto.getUnidad_id()));
                parametros.put(Utilidades.CAMPO_CATEGORIA_PRODUCTO, String.valueOf(objeto.getCategoria_id()));
                return parametros;
            }
        };
        return stringRequest;
    }
}
