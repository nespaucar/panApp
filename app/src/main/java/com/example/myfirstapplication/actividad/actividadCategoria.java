package com.example.myfirstapplication.actividad;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myfirstapplication.R;
import com.example.myfirstapplication.beans.Objeto;
import com.example.myfirstapplication.db.Conexion;
import com.example.myfirstapplication.utilidades.Utilidades;

public class actividadCategoria extends Fragment {

    private EditText codigo, nombre, id;
    Button btn;

    private RequestQueue requestQueue;

    private OnFragmentInteractionListener mListener;

    public actividadCategoria() {
        // Required empty public constructor
    }

    public static actividadCategoria newInstance(String param1, String param2) {
        actividadCategoria fragment = new actividadCategoria();
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
        View view = inflater.inflate(R.layout.fragment_categoria, container, false);
        codigo = (EditText) view.findViewById(R.id.categoria_codigo);
        nombre = (EditText) view.findViewById(R.id.categoria_nombre);
        id = (EditText) view.findViewById(R.id.categoria_id);
        btn = (Button) view.findViewById(R.id.btn_registrar_categoria);

        String codigoGet = "";
        String nombreGet = "";
        int idGet = 0;

        if(getArguments() != null) {
            codigoGet = getArguments().getString("codigo", "");
            nombreGet = getArguments().getString("nombre", "");
            idGet = getArguments().getInt("id", 0);
            nombre.setText(nombreGet);
            codigo.setText(codigoGet);
            id.setText(String.valueOf(idGet));
        } else {
            limpiarFormulario();
            id.setText("0");
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Conexion conexion = new Conexion(getContext());

                Objeto objeto = new Objeto();
                objeto.setId(Integer.parseInt(id.getText().toString()));
                objeto.setCodigo(codigo.getText().toString());
                objeto.setNombre(nombre.getText().toString());

                StringRequest stringRequest;

                String mensaje = "";

                if(!codigo.getText().toString().equals("") || !codigo.getText().toString().equals("")) {
                    if(id.getText().toString().equals("0")) {
                        conexion.insertarRegistro(objeto, Utilidades.TABLA_CATEGORIA);
                        stringRequest =  conexion.insertarRegistroWebService(getContext(), Utilidades.WS_INSERTAR, Utilidades.TABLA_CATEGORIA, objeto);
                        limpiarFormulario();
                        mensaje = "INSERTADO";
                    } else {
                        conexion.actualizarRegistro(objeto, Utilidades.TABLA_CATEGORIA);
                        stringRequest =  conexion.actualizarRegistroWebService(getContext(), Utilidades.WS_ACTUALIZAR, Utilidades.TABLA_CATEGORIA, objeto);
                        mensaje = "ACTUALIZADO";
                    }
                    requestQueue = Volley.newRequestQueue(getContext());
                    requestQueue.add(stringRequest);
                    mensaje = "CATEGORÍA " + mensaje + " CORRECTAMENTE";
                } else {
                    mensaje = "NO PUEDES DEJAR CAMPOS VACÍOS";
                }

                Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
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
        id.setText("0");
        nombre.setText("");
        codigo.setText("");
        codigo.requestFocus();
    }
}
