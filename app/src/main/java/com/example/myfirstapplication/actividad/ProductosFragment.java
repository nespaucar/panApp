package com.example.myfirstapplication.actividad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myfirstapplication.MainActivity;
import com.example.myfirstapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductosFragment extends Fragment {

    FloatingActionButton btnEditarDatos;
    MainActivity m = new MainActivity();

    public ProductosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_productos, container, false);
        btnEditarDatos = (FloatingActionButton) getActivity().findViewById(R.id.btnEditarUsuario);
        if(m.obtenerEstadoSesion(getContext())) {
            btnEditarDatos.setVisibility(View.VISIBLE);
        } else {
            btnEditarDatos.setVisibility(View.GONE);
        }
        return view;
    }

    public interface OnFragmentInteractionListener {
    }
}
