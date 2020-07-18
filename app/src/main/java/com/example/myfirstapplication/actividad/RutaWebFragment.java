package com.example.myfirstapplication.actividad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.example.myfirstapplication.MainActivity;
import com.example.myfirstapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RutaWebFragment extends Fragment {

    WebView web;
    FloatingActionButton btnVolver, btnEditarDatos;

    public RutaWebFragment() {
        // Required empty public constructor
    }

    public static RutaWebFragment newInstance(String param1, String param2) {
        RutaWebFragment fragment = new RutaWebFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ruta_web, container, false);

        web = (WebView) view.findViewById(R.id.web_ruta);
        web.getSettings().setJavaScriptEnabled(true);

        btnEditarDatos = (FloatingActionButton) getActivity().findViewById(R.id.btnEditarUsuario);
        btnEditarDatos.setVisibility(View.GONE);

        btnVolver = (FloatingActionButton) view.findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioFragment(new BienvenidoFragment(), "BIENVENIDO");
            }
        });

        web.loadUrl("http://161.35.122.212/mishka/SimularRecorrido.php?idrepartidor=" + MainActivity.obtenerIdUsuarioEstadoSesionStatic(getContext()) + "&hora=6");

        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverriceUrlLoading(WebView webView, String url) {
                return false;
            }
        });
        return view;
    }

    public void cambioFragment(Fragment object, String cabecera) {
        TextView idcabecera = (TextView) getActivity().findViewById(R.id.idcabecera);

        //CAMBIO DE FRAGMENT

        if (getActivity().getSupportFragmentManager().findFragmentById(R.id.contenedordinamico) != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction().
                    remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.contenedordinamico)).commit();
        }
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedordinamico, object)
                .commit();

        idcabecera.setText(cabecera);
    }
}
