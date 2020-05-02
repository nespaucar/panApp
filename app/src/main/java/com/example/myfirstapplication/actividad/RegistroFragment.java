package com.example.myfirstapplication.actividad;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroFragment extends Fragment {

    private TextView txtHide, idcabecera, fechaNacimiento, direccion, nombres, latitud, longitud, correo, usuario, dni, password, referencia, celular;

    private Button btnVolverse, btnRegistrar;

    private OnFragmentInteractionListener mListener;

    private LoginFragment vistalogin;

    private LocationManager mlocManager;

    private Spinner spinerSexo;

    private String mes;
    private String anno;
    private String dia;
    private DatePickerDialog fechaNacimientoPicker;

    private LocationListener Localizacion;

    public ProgressDialog progress;

    private RequestQueue requestQueue;

    private FloatingActionButton btnEditarDatos;

    private MainActivity m;

    public RegistroFragment() {
        // Required empty public constructor
    }

    public static RegistroFragment newInstance(String param1, String param2) {
        RegistroFragment fragment = new RegistroFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(getActivity());
        progress.setCancelable(false);
        progress.setMessage("Espere un momento por favor...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();
        m = new MainActivity();
        vistalogin = new LoginFragment();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_registro, container, false);
        btnEditarDatos = (FloatingActionButton) getActivity().findViewById(R.id.btnEditarUsuario);
        btnEditarDatos.setVisibility(View.GONE);
        // Inflate the layout for this fragment
        txtHide = (TextView) getActivity().findViewById(R.id.hide);
        txtHide.setText("REG");
        spinerSexo = (Spinner) view.findViewById(R.id.c_sexo);
        //EDITTEXTS
        nombres = (EditText) view.findViewById(R.id.c_nombres);
        dni = (EditText) view.findViewById(R.id.c_dni);
        fechaNacimiento = (EditText) view.findViewById(R.id.c_fnacimiento);
        usuario = (EditText) view.findViewById(R.id.c_usuario);
        password = (EditText) view.findViewById(R.id.c_clave);
        direccion = (EditText) view.findViewById(R.id.c_direccion);
        referencia = (EditText) view.findViewById(R.id.c_referencia);
        celular = (EditText) view.findViewById(R.id.c_celular);
        correo = (EditText) view.findViewById(R.id.c_correo);
        longitud = (EditText) view.findViewById(R.id.longitud);
        latitud = (EditText) view.findViewById(R.id.latitud);
        //FINEDITTEXTS
        String[] valores = {"MASCULINO","FEMENINO"};
        spinerSexo.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valores));
        direccion = (TextView) view.findViewById(R.id.c_direccion);
        direccion.setKeyListener(null);
        btnVolverse = (Button) view.findViewById(R.id.btn_volverse);
        btnRegistrar = (Button) view.findViewById(R.id.btn_registrarse);
        btnVolverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtHide.setText("REG_NO");
                //pararServicio();
                cambioFragment(vistalogin, "IDENTIFÍCATE", container, inflater);
            }
        });
        fechaNacimiento = (TextView) view.findViewById(R.id.c_fnacimiento);
        fechaNacimiento.setKeyListener(null);
        usuario.setKeyListener(null);
        fechaNacimiento.requestFocus();
        setearFecha("1", "", "", "");
        fechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int anno2 = Integer.parseInt(anno);
                int mes2 = Integer.parseInt(mes) - 1;
                int dia2 = Integer.parseInt(dia);
                fechaNacimientoPicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        setearFecha("2", String.format("%02d", dayOfMonth),
                                String.format("%02d", (month + 1)), String.valueOf(year));
                    }
                }, anno2, mes2, dia2);
                fechaNacimientoPicker.setCancelable(false);
                fechaNacimientoPicker.setCanceledOnTouchOutside(false);
                fechaNacimientoPicker.show();
                //dialog3.show();
            }
        });
        Localizacion = new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
                // debido a la deteccion de un cambio de ubicacion
                loc.getLatitude();
                loc.getLongitude();
                String Text = "Lat = "+ loc.getLatitude() + " Long = " + loc.getLongitude();
                //AQUI PODEMOS SETEAR LA LONG Y LAT
                //mostrarToast(Text);
                if (loc!=null) {
                    if(txtHide.getText().toString().equals("REG")) {
                        setLocation(loc);
                    }
                }
            }
            @Override
            public void onProviderDisabled(String provider) {
                // Este metodo se ejecuta cuando el GPS es desactivado
                //mlocManager.removeUpdates(mListener);
                mostrarToast("GPS Desactivado");
            }
            @Override
            public void onProviderEnabled(String provider) {
                // Este metodo se ejecuta cuando el GPS es activado
                mostrarToast("GPS Activado");
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        Log.d("debug", "LocationProvider.AVAILABLE");
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                        break;
                }
            }
        };
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                if(isOnlineNet()) {
                    requestQueue = Volley.newRequestQueue(getContext());
                    String mensaje = "";
                    boolean enviar = true;
                    if(nombres.getText().toString().equals("")) {
                        mostrarToast("INGRESA TU NOMBRE");
                        nombres.requestFocus();
                        enviar=false;
                    } else {
                        if(dni.getText().toString().equals("")) {
                            mostrarToast("INGRESA TU DNI");
                            dni.requestFocus();
                            enviar=false;
                        } else {
                            if(fechaNacimiento.getText().toString().equals("")) {
                                mostrarToast("INGRESA TU FECHA DE NACIMIENTO");
                                correo.requestFocus();
                                enviar=false;
                            } else {
                                if(usuario.getText().toString().equals("")) {
                                    mostrarToast("INGRESA TU USUARIO");
                                    usuario.requestFocus();
                                    enviar=false;
                                } else {
                                    if(password.getText().toString().equals("")) {
                                        mostrarToast("INGRESA TU CLAVE");
                                        password.requestFocus();
                                        enviar=false;
                                    } else {
                                        if(direccion.getText().toString().equals("")) {
                                            mostrarToast("INGRESA TU DIRECCIÓN");
                                            direccion.requestFocus();
                                            enviar=false;
                                        } else {
                                            if(referencia.getText().toString().equals("")) {
                                                mostrarToast("INGRESA TU REFERENCIA");
                                                referencia.requestFocus();
                                                enviar=false;
                                            } else {
                                                if(celular.getText().toString().equals("")) {
                                                    mostrarToast("INGRESA TU CELULAR");
                                                    celular.requestFocus();enviar=false;
                                                } else {
                                                    if (correo.getText().toString().equals("")) {
                                                        mostrarToast("INGRESA TU CORREO");
                                                        correo.requestFocus();
                                                        enviar = false;
                                                    } else {
                                                        // Patrón para validar el email
                                                        Pattern pattern = Pattern
                                                                .compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                                                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

                                                        // El email a validar
                                                        Matcher mather = pattern.matcher(correo.getText().toString());

                                                        if (!mather.find()) {
                                                            mostrarToast("CORREO INVÁLIDO");
                                                            correo.requestFocus();
                                                            enviar = false;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(enviar) {
                        String URL_REGISTRO = Utilidades.WEB_SERVICE + "?accion=REGISTRAR_CLIENTE";
                        //mostrarToast(URL_LOGUEO);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRO,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //BASTANTE ÚTIL PARA VER ERRORES
                                        Log.i("tagconvertstr", "["+response+"]");
                                        try {
                                            JSONObject rptaJson = new JSONObject(response);
                                            String respuesta = rptaJson.getString("1");
                                            if(respuesta.equals("1")) {
                                                mostrarToast("EL DNI YA EXISTE");
                                                progress.dismiss();
                                            } else if(respuesta.equals("2")) {
                                                mostrarToast("EL USUARIO YA EXISTE");
                                                progress.dismiss();
                                            } else if(respuesta.equals("3")) {
                                                mostrarToast("USUARIO INSERTADO CORRECTAMENTE");
                                                mostrarToast("PUEDES INICIAR SESIÓN");
                                                //OCULTAMOS EL TECLADO

                                                UIUtil.hideKeyboard(getActivity());

                                                //
                                                txtHide.setText("REG_NO");
                                                progress.dismiss();
                                                pararServicio();
                                                cambioFragment(vistalogin, "IDENTIFÍCATE", container, inflater);
                                            } else if(respuesta.equals("4")) {
                                                mostrarToast("NO SE PUDO INGRESAR USUARIO");
                                                progress.dismiss();
                                            } else if(respuesta.equals("5")) {
                                                mostrarToast("EL CORREO YA EXISTE");
                                                progress.dismiss();
                                            } else if(respuesta.equals("6")) {
                                                mostrarToast("DATOS MODIFICADOS");
                                                //OCULTAMOS EL TECLADO

                                                UIUtil.hideKeyboard(getActivity());

                                                //
                                                txtHide.setText("REG_NO");
                                                progress.dismiss();
                                                m.guardarEstadoSesion(getContext(), true, m.obtenerIdUsuarioEstadoSesion(getContext()), nombres.getText().toString(), m.obtenerIdPedidoSesion(getContext()), m.obtenerTipoUsuarioEstadoSesion(getContext()));
                                                TextView nombrecabecera = getActivity().findViewById(R.id.nombrecabecera);
                                                nombrecabecera.setText(nombres.getText().toString());
                                                cambioFragment(vistalogin, "BIENVENIDO", container, inflater);
                                            } else if(respuesta.equals("7")) {
                                                mostrarToast("NO SE PUDO MODIFICAR EL USUARIO");
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
                                parametros.put("id_usuario", "" + m.obtenerIdUsuarioEstadoSesion(getContext()));
                                parametros.put("nombres", nombres.getText().toString());
                                parametros.put("dni", dni.getText().toString());
                                parametros.put("clave", password.getText().toString());
                                parametros.put("telefono", celular.getText().toString());
                                parametros.put("direccion", direccion.getText().toString());
                                parametros.put("latitud", latitud.getText().toString());
                                parametros.put("longitud", longitud.getText().toString());
                                parametros.put("referencia", referencia.getText().toString());
                                parametros.put("fec_nac", fechaNacimiento.getText().toString());
                                parametros.put("correo", correo.getText().toString());
                                parametros.put("sexo", spinerSexo.getSelectedItem().toString());
                                return parametros;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(getActivity().getApplication());
                        requestQueue.add(stringRequest);
                    }
                    progress.dismiss();
                } else {
                    mostrarToast("NO ESTÁS CONECTADO A INTERNET");
                    progress.dismiss();
                }
                //////////////////////////////
            }
        });
        dni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usuario.setText(dni.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if(m.obtenerIdUsuarioEstadoSesion(getContext())!=0) {
            llenarDatosEditarUsuario();
        } else {
            //PERMISO DE UBICACION
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 5000);
            } else {
                locationStart();
            }
        }
        return view;
    }

    private void llenarDatosEditarUsuario() {
        {
            String URL_REGISTRO = Utilidades.WEB_SERVICE + "?accion=DATOS_USUARIO";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRO,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("tagconvertstr", "["+response+"]");
                            try {
                                JSONArray rptaJson = new JSONArray(response);
                                String respuesta = rptaJson.getJSONObject(0).getString("1");
                                String nombrec = rptaJson.getJSONObject(0).getString("2");
                                String dnic = rptaJson.getJSONObject(0).getString("3");
                                String nacimientoc = rptaJson.getJSONObject(0).getString("4");
                                String passc = rptaJson.getJSONObject(0).getString("5");
                                String direccionc = rptaJson.getJSONObject(0).getString("6");
                                String latitudc = rptaJson.getJSONObject(0).getString("7");
                                String longitudc = rptaJson.getJSONObject(0).getString("8");
                                String referenciac = rptaJson.getJSONObject(0).getString("9");
                                String telefonoc = rptaJson.getJSONObject(0).getString("10");
                                String sexoc = rptaJson.getJSONObject(0).getString("11");
                                String correoc = rptaJson.getJSONObject(0).getString("12");
                                if(respuesta.equals("1")) {
                                    nombres.setText(nombrec);
                                    dni.setText(dnic);
                                    fechaNacimiento.setText(nacimientoc);
                                    password.setText(passc);
                                    direccion.setText(direccionc);
                                    latitud.setText(latitudc);
                                    longitud.setText(longitudc);
                                    referencia.setText(referenciac);
                                    celular.setText(telefonoc);
                                    int optionSex = 0;
                                    if(sexoc.equals("FEMENINO")) {
                                        optionSex = 1;
                                    }
                                    btnVolverse.setText("REGRESAR");
                                    btnRegistrar.setText("MODIFICAR");
                                    spinerSexo.setSelection(optionSex);
                                    correo.setText(correoc);
                                    dni.setKeyListener(null);
                                    correo.setKeyListener(null);
                                    progress.dismiss();
                                }  else {
                                    mostrarToast("ERROR AL OBTENER LOS DATOS DEL USUARIO");
                                    progress.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mostrarToast("Error: " + e.getMessage());
                                progress.dismiss();
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
                    parametros.put("id_usuario", "" + m.obtenerIdUsuarioEstadoSesion(getContext()));
                    return parametros;
                }
            };
            requestQueue = Volley.newRequestQueue(getActivity().getApplication());
            requestQueue.add(stringRequest);
        }
    }

    private void setearFecha(String tipo, String dia2, String mes2, String anno2) {
        if (tipo.equals("1")) {
            SimpleDateFormat dateFormatDia = new SimpleDateFormat("dd", Locale.ENGLISH);
            SimpleDateFormat dateFormatMes = new SimpleDateFormat("MM", Locale.ENGLISH);
            SimpleDateFormat dateFormatAnno = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            anno = dateFormatAnno.format(new Date());
            mes = dateFormatMes.format(new Date());
            dia = dateFormatDia.format(new Date());
        } else {
            anno = anno2;
            mes = mes2;
            dia = dia2;
        }
        fechaNacimiento.setText(dia + "/" + mes + "/" + anno);
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

        getActivity().closeContextMenu();

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

    private void locationStart() {
        mlocManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Localizacion);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Localizacion);
        //mensaje1.setText("Localizacion agregada");
        direccion.setText("");
    }

    private void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if(loc!=null) {
            if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
                try {
                    if(txtHide.getText().toString().equals("REG")) {
                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                        if(geocoder!=null) {
                            List<Address> list = geocoder.getFromLocation(
                                    loc.getLatitude(), loc.getLongitude(), 1);
                            if (!list.isEmpty()) {
                                Address DirCalle = list.get(0);
                                direccion.setText(DirCalle.getAddressLine(0));
                                longitud.setText(""+loc.getLongitude());
                                latitud.setText(""+loc.getLatitude());
                                pararServicio();
                            }
                        }
                    }
                    progress.dismiss();

                } catch (IOException e) {
                    e.printStackTrace();
                    progress.dismiss();
                }
            }
            progress.dismiss();
        }
        progress.dismiss();
    }

    public void pararServicio() {
        mlocManager.removeUpdates(Localizacion);
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
