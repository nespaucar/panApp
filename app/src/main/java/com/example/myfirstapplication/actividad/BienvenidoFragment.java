package com.example.myfirstapplication.actividad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myfirstapplication.MainActivity;
import com.example.myfirstapplication.R;
import com.example.myfirstapplication.proceso.CatalogoProductosFragment;
import com.example.myfirstapplication.proceso.MisPedidosFragment;

public class BienvenidoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private LoginFragment vistalogin;
    private MainActivity vistaprincipal;
    private CatalogoProductosFragment vistaCatalogo;
    private MisPedidosFragment vistaMisPedidos;
    private RutaWebFragment rutaWebFragment;

    private int idsCategoria[];

    private Button btnSalir, btnCatalogo, btnMisPedidos, btnRuta;

    private TextView idcabecera, nombrecabecera;

    private FloatingActionButton btnEditarDatos;

    public BienvenidoFragment() {
        // Required empty public constructor
    }

    public static BienvenidoFragment newInstance(String param1, String param2) {
        BienvenidoFragment fragment = new BienvenidoFragment();
        return fragment;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vistalogin = new LoginFragment();
        vistaprincipal = new MainActivity();
        vistaCatalogo = new CatalogoProductosFragment();
        rutaWebFragment = new RutaWebFragment();
        vistaMisPedidos = new MisPedidosFragment();
        nombrecabecera = (TextView) getActivity().findViewById(R.id.nombrecabecera);
        btnEditarDatos = (FloatingActionButton) getActivity().findViewById(R.id.btnEditarUsuario);
        btnEditarDatos.setVisibility(View.VISIBLE);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_bienvenido, container, false);

        btnSalir = (Button) view.findViewById(R.id.btnSalir);
        btnCatalogo = (Button) view.findViewById(R.id.btnCatalogo);
        btnMisPedidos = (Button) view.findViewById(R.id.btnMisPedidos);
        btnRuta = (Button) view.findViewById(R.id.btnRuta);

        if(vistaprincipal.obtenerTipoUsuarioEstadoSesion(getContext()).equals("R")) {
            btnCatalogo.setVisibility(View.GONE);
            btnMisPedidos.setVisibility(View.GONE);
        } else if(vistaprincipal.obtenerTipoUsuarioEstadoSesion(getContext()).equals("C")) {
            btnRuta.setVisibility(View.GONE);
        }
        final NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.getMenu().setGroupVisible(R.id.items_nav, true);
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity m = new MainActivity();
                m.guardarEstadoSesion(getContext(), false, 0, "", 0, "");
                nombrecabecera.setText("");
                cambioFragment(vistalogin, "IDENTIFÍCATE", container, inflater);
                navigationView.getMenu().setGroupVisible(R.id.items_nav, false);
                mostrarToast("CERRASTE SESIÓN");
                btnEditarDatos.setVisibility(View.GONE);
                //Toast.makeText(getActivity().getApplication(), "CERRASTE SESIÓN", Toast.LENGTH_SHORT).show();
            }
        });
        btnCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioFragment(vistaCatalogo, "CATÁLOGO", container, inflater);
            }
        });
        btnMisPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioFragment(vistaMisPedidos, "MIS PEDIDOS", container, inflater);
            }
        });
        btnRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioFragment(rutaWebFragment, "RUTA DE PEDIDOS", container, inflater);
            }
        });
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    public void cambioFragment(Fragment object, String cabecera, ViewGroup container, LayoutInflater inflater) {
        idcabecera = (TextView) getActivity().findViewById(R.id.idcabecera);

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
        //Toast.makeText(getContext(), cabecera, Toast.LENGTH_SHORT).show();
    }

    public void mostrarToast(String mensaje) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) getActivity().findViewById(R.id.toast));

        TextView textview = layout.findViewById(R.id.texttoast);
        textview.setText(mensaje);

        ImageView image = layout.findViewById(R.id.imagetoast);

        Glide.with(getActivity().getBaseContext()).load(R.drawable.panadero);
        Toast toast = new Toast(getActivity().getApplicationContext());

        toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 500);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }
}
