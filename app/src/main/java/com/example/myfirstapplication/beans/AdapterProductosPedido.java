package com.example.myfirstapplication.beans;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myfirstapplication.MainActivity;
import com.example.myfirstapplication.R;
import com.example.myfirstapplication.proceso.CatalogoProductosFragment;
import com.example.myfirstapplication.utilidades.Utilidades;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdapterProductosPedido
        extends RecyclerView.Adapter<AdapterProductosPedido.ViewHolderProductos>
        implements CatalogoProductosFragment.OnFragmentInteractionListener {

    ArrayList<ProductoVo> listaProductos;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public AdapterProductosPedido(ArrayList<ProductoVo> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @Override
    public ViewHolderProductos onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_pedido, null, false);
        return new ViewHolderProductos(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderProductos holder, int position) {
        holder.etiNombre.setText(listaProductos.get(position).getNombre());
        holder.etiPrecio.setText("PRECIO: S/. " + listaProductos.get(position).getPrecio().toString());
        holder.etiCantidad.setText(listaProductos.get(position).getCantidad() + " UNID.");
        holder.etiSubtotal.setText("S/. " + listaProductos.get(position).getSubtotal().toString());
        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public class ViewHolderProductos extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView etiNombre, etiCantidad, etiSubtotal, etiPrecio, txtTotalProductoPedido;
        Button btnEliminar;
        Button btnAumentar;
        Button btnDisminuir;
        TextView totalProductoPedido;
        Context context;
        MainActivity m;
        RequestQueue requestQueue;
        CatalogoProductosFragment vistacatalogo;
        EventBus bus;

        public ViewHolderProductos(View itemView) {
            super(itemView);
            context = itemView.getContext();
            m = new MainActivity();
            etiNombre = (TextView) itemView.findViewById(R.id.nombreProductoPedido);
            etiPrecio = (TextView) itemView.findViewById(R.id.precioProductoPedido);
            etiCantidad = (TextView) itemView.findViewById(R.id.cantidadProductoPedido);
            etiSubtotal = (TextView) itemView.findViewById(R.id.subtotalProductoPedido);
            btnAumentar = (Button) itemView.findViewById(R.id.btnAumentarCantidadDetallePedido);
            btnDisminuir = (Button) itemView.findViewById(R.id.btnDisminuirCantidadDetallePedido);
            btnEliminar = (Button) itemView.findViewById(R.id.btnEliminarDetallePedido);
            vistacatalogo = new CatalogoProductosFragment();
            bus = EventBus.getDefault();
        }

        void setOnClickListeners() {
            btnEliminar.setOnClickListener(this);
            btnDisminuir.setOnClickListener(this);
            btnAumentar.setOnClickListener(this);
        }

        public void setearTotal(boolean b, String unidades, Float subtotal) {
            final int position = getAdapterPosition();
            Float total = 0f;
            for (int i = 0; i < listaProductos.size(); i++) {
                total += listaProductos.get(i).getSubtotal();
            }
            ProductoVo p = new ProductoVo();
            //USO PRECIO PARA SETEAR EL TOTAL
            p.setPrecio(total);
            p.setNombre(position+"");
            p.setId(0);
            if(b) {
                p.setCantidad(Integer.parseInt(unidades));
                p.setSubtotal(subtotal);
                //INDICANDO QUE ES SUMAR O RESTAR
                p.setId(1);
            }
            bus.post(p);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.btnAumentarCantidadDetallePedido:
                    //AQUÍ AGREGO LA LÓGICA PARA AUMENTAR CANTIDAD
                    if(isOnlineNet()) {
                        requestQueue = Volley.newRequestQueue(context);
                        String URL_AUMENTARDP = Utilidades.WEB_SERVICE + "?accion=AUMENTAR_DETALLE_PEDIDO";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_AUMENTARDP,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //BASTANTE ÚTIL PARA VER ERRORES
                                        Log.i("tagconvertstr", "["+response+"]");
                                        try {
                                            JSONObject rptaJson = new JSONObject(response);
                                            String respuesta = rptaJson.getString("1");
                                            String UNIDADES = rptaJson.getString("2");
                                            String SUBTOTAL = rptaJson.getString("3");
                                            if(respuesta.equals("1")) {
                                                etiCantidad.setText(UNIDADES + " UNID.");
                                                etiSubtotal.setText("S/. " + Float.parseFloat(SUBTOTAL));
                                                listaProductos.get(position).setCantidad(Integer.parseInt(UNIDADES));
                                                listaProductos.get(position).setSubtotal(Float.parseFloat(SUBTOTAL));
                                                setearTotal(true, UNIDADES, Float.parseFloat(SUBTOTAL));
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
                                parametros.put("id_detalle", listaProductos.get(position).getId()+"");
                                parametros.put("cantidad", listaProductos.get(position).getCantidad()+"");
                                parametros.put("subtotal", listaProductos.get(position).getSubtotal()+"");
                                parametros.put("precio", listaProductos.get(position).getPrecio()+"");
                                return parametros;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(stringRequest);
                    }
                    ///////////////////////////////////////////////
                    break;
                case R.id.btnDisminuirCantidadDetallePedido:
                    //AQUÍ AGREGO LA LÓGICA PARA DISMINUIR CANTIDAD
                    if(isOnlineNet()) {
                        requestQueue = Volley.newRequestQueue(context);
                        String URL_DISMINUIRDP = Utilidades.WEB_SERVICE + "?accion=DISMINUIR_DETALLE_PEDIDO";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_DISMINUIRDP,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //BASTANTE ÚTIL PARA VER ERRORES
                                        Log.i("tagconvertstr", "["+response+"]");
                                        try {
                                            JSONObject rptaJson = new JSONObject(response);
                                            String respuesta = rptaJson.getString("1");
                                            String UNIDADES = rptaJson.getString("2");
                                            String SUBTOTAL = rptaJson.getString("3");
                                            if(respuesta.equals("1")) {
                                                etiCantidad.setText(UNIDADES + " UNID.");
                                                etiSubtotal.setText("S/. " + Float.parseFloat(SUBTOTAL));
                                                listaProductos.get(position).setCantidad(Integer.parseInt(UNIDADES));
                                                listaProductos.get(position).setSubtotal(Float.parseFloat(SUBTOTAL));
                                                setearTotal(true, UNIDADES, Float.parseFloat(SUBTOTAL));
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
                                parametros.put("id_detalle", listaProductos.get(position).getId()+"");
                                parametros.put("cantidad", listaProductos.get(position).getCantidad()+"");
                                parametros.put("subtotal", listaProductos.get(position).getSubtotal()+"");
                                parametros.put("precio", listaProductos.get(position).getPrecio()+"");
                                return parametros;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(stringRequest);
                    }
                    break;
                case R.id.btnEliminarDetallePedido:
                    //AQUÍ AGREGO LA LÓGICA PARA ELIMINAR
                    if(isOnlineNet()) {
                        requestQueue = Volley.newRequestQueue(context);
                        String URL_ELIMINARDP = Utilidades.WEB_SERVICE + "?accion=ELIMINAR_DETALLE_PEDIDO";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ELIMINARDP,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //BASTANTE ÚTIL PARA VER ERRORES
                                        Log.i("tagconvertstr", "["+response+"]");
                                        try {
                                            JSONObject rptaJson = new JSONObject(response);
                                            String respuesta = rptaJson.getString("1");
                                            if(respuesta.equals("1")) {
                                                listaProductos.remove(position);
                                                setearTotal(false, "0", 0f);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();

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
                                parametros.put("id_detalle", listaProductos.get(position).getId()+"");
                                return parametros;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(stringRequest);
                    }
                    ///////////////////////////////////////////////
                    break;
            }
        }

        public Boolean isOnlineNet() {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                boolean connect = false;

                if (networkInfo != null && networkInfo.isConnected()) {
                    connect = true;
                }
                return connect;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }
    }
}
