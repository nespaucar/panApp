package com.example.myfirstapplication.actividad;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.myfirstapplication.MainActivity;
import com.example.myfirstapplication.R;
import com.example.myfirstapplication.utilidades.Utilidades;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    private TextView idcabecera, nombrecabecera;
    private EditText campoCodigo, campoNombre;
    private Button btnLogueo, btnRegistrar, btnlimpiar, btnRecuperar;
    private RegistroFragment vistaregistro;
    private MainActivity vistaPrincipal;
    private BienvenidoFragment vistabienvenido;
    private RecuperarFragment vistarecuperar;

    private OnFragmentInteractionListener mListener;

    private RequestQueue requestQueue;

    private ProgressBar progressBar;

    private MainActivity m;

    private LottieAnimationView animation;

    private ProgressDialog progress;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vistaPrincipal = new MainActivity();
        vistaregistro = new RegistroFragment();
        vistabienvenido = new BienvenidoFragment();
        vistarecuperar = new RecuperarFragment();
        m = new MainActivity();
        progress = new ProgressDialog(getActivity());
        progress.setCancelable(false);
        progress.setMessage("Espere un momento por favor...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        nombrecabecera = (TextView) getActivity().findViewById(R.id.nombrecabecera);
        //progress.show();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //m.guardarEstadoSesion(false);
        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        final EditText usuario = (EditText) view.findViewById(R.id.txtUser);
        final EditText pass = (EditText) view.findViewById(R.id.txtPass);
        final NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        btnRecuperar = (Button) view.findViewById(R.id.btnRecuperar);
        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioFragment(vistarecuperar, "RECUPERAR CLAVE", container, inflater);
            }
        });
        btnlimpiar = (Button) view.findViewById(R.id.btnLimpiar);
        btnlimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usuario.setText("");
                pass.setText("");
                usuario.requestFocus();
            }
        });
        btnRegistrar = (Button) view.findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                if(isOnlineNet()) {
                    progress.dismiss();
                    cambioFragment(vistaregistro, "REGÍSTRATE", container, inflater);
                } else {
                    mostrarToast("NECESITAS CONEXIÓN A INTERNET PARA REGISTRARTE");
                    progress.dismiss();
                }

            }
        });
        btnLogueo = (Button) view.findViewById(R.id.btnLogueo);
        btnLogueo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                if(isOnlineNet()) {
                    requestQueue = Volley.newRequestQueue(getContext());
                    final EditText usuario = (EditText) view.findViewById(R.id.txtUser);
                    final EditText pass = (EditText) view.findViewById(R.id.txtPass);

                    String mensaje = "";
                    if(!usuario.getText().toString().equals("")&&!usuario.getText().toString().equals("")) {
                        String URL_LOGUEO = Utilidades.WEB_SERVICE + "?accion=LOGUEARSE_WEB_SERVICE";
                        //mostrarToast(URL_LOGUEO);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGUEO,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //BASTANTE ÚTIL PARA VER ERRORES
                                        Log.i("tagconvertstr", "["+response+"]");
                                        try {
                                            JSONObject rptaJson = new JSONObject(response);
                                            String respuesta = rptaJson.getString("1");
                                            String respuesta_id = rptaJson.getString("2");
                                            String respuesta_nombre = rptaJson.getString("3");
                                            String respuesta_idpedido = rptaJson.getString("4");
                                            String respuesta_tipousuario = rptaJson.getString("5");
                                            if(respuesta.equals("1")) {
                                                m.guardarEstadoSesion(getContext(), true, Integer.parseInt(respuesta_id), respuesta_nombre, Integer.parseInt(respuesta_idpedido), respuesta_tipousuario);
                                                nombrecabecera.setText(respuesta_nombre);
                                                //OCULTAMOS EL TECLADO

                                                UIUtil.hideKeyboard(getActivity());

                                                //
                                                cambioFragment(vistabienvenido, "BIENVENIDO", container, inflater);
                                                navigationView.getMenu().setGroupVisible(R.id.items_nav, true);
                                                mostrarToast("INICIASTE SESIÓN");
                                                progress.dismiss();
                                                //Toast.makeText(getActivity().getApplication(), "INICIASTE SESIÓN", Toast.LENGTH_SHORT).show();
                                            } else {
                                                mostrarToast("NO SE ENCUENTRA EN LA BASE DE DATOS");
                                                progress.dismiss();
                                                //Toast.makeText(getActivity().getApplication(), "NO SE ENCUENTRA EN LA BASE DE DATOS", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            mostrarToast("Error: " + e.getMessage());
                                            progress.dismiss();
                                            //Toast.makeText(getActivity().getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mostrarToast("NO TE ENCUENTRAS CONECTADO AL SERVIDOR");
                                progress.dismiss();
                                //Toast.makeText(getActivity().getApplication(), "NO TE ENCUENTRAS CONECTADO AL SERVIDOR", Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> parametros = new HashMap<String, String>();
                                parametros.put("user", usuario.getText().toString());
                                parametros.put("pass", pass.getText().toString());
                                return parametros;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(getActivity().getApplication());
                        requestQueue.add(stringRequest);
                    } else {
                        usuario.requestFocus();
                        mostrarToast("ASEGÚRATE DE LLENAR TUS DATOS");
                        progress.dismiss();
                        //Toast.makeText(getActivity().getApplication(), "ASEGÚRATE DE LLENAR TUS DATOS", Toast.LENGTH_SHORT).show();
                    }
                    //PRUEBA INGRESO DIRECTO
                    //cambioFragment(vistabienvenido, "BIENVENIDO", container, inflater);
                    //navigationView.getMenu().setGroupVisible(R.id.items_nav, true);
                    //mostrarToast("INICIASTE SESIÓN");
                } else {
                    mostrarToast("NO ESTÁS CONECTADO A INTERNET");
                    progress.dismiss();
                }


            }
        });
        if(m.obtenerEstadoSesion(getContext())) {
            cambioFragment(vistabienvenido, "BIENVENIDO", container, inflater);
            //cambioFragment(new CalificarWebFragment(), "CALIFICAR", container, inflater);
            //mostrarToast(m.obtenerIdUsuarioEstadoSesion(getContext())+"");
            navigationView.getMenu().setGroupVisible(R.id.items_nav, true);
            nombrecabecera.setText(m.obtenerNombreUsuarioEstadoSesion(getContext()));
        }
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

    public Boolean isOnlineNet() {
        try {
            /*Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");
            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;*/
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            boolean connect = false;

            if (networkInfo != null && networkInfo.isConnected()) {
                connect = true;
            }
            return connect;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
