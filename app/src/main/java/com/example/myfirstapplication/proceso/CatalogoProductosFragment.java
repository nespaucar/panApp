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
import android.os.Handler;
import android.provider.Settings;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfirstapplication.MainActivity;
import com.example.myfirstapplication.R;
import com.example.myfirstapplication.actividad.BienvenidoFragment;
import com.example.myfirstapplication.beans.AdapterProductos;
import com.example.myfirstapplication.beans.AdapterProductosPedido;
import com.example.myfirstapplication.beans.Categoria;
import com.example.myfirstapplication.beans.Producto;
import com.example.myfirstapplication.beans.ProductoVo;
import com.example.myfirstapplication.utilidades.Utilidades;
import com.google.android.libraries.places.api.net.PlacesClient;
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

import cz.msebera.android.httpclient.Header;

public class CatalogoProductosFragment extends Fragment implements Response.Listener<JSONObject>,
        Response.ErrorListener {

    private OnFragmentInteractionListener mListener;

    private String idProductoMayor;

    private ArrayList<Producto> listProductos;

    private RecyclerView recyclerProductos;

    private RecyclerView recyclerProductosPedido;

    private Spinner spinnerCategoria, spinnerTipo, spinnerTurno, spinnerHora, spinerUbicacion;

    private TextView idcabecera, fechaPedido, fecha2Pedido, ubicacion;

    private Button btnRegresar, btnHacerPedido, btnVerCarrito, btnRefreshDireccion;

    private Dialog dialog, dialog2;

    private TextView totalProductoPedido;

    private Float totalproductospedidof;

    private RequestQueue requestQueue;

    private AsyncHttpClient cliente;

    private ArrayList<String> nombres_categorias;
    private ArrayList<Categoria> categorias;
    private ArrayList<ProductoVo> productospedido;

    private BienvenidoFragment vistabienvenido;

    private String mes;
    private String anno;
    private String dia;
    private String mes2;
    private String anno2;
    private String dia2;
    private DatePickerDialog fechaPedidoPicker;

    private int idCategoriaSeleccionada;

    private AdapterProductos adapterProductos;

    private AdapterProductosPedido adapterProductosPedido;

    private ProgressDialog progress;

    private MainActivity m;

    private String fecha_hoy;

    private String cdireccion, clatitud, clongitud;

    private LocationManager mlocManager;

    private  Context context;

    private LocationListener Localizacion;

    private EventBus bus;

    private PlacesClient placesClient;

    private Boolean isScrolling = false;

    //final String apiKey = "AIzaSyDjA69DHqzKs92igGpSRfbZkJ4GfSTvZ_k";

    public CatalogoProductosFragment() {
        // Required empty public constructor
    }

    public static CatalogoProductosFragment newInstance(String param1, String param2) {
        CatalogoProductosFragment fragment = new CatalogoProductosFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        idProductoMayor = "0";
        vistabienvenido = new BienvenidoFragment();
        listProductos = new ArrayList<Producto>();
        m = new MainActivity();
        cliente = new AsyncHttpClient();
        progress = new ProgressDialog(getActivity());
        progress.setCancelable(false);
        progress.setMessage("Espere un momento por favor...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        nombres_categorias = new ArrayList<String>();
        categorias = new ArrayList<Categoria>();
        idCategoriaSeleccionada = 0;

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.popup_agregarcarrito);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog2 = new Dialog(getContext());
        dialog2.setContentView(R.layout.popup_pedido);
        dialog2.setCancelable(false);
        dialog2.setCanceledOnTouchOutside(false);
        bus = EventBus.getDefault();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_catalogo_productos, container, false);
        context = getContext();
        spinnerTipo = (Spinner) dialog2.findViewById(R.id.spinerTipo);
        ubicacion = (TextView) dialog2.findViewById(R.id.txtUbicacion);
        btnRefreshDireccion = (Button) dialog2.findViewById(R.id.btnRefreshDireccion);
        //spinerUbicacion = (Spinner) dialog2.findViewById(R.id.spinerUbicacion);
        final String[] valorestipo = {"UN DÍA","RANGO"};
        //final String[] valoresubicacion = {"ACTUAL","DISTINTA"};
        //final String[] valoresubicacion = {"ACTUAL"};
        spinnerTipo.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valorestipo));
        //spinerUbicacion.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valoresubicacion));
        fechaPedido = (TextView) dialog2.findViewById(R.id.fechaProductoPedido);
        fechaPedido.setKeyListener(null);
        fecha2Pedido = (TextView) dialog2.findViewById(R.id.fecha2ProductoPedido);
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
        spinnerCategoria = (Spinner) view.findViewById(R.id.spinnerCategorias);
        recyclerProductos = (RecyclerView) view.findViewById(R.id.recyclerProductos);
        recyclerProductos.setLayoutManager(new GridLayoutManager(getActivity(), 1));

        /*recyclerProductos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });*/

        requestQueue = Volley.newRequestQueue(getContext());

        llenarCategorias();
        llenarProductosPedido();

        //LLENAMOS EL RECYCLER

        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idCategoriaSeleccionada = categorias.get(position).getId();
                llenarProductos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnRegresar = (Button) view.findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambioFragment(vistabienvenido, "BIENVENIDO", container, inflater);
            }
        });

        btnVerCarrito = (Button) view.findViewById(R.id.btnVerCarrito);

        spinnerTurno = (Spinner) dialog2.findViewById(R.id.spinerTurno);
        spinnerHora = (Spinner) dialog2.findViewById(R.id.spinerHora);

        totalProductoPedido = (TextView) dialog2.findViewById(R.id.totalProductoPedido);

        btnHacerPedido = (Button) dialog2.findViewById(R.id.btnHacerPedido);
        btnHacerPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LÓGICA PARA CAMBIAR ESTADO DE PEDIDO Y LOS DETALLES
                if(isOnlineNet()) {
                    if(!obtenerTotal().equals("S/. 0.0")) {
                        try {
                            if (compararHora()) {
                                progress.show();
                                requestQueue = Volley.newRequestQueue(getContext());
                                String URL_REGISTRO_PEDIDO_LISTO = Utilidades.WEB_SERVICE + "?accion=REGISTRAR_PEDIDO_LISTO";
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRO_PEDIDO_LISTO,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                //BASTANTE ÚTIL PARA VER ERRORES
                                                Log.i("tagconvertstr", "[" + response + "]");
                                                try {
                                                    JSONObject rptaJson = new JSONObject(response);
                                                    String respuesta = rptaJson.getString("1");
                                                    String id_pedido = rptaJson.getString("2");
                                                    if (respuesta.equals("1")) {
                                                        //llenarProductos();
                                                        mostrarToast("PEDIDO REALIZADO CORRECTAMENTE");
                                                        m.guardarEstadoSesion(getContext(), true, m.obtenerIdUsuarioEstadoSesion(getContext()),
                                                                m.obtenerNombreUsuarioEstadoSesion(getContext()), Integer.parseInt(id_pedido), m.obtenerTipoUsuarioEstadoSesion(getContext()));
                                                        llenarProductosPedido();
                                                        pararServicio();
                                                        dialog2.dismiss();
                                                        progress.dismiss();
                                                    } else if (respuesta.equals("2")) {
                                                        mostrarToast("NO SE PUDO REGISTRAR EL PEDIDO");
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
                                        parametros.put("id_persona", String.valueOf(m.obtenerIdUsuarioEstadoSesion(getContext())));
                                        parametros.put("id_pedido", String.valueOf(m.obtenerIdPedidoSesion(getContext())));
                                        parametros.put("turno", spinnerTurno.getSelectedItem().toString());
                                        parametros.put("hora", spinnerHora.getSelectedItem().toString());
                                        parametros.put("fecha_atencion1", fechaPedido.getText().toString());
                                        parametros.put("fecha_atencion2", fecha2Pedido.getText().toString());
                                        parametros.put("direccion", cdireccion);
                                        parametros.put("latitud", clatitud);
                                        parametros.put("longitud", clongitud);
                                        parametros.put("total", totalproductospedidof + "");
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
                        mostrarToast("DEBES PEDIR AL MENOS UN PRODUCTO");
                    }
                } else {
                    mostrarToast("NO ESTÁS CONECTADO A INTERNET");
                }
                ////////////////////////////////////////////////////////
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

        btnVerCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llenarProductosPedido();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        ShowPopup2();
                    }
                }, 1500);

            }
        });

        /////////////////////////////////////////////////////////////////////
        //DELETE from pedido where id_pedido > 15;
        //DELETE from detalle_pedido where id_pedido > 15

        return view;
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

    public void actualizarTotal(String mensaje) {
        totalProductoPedido.setText(mensaje);
    }

    public String obtenerTotal() {
        return totalProductoPedido.getText().toString();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(JSONObject response) {
        Producto p = null;
        JSONArray json = response.optJSONArray("producto");
        try {
            for (int i = 0; i < json.length(); i++) {
                p = new Producto();
                JSONObject jsonObject = null;
                jsonObject = json.getJSONObject(i);

                p.setId(Integer.parseInt(jsonObject.optString("id")));
                p.setNombre(jsonObject.optString("nombre"));
                p.setDescripcion(jsonObject.optString("descripcion"));
                p.setPrecio(Float.parseFloat(jsonObject.optString("precio")));
                p.setStock(Float.parseFloat(jsonObject.optString("stock")));
                listProductos.add(p);
            }
            progress.dismiss();
            adapterProductos = new AdapterProductos(listProductos, context);
            recyclerProductos.setAdapter(adapterProductos);
        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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

    private void llenarProductos() {
        progress.show();
        if(categorias.size() > 0 && idCategoriaSeleccionada == 0) {
            idCategoriaSeleccionada = categorias.get(0).getId();
        }
        String url = Utilidades.WEB_SERVICE + "?accion=LISTAR_PRODUCTOS&categoria_id="+idCategoriaSeleccionada+"&idProductoMayor="+idProductoMayor;
        //mostrarToast(url);
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //SI REALMENTE SE HAN CARGADO LOS DATOS
                if(statusCode == 200) {
                    listarProductos(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void listarProductos(String respuesta) {

        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            listProductos = new ArrayList<Producto>();
            for (int i = 0; i < jsonArray.length(); i++) {
                Producto p = new Producto();
                p.setId(jsonArray.getJSONObject(i).getInt("id"));
                p.setNombre(jsonArray.getJSONObject(i).getString("nombre"));
                p.setDescripcion(jsonArray.getJSONObject(i).getString("descripcion"));
                p.setRuta(jsonArray.getJSONObject(i).getString("ruta"));
                p.setStock(Float.parseFloat(jsonArray.getJSONObject(i).getString("stock")));
                p.setPrecio(Float.parseFloat(jsonArray.getJSONObject(i).getString("precio")));
                p.setDato(jsonArray.getJSONObject(i).getString("foto"));
                listProductos.add(p);
                idProductoMayor = ""+jsonArray.getJSONObject(i).getInt("id");
            }
            adapterProductos = new AdapterProductos(listProductos, context);
            adapterProductos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowPopup(v, listProductos.get(recyclerProductos.getChildAdapterPosition(v)));
                }
            });
            recyclerProductos.setAdapter(adapterProductos);
            progress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
        }
    }

    public void llenarCategorias() {
        progress.show();
        String url = Utilidades.WEB_SERVICE + "?accion=LISTA_CATEGORIAS";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //SI REALMENTE SE HAN CARGADO LOS DATOS
                if(statusCode == 200) {
                    listarCategorias(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void listarCategorias(String respuesta) {
        try {
            JSONArray jsonArray = new JSONArray(respuesta);
            Categoria cat0 = new Categoria();
            cat0.setId(0);
            cat0.setNombre("--- TODOS ---");
            categorias.add(cat0);
            nombres_categorias.add("--- TODOS ---");
            for (int i = 0; i < jsonArray.length(); i++) {
                Categoria cat = new Categoria();
                cat.setId(jsonArray.getJSONObject(i).getInt("id"));
                cat.setNombre(jsonArray.getJSONObject(i).getString("nombre"));
                categorias.add(cat);
                nombres_categorias.add(jsonArray.getJSONObject(i).getString("nombre"));
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, nombres_categorias);
            spinnerCategoria.setAdapter(arrayAdapter);
            llenarProductos();
            progress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
        }
    }

    private void llenarProductosPedido() {
        progress.show();
        String url = Utilidades.WEB_SERVICE + "?accion=LISTA_DETALLES_PEDIDO&pedido_id="+m.obtenerIdPedidoSesion(getContext());
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //SI REALMENTE SE HAN CARGADO LOS DATOS
                if(statusCode == 200) {
                    listarProductosPedido(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void listarProductosPedido(String respuesta) {
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
        } catch (Exception e) {
            e.printStackTrace();
            progress.dismiss();
        }
    }

    public void ShowPopup(View v, final Producto p) {

        final int id_producto = p.getId();
        TextView textClose = (TextView) dialog.findViewById(R.id.txtClose);
        TextView nombreproducto = (TextView) dialog.findViewById(R.id.nombreProductoAgregarCarrito);
        TextView descripcionproducto = (TextView) dialog.findViewById(R.id.descripcionProductoAgregarCarrito);
        final TextView stockproducto = (TextView) dialog.findViewById(R.id.stockProductoAgregarCarrito);
        final TextView precioproducto = (TextView) dialog.findViewById(R.id.precioProductoAgregarCarrito);
        TextView stockproducto2 = (TextView) dialog.findViewById(R.id.stock2ProductoAgregarCarrito);
        final TextView subtotalproducto = (TextView) dialog.findViewById(R.id.subtotalProductoAgregarCarrito);
        TextView precioproducto2 = (TextView) dialog.findViewById(R.id.precio2ProductoAgregarCarrito);
        ImageView foto = (ImageView) dialog.findViewById(R.id.imagenProductoAgregarCarrito);
        final EditText quiero = (EditText) dialog.findViewById(R.id.quieroProductoAgregarCarrito);

        //BOTONES
        Button btnSubir = (Button) dialog.findViewById(R.id.btnSubir);
        Button btnBajar = (Button) dialog.findViewById(R.id.btnBajar);

        btnSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int txtQuiero = Integer.parseInt(quiero.getText().toString())+1;
                Float subtot = p.getPrecio()*Float.parseFloat(txtQuiero+"");
                //if(p.getStock()>=Float.parseFloat(txtQuiero+"")) {
                    quiero.setText(txtQuiero+"");
                    subtotalproducto.setText("S/. "+subtot);
                //}
            }
        });

        btnBajar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int txtQuiero = Integer.parseInt(quiero.getText().toString())-1;
                Float subtot = p.getPrecio()*Float.parseFloat(txtQuiero+"");
                if(txtQuiero>0) {
                    quiero.setText(txtQuiero+"");
                    subtotalproducto.setText("S/. "+subtot);
                }
            }
        });

        quiero.setText("1");
        subtotalproducto.setText("S/. "+p.getPrecio());
        quiero.setKeyListener(null);

        Button btnAgregarCarrito = (Button) dialog.findViewById(R.id.btnAgregarCarrito);

        float stockp = Float.parseFloat(p.getStock()+"");
        int stock = (int) stockp;
        //SETEO LA VISTA
        nombreproducto.setText(p.getNombre());
        descripcionproducto.setText(p.getDescripcion());
        stockproducto2.setText(stock+"");
        precioproducto2.setText("S/. "+p.getPrecio()+"");
        stockproducto.setText(p.getStock()+"");
        precioproducto.setText(p.getPrecio()+"");
        Glide.with(v.getContext())
                .load(Utilidades.WEB_IMAGEN_PRODUCTO + p.getRuta())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .placeholder(R.drawable.panadero)
                .error(R.drawable.panadero)
                .into(foto);
        /*if(p.getFoto()!=null) {
            foto.setImageBitmap(p.getFoto());
        } else {
            foto.setImageResource(R.mipmap.ic_launcher);
        }*/
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAgregarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                float stockp = Float.parseFloat(stockproducto.getText().toString());
                int stock = (int) stockp;
                int quieroq = 0;
                if(!quiero.getText().toString().equals("")) {
                    float quierop = Float.parseFloat(quiero.getText().toString());
                    quieroq = (int) quierop;
                }
                if(quiero.getText().toString().equals("")||quiero.getText().toString().equals("0")||quiero.getText().toString().equals("00")||
                        quiero.getText().toString().equals("000")||quiero.getText().toString().equals("0000")) {
                    quiero.setText("");
                    quiero.requestFocus();
                    progress.dismiss();
                    mostrarToast("INGRESA UNA CANTIDAD CORRECTA");
                } else {
                    //if(stock < quieroq) {
                    if(1 > 2) {
                        quiero.setText("");
                        quiero.requestFocus();
                        progress.dismiss();
                        mostrarToast("NO HAY SUFICIENTES PRODUCTOS EN STOCK");
                    } else {
                        //LISTO PARA REGISTRAR
                        if(isOnlineNet()) {
                            requestQueue = Volley.newRequestQueue(getContext());
                            String URL_REGISTRO = Utilidades.WEB_SERVICE + "?accion=REGISTRAR_DETALLE_PEDIDO";

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
                                                    //llenarProductos();
                                                    llenarProductosPedido();
                                                    mostrarToast("PRODUCTO AGREGADO AL CARRITO");
                                                    dialog.dismiss();
                                                } else if(respuesta.equals("2")) {
                                                    mostrarToast("NO SE PUDO INGRESAR DETALLE DE PEDIDO");
                                                }
                                                progress.dismiss();
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
                                    parametros.put("id_producto", String.valueOf(id_producto));
                                    parametros.put("pedido_id", String.valueOf(m.obtenerIdPedidoSesion(getContext())));
                                    parametros.put("cantidad", quiero.getText().toString());
                                    parametros.put("precio", String.valueOf(p.getPrecio()));
                                    return parametros;
                                }
                            };
                            requestQueue = Volley.newRequestQueue(getActivity().getApplication());
                            requestQueue.add(stringRequest);
                        } else {
                            mostrarToast("NO ESTÁS CONECTADO A INTERNET");
                            progress.dismiss();
                        }
                    }
                }
            }
        });

        dialog.show();
    }

    public void ShowPopup2() {
        //PERMISO DE UBICACION

        locationStart();
        llenarProductosPedido();
        //final String[] valoresubicacion = {"ACTUAL","DISTINTA"};
        //final String[] valoresubicacion = {"ACTUAL"};
        totalproductospedidof = 0f;
        TextView textClose = (TextView) dialog2.findViewById(R.id.txtClose2);
        Button btnAgregarCarrito = (Button) dialog2.findViewById(R.id.btnVerCarrito);
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                pararServicio();
            }
        });
        //LLENAMOS EL RECYCLER DEL MODAL VER CARRITO
        recyclerProductosPedido = (RecyclerView) dialog2.findViewById(R.id.recyclerProductosPedido);
        recyclerProductosPedido.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerProductosPedido.setAdapter(adapterProductosPedido);
        dialog2.show();
        btnRefreshDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationStart();
            }
        });
        final String[] valorestipo = {"UN DÍA","RANGO"};
        spinnerTipo.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valorestipo));
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(valorestipo[position].equals("UN DÍA")) {
                    fecha2Pedido.setText(fechaPedido.getText().toString());
                    fecha2Pedido.setVisibility(View.GONE);
                } else {
                    fecha2Pedido.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final String[] valoreshora = {"6:00 AM","7:00 AM"};
        spinnerHora.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valoreshora));
        final String[] valoresturno = {"MAÑANA","TARDE"};
        spinnerTurno.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valoresturno));
        spinnerTurno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(valoresturno[position].equals("TARDE")) {
                    final String[] valoreshora = {"5:00 PM", "6:00 PM", "7:00 PM"};
                    spinnerHora.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valoreshora));
                } else {
                    final String[] valoreshora = {"6:00 AM","7:00 AM"};
                    spinnerHora.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.spinner_item_chalalo, valoreshora));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for(int i = 0;i < productospedido.size();i++) {
            totalproductospedidof += Float.parseFloat(productospedido.get(i).getSubtotal().toString());
        }
        totalProductoPedido.setText("S/. "+totalproductospedidof+"");
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

    private int compararFechasConDate(String fecha1, String fechaActual) {
        int rst = 0;
        try {
            //Obtenemos las fechas enviadas en el formato a comparar
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

    ///////////////////////////////////////////////////////////////////////////////////////////

    private void locationStart() {
        ubicacion.setText("Cargando...");
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
                            ubicacion.setText(cdireccion);
                            clongitud = ""+loc.getLongitude();
                            clatitud = ""+loc.getLatitude();
                            pararServicio();
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
    public void ejecutarLlamada(ProductoVo p) {
        //RECIBO EN EL PRECIO EL MONTO TOTAL
        totalProductoPedido.setText("S/. "+p.getPrecio());
        //EN GETNOMBRE ALMACENO LA POSICION DEL PRODUCTO PARA SABER DÓNDE REMOVER O EDITAR EN LA LISTA DE PRODUCTOS EN PEDIDO DE ESTE CONTEXTO
        if (p.getId()==1) {
            productospedido.get(Integer.parseInt(p.getNombre())).setCantidad(p.getCantidad());
            productospedido.get(Integer.parseInt(p.getNombre())).setSubtotal(p.getSubtotal());
        }
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
