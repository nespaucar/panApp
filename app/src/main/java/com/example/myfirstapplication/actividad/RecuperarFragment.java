package com.example.myfirstapplication.actividad;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecuperarFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private MainActivity m;

    private Button btn_recuperar, btn_volverse_recuperar;

    private EditText ic_correo_recuperacion;

    private LoginFragment vistalogin;

    private TextView idcabecera;

    private ProgressDialog progress;

    private RequestQueue requestQueue;

    private LocationManager mlocManager;

    public RecuperarFragment() {
        // Required empty public constructor
    }

    public static RecuperarFragment newInstance(String param1, String param2) {
        RecuperarFragment fragment = new RecuperarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vistalogin = new LoginFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recuperar, container, false);
        m = new MainActivity();
        progress = new ProgressDialog(getActivity());
        btn_recuperar = (Button) view.findViewById(R.id.btn_recuperar);
        btn_volverse_recuperar = (Button) view.findViewById(R.id.btn_volverse_recuperar);
        ic_correo_recuperacion = (EditText) view.findViewById(R.id.ic_correo_recuperacion);
        ic_correo_recuperacion.requestFocus();
        btn_volverse_recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioFragment(vistalogin, "BIENVENIDO", container, inflater);
                UIUtil.hideKeyboard(getActivity());
            }
        });
        btn_recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                if(isOnlineNet()) {
                    requestQueue = Volley.newRequestQueue(getContext());
                    String mensaje = "";
                    if(!ic_correo_recuperacion.getText().toString().equals("")) {
                        Pattern pattern = Pattern
                                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

                        // El email a validar
                        Matcher mather = pattern.matcher(ic_correo_recuperacion.getText().toString());

                        if (!mather.find()) {
                            mostrarToast("CORREO INVÁLIDO");
                            ic_correo_recuperacion.setText("");
                            ic_correo_recuperacion.requestFocus();
                            progress.dismiss();
                        } else {
                            String URL_LOGUEO = Utilidades.WEB_SERVICE + "?accion=RECUPERAR_CLAVE";
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
                                                if(respuesta.equals("1")) {
                                                    UIUtil.hideKeyboard(getActivity());
                                                    cambioFragment(vistalogin, "BIENVENIDO", container, inflater);
                                                    mostrarToast("CLAVE ENVIADA AL CORREO " + ic_correo_recuperacion.getText().toString() + ". REVISA TU SPAM");
                                                    progress.dismiss();
                                                } else {
                                                    mostrarToast("NO SE ENCUENTRA EN LA BASE DE DATOS. VUELVA A INTENTAR");
                                                    ic_correo_recuperacion.setText("");
                                                    ic_correo_recuperacion.requestFocus();
                                                    progress.dismiss();
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
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> parametros = new HashMap<String, String>();
                                    parametros.put("correo", ic_correo_recuperacion.getText().toString());
                                    return parametros;
                                }
                            };
                            requestQueue = Volley.newRequestQueue(getActivity().getApplication());
                            requestQueue.add(stringRequest);
                        }
                    } else {
                        ic_correo_recuperacion.requestFocus();
                        mostrarToast("ASEGÚRATE DE PONER TU CORREO");
                        progress.dismiss();
                    }
                } else {
                    mostrarToast("NO ESTÁS CONECTADO A INTERNET");
                    progress.dismiss();
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
