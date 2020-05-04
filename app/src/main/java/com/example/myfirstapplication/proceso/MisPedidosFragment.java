package com.example.myfirstapplication.proceso;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cz.msebera.android.httpclient.Header;

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
import com.example.myfirstapplication.actividad.BienvenidoFragment;
import com.example.myfirstapplication.beans.AdapterMiPedido;
import com.example.myfirstapplication.beans.AdapterProductosPedido;
import com.example.myfirstapplication.beans.Pedido;
import com.example.myfirstapplication.beans.ProductoVo;
import com.example.myfirstapplication.utilidades.Utilidades;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MisPedidosFragment extends Fragment {

    private String cdireccion, clatitud, clongitud;

    private TextView totalProductoPedido, fechaPedidoMisPedidos, estadoMiPedido;

    private Float totalproductospedidof;

    private Integer idPedido;

    private ArrayList<Pedido> listMisPedidos;

    private RecyclerView recyclerMisPedidos;

    private BienvenidoFragment vistaBienvenido;

    private Button btnRegresar, btnHacerPedido;

    private TextView idcabecera, fechaPedido, fecha2Pedido, btnCloseEditar, txtDireccion, tituloPedido;

    private OnFragmentInteractionListener mListener;

    private String mes;
    private String anno;
    private String dia;
    private DatePickerDialog fechaPedidoPicker;

    private ProgressDialog progress;

    private AsyncHttpClient cliente;

    private Spinner spinnerEstado, spinnerTipo, spinnerTurno, spinnerHora;

    private ArrayList<ProductoVo> productospedido;

    private String fecha_hoy;

    private MainActivity m;

    private AdapterMiPedido adapterMiPedido;

    private CheckBox cbxTodos;

    private Dialog dialogEditar;

    private EventBus bus;

    private RecyclerView recyclerProductosPedido;

    private String mes2;
    private String anno2;
    private String dia2;

    private String mes3;
    private String anno3;
    private String dia3;

    private RequestQueue requestQueue;

    private LocationManager mlocManager;

    private LocationListener Localizacion;

    private AdapterProductosPedido adapterProductosPedido;

    private LinearLayoutCompat linearLayoutDireccion, linearLayoutDireccion2;

    private int posicion_pedido;

    public MisPedidosFragment() {
        // Required empty public constructor
    }

    public static MisPedidosFragment newInstance(String param1, String param2) {
        MisPedidosFragment fragment = new MisPedidosFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listMisPedidos = new ArrayList<Pedido>();
        vistaBienvenido = new BienvenidoFragment();
        cliente = new AsyncHttpClient();
        progress = new ProgressDialog(getActivity());
        progress.setCancelable(false);
        m = new MainActivity();
        progress.setMessage("Espere un momento por favor...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        bus = EventBus.getDefault();

        dialogEditar = new Dialog(getContext());
        dialogEditar.setContentView(R.layout.popup_pedido);
        dialogEditar.setCancelable(false);
        dialogEditar.setCanceledOnTouchOutside(false);
        btnCloseEditar = (TextView) dialogEditar.findViewById(R.id.txtClose2);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_mis_pedidos, container, false);
        /////
        tituloPedido = (TextView) dialogEditar.findViewById(R.id.tituloPedido);
        tituloPedido.setText("EDITAR PEDIDO");
        txtDireccion = (TextView) dialogEditar.findViewById(R.id.txtUbicacion2);
        linearLayoutDireccion = (LinearLayoutCompat) dialogEditar.findViewById(R.id.layoutDireccion);
        linearLayoutDireccion2 = (LinearLayoutCompat) dialogEditar.findViewById(R.id.layoutDireccion2);
        linearLayoutDireccion.setVisibility(View.GONE);
        linearLayoutDireccion2.setVisibility(View.VISIBLE);
        spinnerHora = (Spinner) dialogEditar.findViewById(R.id.spinerHora);
        final String[] valoreshora = {"6:00 AM","7:00 AM"};
        spinnerHora.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valoreshora));

        spinnerTurno = (Spinner) dialogEditar.findViewById(R.id.spinerTurno);
        final String[] valoresturno = {"MAÑANA","TARDE"};
        spinnerTurno.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valoresturno));
        /////
        recyclerMisPedidos = (RecyclerView) view.findViewById(R.id.recyclerMisPedidos);
        spinnerEstado = (Spinner) view.findViewById(R.id.txtEstadoMisPedidos);
        final String[] valores = {"--TODOS--", "PENDIENTES", "ATENDIDOS", "ANULADOS"};
        spinnerEstado.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valores));
        fechaPedidoMisPedidos = (TextView) view.findViewById(R.id.txtFechaMisPedidos);
        cbxTodos = (CheckBox) view.findViewById(R.id.cbxTodos);
        fechaPedidoMisPedidos.setKeyListener(null);
        fechaPedidoMisPedidos.requestFocus();
        setearFecha3("1", "", "", "");
        fechaPedidoMisPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int anno__ = Integer.parseInt(anno3);
                int mes__ = Integer.parseInt(mes3)-1;
                int dia__ = Integer.parseInt(dia3);
                fechaPedidoPicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        setearFecha3("2", String.format("%02d", dayOfMonth),
                                String.format("%02d", (month+1)), String.valueOf(year));
                        llenarProductosMiPedido();
                    }
                }, anno__, mes__, dia__);
                fechaPedidoPicker.setCancelable(false);
                fechaPedidoPicker.setCanceledOnTouchOutside(false);
                fechaPedidoPicker.show();
            }
        });

        //LLENAMOS EL RECYCLER

        recyclerMisPedidos.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        //recyclerProductos.setLayoutManager(new LinearLayoutManager(getActivity()));

        //AdapterMiPedido adapterMiPedido = new AdapterMiPedido(listMisPedidos);
        //recyclerMisPedidos.setAdapter(adapterMiPedido);

        btnRegresar = (Button) view.findViewById(R.id.btnRegresarMisPedidos);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioFragment(vistaBienvenido, "BIENVENIDO", container, inflater);
            }
        });
        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                llenarProductosMiPedido();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cbxTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCheckbox();
            }
        });

        /////////////////////////////////////////////////////////////////////

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
                    setLocation(loc);
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

        /////////////////////////////////////////////////////////////////////

        estadoMiPedido = (TextView) dialogEditar.findViewById(R.id.estadoMiPedido);

        fechaPedido = (TextView) dialogEditar.findViewById(R.id.fechaProductoPedido);
        fechaPedido.setKeyListener(null);
        fecha2Pedido = (TextView) dialogEditar.findViewById(R.id.fecha2ProductoPedido);
        fecha2Pedido.setVisibility(View.GONE);
        fecha2Pedido.setPadding(0,0,0,0);
        fecha2Pedido.setTextSize(0);
        productospedido = new ArrayList<ProductoVo>();
        fecha2Pedido.setKeyListener(null);
        setearFecha("1", "", "", "");
        setearFecha2("1", "", "", "");
        fechaPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int anno2 = Integer.parseInt(anno);
                int mes2 = Integer.parseInt(mes) - 1;
                int dia2 = Integer.parseInt(dia);
                fechaPedidoPicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        setearFecha("2", String.format("%02d", dayOfMonth),
                                String.format("%02d", (month + 1)), String.valueOf(year));
                    }
                }, anno2, mes2, dia2);
                fechaPedidoPicker.setCancelable(false);
                fechaPedidoPicker.setCanceledOnTouchOutside(false);
                fechaPedidoPicker.show();
                //dialog3.show();
            }
        });
        fecha2Pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int anno_2 = Integer.parseInt(anno2);
                int mes_2 = Integer.parseInt(mes2) - 1;
                int dia_2 = Integer.parseInt(dia2);
                fechaPedidoPicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        setearFecha2("2", String.format("%02d", dayOfMonth),
                                String.format("%02d", (month + 1)), String.valueOf(year));
                    }
                }, anno_2, mes_2, dia_2);
                fechaPedidoPicker.setCancelable(false);
                fechaPedidoPicker.setCanceledOnTouchOutside(false);
                fechaPedidoPicker.show();
                //dialog3.show();
            }
        });

        totalProductoPedido = (TextView) dialogEditar.findViewById(R.id.totalProductoPedido);

        spinnerTipo = (Spinner) dialogEditar.findViewById(R.id.spinerTipo);
        final String[] valorestipo = {"UN DÍA"};
        spinnerTipo.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valorestipo));

        //EDITAR PEDIDO

        btnHacerPedido = (Button) dialogEditar.findViewById(R.id.btnHacerPedido);
        btnHacerPedido.setText("EDITAR PEDIDO");
        btnHacerPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LÓGICA PARA CAMBIAR ESTADO DE PEDIDO Y LOS DETALLES
                if(isOnlineNet()) {
                    if(totalproductospedidof!=0f) {
                        try {
                            if (compararHora()) {
                                progress.show();
                                requestQueue = Volley.newRequestQueue(getContext());
                                String URL_REGISTRO_PEDIDO_LISTO = Utilidades.WEB_SERVICE + "?accion=ACTUALIZAR_PEDIDO_LISTO";
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRO_PEDIDO_LISTO,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                //BASTANTE ÚTIL PARA VER ERRORES
                                                Log.i("tagconvertstr", "[" + response + "]");
                                                try {
                                                    JSONObject rptaJson = new JSONObject(response);
                                                    String respuesta = rptaJson.getString("1");
                                                    if (respuesta.equals("1")) {
                                                        mostrarToast("PEDIDO EDITADO CORRECTAMENTE");
                                                        llenarProductosMiPedido();
                                                        pararServicio();
                                                        dialogEditar.dismiss();
                                                        progress.dismiss();
                                                    } else if (respuesta.equals("2")) {
                                                        mostrarToast("NO SE PUDO EDITAR EL PEDIDO");
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
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> parametros = new HashMap<String, String>();
                                        parametros.put("id_pedido", idPedido+"");
                                        parametros.put("turno", spinnerTurno.getSelectedItem().toString());
                                        parametros.put("hora", spinnerHora.getSelectedItem().toString());
                                        parametros.put("fecha_atencion1", fechaPedido.getText().toString());
                                        parametros.put("fecha_atencion2", fecha2Pedido.getText().toString());
                                        //parametros.put("direccion", cdireccion);
                                        //parametros.put("latitud", clatitud);
                                        //parametros.put("longitud", clongitud);
                                        //parametros.put("total", totalproductospedidof + "");
                                        return parametros;
                                    }
                                };
                                requestQueue = Volley.newRequestQueue(getActivity().getApplication());
                                requestQueue.add(stringRequest);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mostrarToast("DEBES TENER AL MENOS UN PRODUCTO");
                    }
                } else {
                    mostrarToast("NO ESTÁS CONECTADO A INTERNET");
                }
                ////////////////////////////////////////////////////////
            }
        });

        return view;
    }

    private void validarCheckbox() {
        if(cbxTodos.isChecked()) {
            fechaPedidoMisPedidos.setText("");
        } else {
            setearFecha3("1", "", "", "");
        }
        llenarProductosMiPedido();
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

    private void llenarProductosMiPedido() {
        progress.show();
        String url = Utilidades.WEB_SERVICE + "?accion=LISTAR_PEDIDOS_PERSONA&id_persona="+
                m.obtenerIdUsuarioEstadoSesion(getContext())+"&estado="+spinnerEstado.getSelectedItem().toString()+
                "&fecha="+fechaPedidoMisPedidos.getText().toString();
        Log.i("hola", url);
        //mostrarToast(url);
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //SI REALMENTE SE HAN CARGADO LOS DATOS
                if (statusCode == 200) {
                    listarProductosMiPedido(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void listarProductosMiPedido(String respuesta) {

        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            listMisPedidos = new ArrayList<Pedido>();
            for (int i = 0; i < jsonArray.length(); i++) {
                Pedido p = new Pedido();
                p.setId(jsonArray.getJSONObject(i).getInt("id_pedido"));
                p.setDireccion(jsonArray.getJSONObject(i).getString("direccion"));
                p.setEstado(jsonArray.getJSONObject(i).getString("estado"));
                p.setFecha_atencion(jsonArray.getJSONObject(i).getString("fecha_atencion"));
                p.setFecha_atencion2(jsonArray.getJSONObject(i).getString("fecha_atencion2"));
                p.setHora(jsonArray.getJSONObject(i).getString("hora"));
                p.setIdrepartidor(jsonArray.getJSONObject(i).getString("idrepartidor"));
                p.setLatitud(jsonArray.getJSONObject(i).getString("latitud"));
                p.setLongitud(jsonArray.getJSONObject(i).getString("longitud"));
                p.setTipo_pago(jsonArray.getJSONObject(i).getString("tipo_pago"));
                p.setTotal(Float.parseFloat(jsonArray.getJSONObject(i).getString("total")));
                p.setTurno(jsonArray.getJSONObject(i).getString("turno"));
                listMisPedidos.add(p);
            }
            adapterMiPedido = new AdapterMiPedido(listMisPedidos);
            //mostrarToast(""+listMisPedidos.size());
            /*adapterProductosMiPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowPopup(v, listProductos.get(recyclerProductos.getChildAdapterPosition(v)));
                }
            });*/
            recyclerMisPedidos.setAdapter(adapterMiPedido);
            progress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
        }
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

    //

    public void ShowPopup2() {
        //PERMISO DE UBICACION

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 5000);
        } else {
            locationStart();
        }
        totalproductospedidof = 0f;
        TextView textClose = (TextView) dialogEditar.findViewById(R.id.txtClose2);
        Button btnAgregarCarrito = (Button) dialogEditar.findViewById(R.id.btnVerCarrito);
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditar.dismiss();
                pararServicio();
            }
        });
        //LLENAMOS EL RECYCLER DEL MODAL VER CARRITO
        recyclerProductosPedido = (RecyclerView) dialogEditar.findViewById(R.id.recyclerProductosPedido);
        recyclerProductosPedido.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerProductosPedido.setAdapter(adapterProductosPedido);
        dialogEditar.show();
        //LLENO LOS SPINNERS NECESARIOS
        final String[] valorestipo = {"UN DÍA"};
        spinnerTipo.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valorestipo));
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(valorestipo[position].equals("UN DÍA")) {
                    fecha2Pedido.setText(fechaPedido.getText().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setearFecha(String tipo, String dia2_, String mes2_, String anno2_) {
        if (tipo.equals("1")) {
            SimpleDateFormat dateFormatDia = new SimpleDateFormat("dd", Locale.ENGLISH);
            SimpleDateFormat dateFormatMes = new SimpleDateFormat("MM", Locale.ENGLISH);
            SimpleDateFormat dateFormatAnno = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            anno = dateFormatAnno.format(new Date());
            mes = dateFormatMes.format(new Date());
            dia = dateFormatDia.format(new Date());
            fecha_hoy = dia + "/" + mes + "/" + anno;
            fechaPedido.setText(fecha_hoy);
        } else {
            if(compararFechasConDate(dia2_ + "/" + mes2_ + "/" + anno2_, fecha_hoy)==0||
                    compararFechasConDate(dia2_ + "/" + mes2_ + "/" + anno2_, fecha_hoy)==-1) {
                mostrarToast("NO SE PUDO ACTUALIZAR LA FECHA");
            } else if(compararFechasConDate(dia2_ + "/" + mes2_ + "/" + anno2_, fecha_hoy)==1) {
                mostrarToast("NO PUEDES INGRESAR FECHAS MENORES A HOY");
            } else if(compararFechasConDate(dia2_ + "/" + mes2_ + "/" + anno2_, fecha_hoy)==2) {
                if (spinnerTipo.getSelectedItem().toString().equals("UN DÍA")) {
                    fecha2Pedido.setText(dia2_ + "/" + mes2_ + "/" + anno2_);
                    fechaPedido.setText(dia2_ + "/" + mes2_ + "/" + anno2_);
                    anno2 = anno2_;
                    mes2 = mes2_;
                    dia2 = dia2_;
                    anno = anno2_;
                    mes = mes2_;
                    dia = dia2_;
                } else {
                    if (compararFechasConDate(dia2_ + "/" + mes2_ + "/" + anno2_, fecha2Pedido.getText().toString()) == 1||
                            (dia2_ + "/" + mes2_ + "/" + anno2_).equals(fecha2Pedido.getText().toString())) {
                        fechaPedido.setText(dia2_ + "/" + mes2_ + "/" + anno2_);
                        anno = anno2_;
                        mes = mes2_;
                        dia = dia2_;
                    } else {
                        mostrarToast("SELECCIONA UNA FECHA INICIAL CORRECTA");
                    }
                }
            }
        }
    }

    private void setearFecha2(String tipo, String dia_2, String mes_2, String anno_2) {
        if (tipo.equals("1")) {
            SimpleDateFormat dateFormatDia = new SimpleDateFormat("dd", Locale.ENGLISH);
            SimpleDateFormat dateFormatMes = new SimpleDateFormat("MM", Locale.ENGLISH);
            SimpleDateFormat dateFormatAnno = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            anno2 = dateFormatAnno.format(new Date());
            mes2 = dateFormatMes.format(new Date());
            dia2 = dateFormatDia.format(new Date());
            fecha_hoy = dia2 + "/" + mes2 + "/" + anno2;
            fecha2Pedido.setText(fecha_hoy);
        } else {
            if(compararFechasConDate(dia_2 + "/" + mes_2 + "/" + anno_2, fecha_hoy)==0||
                    compararFechasConDate(dia_2 + "/" + mes_2 + "/" + anno_2, fecha_hoy)==-1) {
                mostrarToast("NO SE PUDO ACTUALIZAR LA FECHA");
            } else if(compararFechasConDate(dia_2 + "/" + mes_2 + "/" + anno_2, fecha_hoy)==1) {
                mostrarToast("NO PUEDES INGRESAR FECHAS MENORES A HOY");
            } else if(compararFechasConDate(dia_2 + "/" + mes_2 + "/" + anno_2, fecha_hoy)==2) {
                if (spinnerTipo.getSelectedItem().toString().equals("UN DÍA")) {
                    fecha2Pedido.setText(dia_2 + "/" + mes_2 + "/" + anno_2);
                    fechaPedido.setText(dia_2 + "/" + mes_2 + "/" + anno_2);
                    anno = anno_2;
                    mes = mes_2;
                    dia = dia_2;
                    anno2 = anno_2;
                    mes2 = mes_2;
                    dia2 = dia_2;
                }
                if (compararFechasConDate(dia_2 + "/" + mes_2 + "/" + anno_2, fechaPedido.getText().toString()) == 2) {
                    fecha2Pedido.setText(dia_2 + "/" + mes_2 + "/" + anno_2);
                    anno2 = anno_2;
                    mes2 = mes_2;
                    dia2 = dia_2;
                } else {
                    mostrarToast("SELECCIONA UNA FECHA FINAL CORRECTA");
                }
            }
        }
    }

    private void setearFecha3(String tipo, String dia_2, String mes_2, String anno_2) {
        if (tipo.equals("1")) {
            SimpleDateFormat dateFormatDia = new SimpleDateFormat("dd", Locale.ENGLISH);
            SimpleDateFormat dateFormatMes = new SimpleDateFormat("MM", Locale.ENGLISH);
            SimpleDateFormat dateFormatAnno = new SimpleDateFormat("yyyy", Locale.ENGLISH);
            anno3 = dateFormatAnno.format(new Date());
            mes3 = dateFormatMes.format(new Date());
            dia3 = dateFormatDia.format(new Date());
            fecha_hoy = dia3 + "/" + mes3 + "/" + anno3;
            fechaPedidoMisPedidos.setText(fecha_hoy);
        } else {
            fechaPedidoMisPedidos.setText(dia_2 + "/" + mes_2 + "/" + anno_2);
            //mostrarToast("aqui es el error :V");
            anno3 = anno_2;
            mes3 = mes_2;
            dia3 = dia_2;
        }
        cbxTodos.setChecked(false);
    }

    private int compararFechasConDate(String fecha1, String fechaActual) {
        int rst = 0;
        try {
            //Obtenemos las fechas enviadas en el formato a comparar*/
            SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
            Date fechaDate1 = formateador.parse(fecha1);
            Date fechaDate2 = formateador.parse(fechaActual);

            if (fechaDate1.before(fechaDate2) ){
                rst = 1;
                //resultado= "La Fecha 1 es menor ";
            } else {
                rst = 2;
                //resultado= "La Fecha 1 es mayor o igual ";
            }
        } catch (ParseException e) {
            rst = -1;
        }
        return rst;
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
        cdireccion = "";
    }

    private void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if(loc!=null) {
            if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
                try {
                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                    if(geocoder!=null) {
                        List<Address> list = geocoder.getFromLocation(
                                loc.getLatitude(), loc.getLongitude(), 1);
                        if (!list.isEmpty()) {
                            Address DirCalle = list.get(0);
                            cdireccion = DirCalle.getAddressLine(0);
                            clongitud = ""+loc.getLongitude();
                            clatitud = ""+loc.getLatitude();
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

    private void llenarProductosPedido(final String id) {
        progress.show();
        String url = Utilidades.WEB_SERVICE + "?accion=LISTA_DETALLES_PEDIDO&pedido_id="+id;
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //SI REALMENTE SE HAN CARGADO LOS DATOS
                if(statusCode == 200) {
                    listarProductosPedido(new String(responseBody), id);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void listarProductosPedido(String respuesta, String id) {
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            productospedido = new ArrayList<ProductoVo>();
            for (int i = 0; i < jsonArray.length(); i++) {
                ProductoVo pro = new ProductoVo();
                pro.setId(jsonArray.getJSONObject(i).getInt("id"));
                pro.setNombre(jsonArray.getJSONObject(i).getString("nombre"));
                pro.setIdProducto(Integer.parseInt(jsonArray.getJSONObject(i).getString("id_producto")));
                pro.setCantidad(Integer.parseInt(jsonArray.getJSONObject(i).getString("cantidad")));
                pro.setPrecio(Float.parseFloat(jsonArray.getJSONObject(i).getString("precio")));
                pro.setSubtotal(Float.parseFloat(jsonArray.getJSONObject(i).getString("subtotal")));
                productospedido.add(pro);
            }
            adapterProductosPedido = new AdapterProductosPedido(productospedido);
            progress.dismiss();
            //llenarDatosPedido
            ShowPopup2();
            llenarDatosPedido(Integer.parseInt(id));
        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
        }
    }

    private void llenarDatosPedido(int id_pedido) {
        if (isOnlineNet()) {
            requestQueue = Volley.newRequestQueue(getContext());
            String URL_DATOSP = Utilidades.WEB_SERVICE + "?accion=DATOS_PEDIDO&id_pedido="+id_pedido;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DATOSP,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //BASTANTE ÚTIL PARA VER ERRORES
                            Log.i("tagconvertstr", "[" + response + "]");
                            try {
                                JSONArray rptaJson = new JSONArray(response);
                                String respuesta = rptaJson.getString(0);
                                String turno = rptaJson.getString(1);
                                String hora = rptaJson.getString(2);
                                String estado = rptaJson.getString(3);
                                String fechad = rptaJson.getString(4);
                                String fecham = rptaJson.getString(5);
                                String fechaa = rptaJson.getString(6);
                                String total = rptaJson.getString(7);
                                String direction = rptaJson.getString(8);
                                if (respuesta.equals("1")) {
                                    spinnerTurno.setSelection(Integer.parseInt(turno));
                                    if(spinnerTurno.getSelectedItem().toString().equals("TARDE")) {
                                        final String[] valoreshora = {"5:00 PM", "6:00 PM", "7:00 PM"};
                                        spinnerHora.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valoreshora));
                                    } else {
                                        final String[] valoreshora = {"6:00 AM","7:00 AM"};
                                        spinnerHora.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valoreshora));
                                    }
                                    fechaPedido.setText(fechad+"/"+fecham+"/"+fechaa);
                                    fecha2Pedido.setText(fechad+"/"+fecham+"/"+fechaa);
                                    totalProductoPedido.setText("S/. "+total);
                                    totalproductospedidof = Float.parseFloat(total);
                                    estadoMiPedido.setText(estado);
                                    //INICIALIZO VALORES PARA LA FECHA
                                    //setearFecha3("2", fechad, fecham, fechaa);
                                    spinnerHora.setSelection(Integer.parseInt(hora));
                                    txtDireccion.setText(direction);
                                } else if (respuesta.equals("2")) {
                                    mostrarToast("NO SE PUDO OBTENER LOS DATOS DEL PEDIDO");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("tagconvertstr", "[" + error + "]");
                }
            });
            requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }
    }

    public void pararServicio() {
        mlocManager.removeUpdates(Localizacion);
    }

    //

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Subscribe
    public void ejecutarLlamada(String s) {
        if (s.equals("S")) {
            mostrarToast("PEDIDO ANULADO CORRECTAMENTE");
        } else if(s.equals("N")) {
            mostrarToast("PEDIDO NO SE PUDO ANULAR");
        } else {
            llenarProductosPedido(s);
        }
    }

    @Subscribe
    public void ejecutarLlamada(Pedido p) {
        llenarProductosPedido(p.getId()+"");
        posicion_pedido = Integer.parseInt(p.getDireccion());
        idPedido = p.getId();
        //mostrarToast(posicion_pedido+"");
    }

    @Subscribe
    public void ejecutarLlamada(final ProductoVo p) {
        if(isOnlineNet()) {
            requestQueue = Volley.newRequestQueue(getContext());
            String URL_ACTP = Utilidades.WEB_SERVICE + "?accion=ACTUALIZAR_PRECIO_PEDIDO";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ACTP,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //BASTANTE ÚTIL PARA VER ERRORES
                            Log.i("tagconvertstr", "["+response+"]");
                            try {
                                JSONObject rptaJson = new JSONObject(response);
                                String respuesta = rptaJson.getString("1");
                                if(respuesta.equals("1")) {
                                    //RECIBO EN EL PRECIO EL MONTO TOTAL
                                    totalProductoPedido.setText("S/. "+p.getPrecio());
                                    totalproductospedidof = p.getPrecio();
                                    //EN GETNOMBRE ALMACENO LA POSICION DEL PRODUCTO PARA SABER DÓNDE REMOVER O EDITAR EN LA LISTA DE PRODUCTOS EN PEDIDO DE ESTE CONTEXTO
                                    if (p.getId()==1) {
                                        productospedido.get(Integer.parseInt(p.getNombre())).setCantidad(p.getCantidad());
                                        productospedido.get(Integer.parseInt(p.getNombre())).setSubtotal(p.getSubtotal());
                                        llenarProductosMiPedido();
                                        //mostrarToast(posicion_pedido+"");
                                    }
                                } else if(respuesta.equals("2")) {
                                    mostrarToast("NO SE PUDO ACTUALIZAR EL PEDIDO");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("tagconvertstr", "["+error+"]");
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> parametros = new HashMap<String, String>();
                    parametros.put("id_pedido", productospedido.get(Integer.parseInt(p.getNombre())).getId()+"");
                    parametros.put("total", p.getPrecio()+"");
                    return parametros;
                }
            };
            requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }
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

    private Boolean compararHora() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateInString = fechaPedido.getText().toString();
        Date horahoy = new Date();
        Date fecha_atencion = formatter.parse(dateInString);

        if(formatter.format(horahoy).equals(formatter.format(fecha_atencion))) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String myTime = "";
            Date horap = null;
            String w = formatter.format(horahoy) + " ";
            switch (spinnerHora.getSelectedItem().toString()) {
                case "6:00 AM":
                    myTime =  w + "05:00:00";
                    break;
                case "7:00 AM":
                    myTime = w + "06:00:00";
                    break;
                case "5:00 PM":
                    myTime = w + "16:00:00";
                    break;
                case "6:00 PM":
                    myTime = w + "17:00:00";
                    break;
                case "7:00 PM":
                    myTime = w + "18:00:00";
                    break;
            }
            horap = sdf.parse(myTime);
            //mostrarToast(horap.toString()+"------"+horahoy.toString());
            if(horap.after(horahoy)) {
                return true;
            } else {
                mostrarToast("VERIFICA LA HORA QUE PIDES. SOLO PUEDES REALIZAR UN PEDIDO CON AL " +
                        "MENOS UNA HORA DE ANTICIPACIÓN");
                return false;
            }
        } else {
            return true;
        }
    }
}
