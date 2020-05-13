package com.example.myfirstapplication.actividad;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SugerenciaFragment extends Fragment {

    FloatingActionButton btnEditarDatos;
    MainActivity m = new MainActivity();
    Button btnEnviar;
    EditText texto;

    public SugerenciaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sugerencia, container, false);
        btnEditarDatos = (FloatingActionButton) getActivity().findViewById(R.id.btnEditarUsuario);
        btnEnviar = (Button) view.findViewById(R.id.btnEnviar);
        texto = (EditText) view.findViewById(R.id.i2);
        if(m.obtenerEstadoSesion(getContext())) {
            btnEditarDatos.setVisibility(View.VISIBLE);
        } else {
            btnEditarDatos.setVisibility(View.GONE);
        }
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (texto.getText().toString().equals("")) {
                    mostrarToast("Debes digitar un mensaje.");
                } else {
                    //Enviar mensaje
                    if (isOnlineNet()) {
                        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                        String URL_LOGUEO = Utilidades.WEB_SERVICE + "?accion=INSERTAR_COMENTARIO";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGUEO,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //BASTANTE ÚTIL PARA VER ERRORES
                                        Log.i("tagconvertstr", "[" + response + "]");
                                        try {
                                            JSONObject rptaJson = new JSONObject(response);
                                            String respuesta = rptaJson.getString("1");
                                            if (respuesta.equals("1")) {
                                                mostrarToast("COMENTARIO REGISTRADO CORRECTAMENTE");
                                            } else {
                                                mostrarToast("NO SE PUDO REGISTRAR TU COMENTARIO");
                                            }
                                            UIUtil.hideKeyboard(getActivity());
                                            texto.setText("");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            mostrarToast("Error: " + e.getMessage());
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                mostrarToast("NO TE ENCUENTRAS CONECTADO AL SERVIDOR");
                                Log.d("[asno]", error.getMessage());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> parametros = new HashMap<String, String>();
                                parametros.put("texto", texto.getText().toString());
                                parametros.put("id_usuario", String.valueOf(m.obtenerIdUsuarioEstadoSesion(getActivity())));
                                return parametros;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(getActivity().getApplication());
                        requestQueue.add(stringRequest);
                    } else {
                        mostrarToast("NO ESTÁS CONECTADO A INTERNET");
                    }
                }
            }
        });
        return view;
    }

public Boolean isOnlineNet() {
        try {
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

    public interface OnFragmentInteractionListener {
    }
}
