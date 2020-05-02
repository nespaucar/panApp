package com.example.myfirstapplication.actividad;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.myfirstapplication.R;
import com.example.myfirstapplication.beans.Objeto;
import com.example.myfirstapplication.db.Conexion;
import com.example.myfirstapplication.utilidades.Utilidades;

import java.util.ArrayList;

public class actividadListaMarca extends Fragment {

    private ListView lista;

    private OnFragmentInteractionListener mListener;

    private int idsMarca[];
    private actividadMarca actividadMarca;

    public actividadListaMarca() {
        // Required empty public constructor
    }

    public static actividadListaMarca newInstance(String param1, String param2) {
        actividadListaMarca fragment = new actividadListaMarca();
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
        View view = inflater.inflate(R.layout.fragment_actividad_lista_marca, container, false);
        lista = (ListView) view.findViewById(R.id.listaMarca);

        Conexion conexion = new Conexion(getContext());
        ArrayList<String> listaMarca = conexion.listaRegistros(Utilidades.TABLA_MARCA, "NO");
        idsMarca = conexion.arrayRegistros(Utilidades.TABLA_MARCA, listaMarca.size());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listaMarca);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idMarca = idsMarca[position];

                //OBTENEMOS EL OBJETO MARCA
                Conexion conexion = new Conexion(getContext());

                Objeto objeto = conexion.obtenerRegistro(idMarca, Utilidades.TABLA_MARCA);

                Toast.makeText(getContext(), String.valueOf(idMarca), Toast.LENGTH_SHORT).show();

                actividadMarca = new actividadMarca();

                if (getActivity().getSupportFragmentManager().findFragmentById(R.id.contenedordinamico) != null) {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction().
                            remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.contenedordinamico)).commit();
                }
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contenedordinamico, actividadMarca)
                        .commit();

                Bundle bundle = new Bundle();
                bundle.putInt("id", objeto.getId());
                bundle.putString("codigo", objeto.getCodigo());
                bundle.putString("nombre", objeto.getNombre());

                actividadMarca.setArguments(bundle);
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
}
