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

public class actividadListaCategoria extends Fragment {

    private ListView lista;

    private OnFragmentInteractionListener mListener;

    private int idsCategoria[];
    private actividadCategoria actividadCategoria;

    public actividadListaCategoria() {
        // Required empty public constructor
    }

    public static actividadListaCategoria newInstance(String param1, String param2) {
        actividadListaCategoria fragment = new actividadListaCategoria();
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
        View view = inflater.inflate(R.layout.fragment_actividad_lista_categoria, container, false);
        lista = (ListView) view.findViewById(R.id.listaCategoria);

        Conexion conexion = new Conexion(getContext());
        ArrayList<String> listaCategoria = conexion.listaRegistros(Utilidades.TABLA_CATEGORIA, "NO");
        idsCategoria = conexion.arrayRegistros(Utilidades.TABLA_CATEGORIA, listaCategoria.size());
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listaCategoria);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idCategoria = idsCategoria[position];

                //OBTENEMOS EL OBJETO CATEGORIA
                Conexion conexion = new Conexion(getContext());

                Objeto objeto = conexion.obtenerRegistro(idCategoria, Utilidades.TABLA_CATEGORIA);

                Toast.makeText(getContext(), String.valueOf(idCategoria), Toast.LENGTH_SHORT).show();

                actividadCategoria = new actividadCategoria();

                if (getActivity().getSupportFragmentManager().findFragmentById(R.id.contenedordinamico) != null) {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction().
                            remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.contenedordinamico)).commit();
                }
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contenedordinamico, actividadCategoria)
                        .commit();

                Bundle bundle = new Bundle();
                bundle.putInt("id", objeto.getId());
                bundle.putString("codigo", objeto.getCodigo());
                bundle.putString("nombre", objeto.getNombre());

                actividadCategoria.setArguments(bundle);
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
