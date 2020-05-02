package com.example.myfirstapplication.actividad;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myfirstapplication.R;
import com.example.myfirstapplication.beans.Objeto;
import com.example.myfirstapplication.db.Conexion;
import com.example.myfirstapplication.utilidades.Utilidades;

import java.util.ArrayList;

public class actividadProducto extends Fragment {

    private Spinner spinnerCategoria, spinnerUnidad, spinnerMarca;
    private EditText codigo, nombre, precio, id;
    private int idsCategoria[], idsMarca[], idsUnidad[];
    Button btn;

    RequestQueue requestQueue;

    private OnFragmentInteractionListener mListener;

    public actividadProducto() {
        // Required empty public constructor
    }

    public static actividadProducto newInstance(String param1, String param2) {
        actividadProducto fragment = new actividadProducto();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_producto, container, false);
        spinnerCategoria = (Spinner) view.findViewById(R.id.categoria_id);
        spinnerUnidad = (Spinner) view.findViewById(R.id.unidad_id);
        spinnerMarca = (Spinner) view.findViewById(R.id.marca_id);

        Conexion conexion = new Conexion(getContext());
        ArrayList<String> listaCategoria = conexion.listaRegistros(Utilidades.TABLA_CATEGORIA, "SI");
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, listaCategoria);
        spinnerCategoria.setAdapter(adapter);

        ArrayList<String> listaMarca = conexion.listaRegistros(Utilidades.TABLA_MARCA, "SI");
        ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, listaMarca);
        spinnerMarca.setAdapter(adapter2);

        ArrayList<String> listaUnidad = conexion.listaRegistros(Utilidades.TABLA_UNIDAD, "SI");
        ArrayAdapter<CharSequence> adapter3 = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, listaUnidad);
        spinnerUnidad.setAdapter(adapter3);

        idsCategoria = conexion.arrayRegistros(Utilidades.TABLA_CATEGORIA, listaCategoria.size());
        idsMarca = conexion.arrayRegistros(Utilidades.TABLA_MARCA, listaMarca.size());
        idsUnidad = conexion.arrayRegistros(Utilidades.TABLA_UNIDAD, listaUnidad.size());

        codigo = (EditText) view.findViewById(R.id.producto_codigo);
        nombre = (EditText) view.findViewById(R.id.producto_nombre);
        precio = (EditText) view.findViewById(R.id.producto_precio);
        id = (EditText) view.findViewById(R.id.producto_id);

        btn = (Button) view.findViewById(R.id.btn_registrar_producto);

        String codigoGet = "";
        String nombreGet = "";
        Double precioGet = 0.0;
        int id_marcaGet = 0;
        int id_categoriaGet = 0;
        int id_unidadGet = 0;
        int idGet = 0;

        if(getArguments() != null) {
            codigoGet = getArguments().getString("codigo", "");
            nombreGet = getArguments().getString("nombre", "");
            precioGet = getArguments().getDouble("precio", 0.0);
            idGet = getArguments().getInt("id", 0);
            id_marcaGet = getArguments().getInt("marca_id", 0);
            id_categoriaGet = getArguments().getInt("categoria_id", 0);
            id_unidadGet = getArguments().getInt("unidad_id", 0);

            //OBTENGO POSICION DE LOS IDS MARCA CATEGORIA Y UNIDAD

            int posMarca = obtenerPosicionArray(id_marcaGet, idsMarca);
            int poscategoria = obtenerPosicionArray(id_categoriaGet, idsCategoria);
            int posUnidad = obtenerPosicionArray(id_unidadGet, idsUnidad);

            nombre.setText(nombreGet);
            codigo.setText(codigoGet);
            precio.setText(String.valueOf(precioGet));
            spinnerUnidad.setSelection(posUnidad);
            spinnerMarca.setSelection(posMarca);
            spinnerCategoria.setSelection(poscategoria);
            id.setText(String.valueOf(idGet));
        } else {
            limpiarFormulario();
            id.setText("0");
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(codigo.getText().toString().equals("") || codigo.getText().toString().equals("") || precio.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "NO PUEDES DEJAR CAMPOS EN BLANCO", Toast.LENGTH_SHORT).show();
                } else {
                    if(spinnerMarca.getSelectedItemPosition()-1 == -1 || spinnerCategoria.getSelectedItemPosition()-1 == -1 || spinnerUnidad.getSelectedItemPosition()-1 == -1) {
                        Toast.makeText(getContext(), "NO PUEDES REGISTRAR, FALTAN DATOS DE CATEGORÍAS, MARCAS O UNIDADES", Toast.LENGTH_SHORT).show();
                    } else {
                        Conexion conexion = new Conexion(getContext());
                        int idMarca = idsMarca[spinnerMarca.getSelectedItemPosition()-1];
                        int idCategoria = idsCategoria[spinnerCategoria.getSelectedItemPosition()-1];
                        int idUnidad = idsUnidad[spinnerUnidad.getSelectedItemPosition()-1];

                        Objeto objeto = new Objeto();
                        objeto.setCodigo(codigo.getText().toString());
                        objeto.setNombre(nombre.getText().toString());
                        objeto.setId(Integer.parseInt(id.getText().toString()));
                        objeto.setPrecio(Double.parseDouble(precio.getText().toString()));
                        objeto.setMarca_id(idMarca);
                        objeto.setCategoria_id(idCategoria);
                        objeto.setUnidad_id(idUnidad);

                        StringRequest stringRequest;

                        String mensaje = "";

                        if(id.getText().toString().equals("0")) {
                            conexion.insertarRegistro(objeto, Utilidades.TABLA_PRODUCTO);
                            stringRequest =  conexion.insertarRegistroWebService(getContext(), Utilidades.WS_INSERTAR, Utilidades.TABLA_PRODUCTO, objeto);
                            limpiarFormulario();
                            mensaje = "INSERTADO";
                        } else {
                            conexion.actualizarRegistro(objeto, Utilidades.TABLA_PRODUCTO);
                            stringRequest =  conexion.actualizarRegistroWebService(getContext(), Utilidades.WS_ACTUALIZAR, Utilidades.TABLA_PRODUCTO, objeto);
                            mensaje = "ACTUALIZADO";
                        }

                        requestQueue = Volley.newRequestQueue(getContext());
                        requestQueue.add(stringRequest);

                        Toast.makeText(getContext(), "PRODUCTO " + mensaje + " CORRECTAMENTE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void limpiarFormulario() {
        nombre.setText("");
        codigo.setText("");
        precio.setText("");
        spinnerCategoria.setSelection(0);
        spinnerMarca.setSelection(0);
        spinnerUnidad.setSelection(0);
        codigo.requestFocus();
    }

    public int obtenerPosicionArray(int id, int[] array) {
        int posicion = 0;
        for (int i = 0; i < array.length; i++) {
            if(array[i] == id) {
                posicion = i;
                break;
            }
        }
        return posicion + 1;
    }
}
