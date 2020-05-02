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

public class actividadListaUnidad extends Fragment {

    private ListView lista;

    private OnFragmentInteractionListener mListener;

    private int idsUnidad[];
    private actividadUnidad actividadUnidad;

    public actividadListaUnidad() {
        // Required empty public constructor
    }

    public static actividadListaUnidad newInstance(String param1, String param2) {
        actividadListaUnidad fragment = new actividadListaUnidad();
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
        View view = inflater.inflate(R.layout.fragment_actividad_lista_unidad, container, false);
        lista = (ListView) view.findViewById(R.id.listaUnidad);

        Conexion conexion = new Conexion(getContext());
        ArrayList<String> listaUnidad = conexion.listaRegistros(Utilidades.TABLA_UNIDAD, "NO");
        idsUnidad = conexion.arrayRegistros(Utilidades.TABLA_UNIDAD, listaUnidad.size());
        ArrayAdapter<String> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, listaUnidad);
        lista.setAdapter(adapter);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idUnidad = idsUnidad[position];

                //OBTENEMOS EL OBJETO UNIDAD
                Conexion conexion = new Conexion(getContext());

                Objeto objeto = conexion.obtenerRegistro(idUnidad, Utilidades.TABLA_UNIDAD);

                Toast.makeText(getContext(), String.valueOf(idUnidad), Toast.LENGTH_SHORT).show();

                actividadUnidad = new actividadUnidad();

                if (getActivity().getSupportFragmentManager().findFragmentById(R.id.contenedordinamico) != null) {
                    getActivity()
                            .getSupportFragmentManager()
                            .beginTransaction().
                            remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.contenedordinamico)).commit();
                }
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contenedordinamico, actividadUnidad)
                        .commit();

                Bundle bundle = new Bundle();
                bundle.putInt("id", objeto.getId());
                bundle.putString("codigo", objeto.getCodigo());
                bundle.putString("nombre", objeto.getNombre());

                actividadUnidad.setArguments(bundle);
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
