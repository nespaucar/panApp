package com.example.myfirstapplication.actividad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.myfirstapplication.MainActivity;
import com.example.myfirstapplication.R;

public class RutaWebFragment extends Fragment {

    WebView web;

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
        web.loadUrl("http://martinampuero.com/sigre/SimularRecorrido.php?idrepartidor=" + MainActivity.obtenerIdUsuarioEstadoSesionStatic(getContext()));

        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverriceUrlLoading(WebView webView, String url) {
                return false;
            }
        });
        return view;
    }
}
