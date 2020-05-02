package com.example.myfirstapplication.beans;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
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
import com.example.myfirstapplication.utilidades.Utilidades;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdapterMiPedido extends RecyclerView.Adapter<AdapterMiPedido.ViewHolderProductos> {

    ArrayList<Pedido> listaPedidos;
    Context context;

    public AdapterMiPedido(ArrayList<Pedido> listaProductos) {
        this.listaPedidos = listaProductos;
    }

    @Override
    public ViewHolderProductos onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_mis_pedidos, null, false);
        return new ViewHolderProductos(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderProductos holder, int position) {
        holder.etiFechaAtencion.setText(listaPedidos.get(position).getFecha_atencion());
        holder.etiTotal.setText("S/. "+listaPedidos.get(position).getTotal());
        holder.etiEstado.setText(listaPedidos.get(position).getEstado());
        holder.etiTurno.setText(listaPedidos.get(position).getTurno());
        switch (listaPedidos.get(position).getEstado()) {
            case "PENDIENTE":
                holder.etiEstado.setTextColor(Color.parseColor("#043B95"));
                break;
            case "ANULADO":
                holder.etiEstado.setTextColor(Color.parseColor("#FF0000"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.etiEditar.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorNaranjaBajo));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.etiEliminar.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorRojoBajo));
                }
                break;
            case "ATENDIDO":
                holder.etiEstado.setTextColor(Color.parseColor("#229500"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.etiEditar.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorNaranjaBajo));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.etiEliminar.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorRojoBajo));
                }
                break;
        }
        holder.setOnClickListeners();
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public class ViewHolderProductos extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView etiFechaAtencion, etiEstado, etiTotal, etiTurno, btnCloseVer, btnCloseEditar, btnCloseEliminar;
        Button etiVer, etiEditar, etiEliminar;
        Dialog dialogVer, dialogEditar, dialogEliminar;
        MainActivity m;
        RequestQueue requestQueue;
        ArrayList<ProductoPo> listaDetallesPedido;
        AdapterProductosPedidoPasado adapterProductosPedidoPasado;
        RecyclerView recyclerProductosPedidoPasado;
        EventBus bus;
        Button btnCancelarPedido;
        @SuppressLint("WrongViewCast")
        public ViewHolderProductos(View itemView) {
            super(itemView);
            etiFechaAtencion = (TextView) itemView.findViewById(R.id.fechaProgramadaMiPedido);
            etiEstado = (TextView) itemView.findViewById(R.id.estadoMiPedido);
            etiTotal = (TextView) itemView.findViewById(R.id.totalMiPedido);
            etiTurno = (TextView) itemView.findViewById(R.id.turnoMiPedido);
            etiVer = (Button) itemView.findViewById(R.id.btnVerMiPedido);
            etiEditar = (Button) itemView.findViewById(R.id.btnEditarMiPedido);
            etiEliminar = (Button) itemView.findViewById(R.id.btnEliminarMiPedido);
            context = itemView.getContext();

            dialogVer = new Dialog(context);
            dialogVer.setContentView(R.layout.popup_detalles_pedido);
            dialogVer.setCancelable(false);
            dialogVer.setCanceledOnTouchOutside(false);
            btnCloseVer = (TextView) dialogVer.findViewById(R.id.btnCloseVer);
            recyclerProductosPedidoPasado = (RecyclerView) dialogVer.findViewById(R.id.recyclerProductosPedido);
            recyclerProductosPedidoPasado.setLayoutManager(new GridLayoutManager(context, 1));

            dialogEditar = new Dialog(context);
            dialogEditar.setContentView(R.layout.popup_pedido);
            dialogEditar.setCancelable(false);
            dialogEditar.setCanceledOnTouchOutside(false);
            btnCloseEditar = (TextView) dialogEditar.findViewById(R.id.txtClose2);

            dialogEliminar = new Dialog(context);
            dialogEliminar.setContentView(R.layout.popup_cancelar_pedido);
            dialogEliminar.setCancelable(false);
            dialogEliminar.setCanceledOnTouchOutside(false);
            btnCloseEliminar = (TextView) dialogEliminar.findViewById(R.id.txtCloseCancelar);

            btnCancelarPedido = (Button) dialogEliminar.findViewById(R.id.btnCancelarPedido);

            m = new MainActivity();
            bus = EventBus.getDefault();
        }

        void setOnClickListeners() {
            etiVer.setOnClickListener(this);
            etiEditar.setOnClickListener(this);
            etiEliminar.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            switch (v.getId()) {
                case R.id.btnVerMiPedido:
                    dialogVer.show();
                    btnCloseVer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogVer.dismiss();
                        }
                    });
                    final TextView turnoD = (TextView) dialogVer.findViewById(R.id.turnoMiPedido);
                    final TextView fechapD = (TextView) dialogVer.findViewById(R.id.fechaProgramadaMiPedido);
                    final TextView fechaeD = (TextView) dialogVer.findViewById(R.id.fechaatencionMiPedido);
                    final TextView tipopagoD = (TextView) dialogVer.findViewById(R.id.tipopagoMiPedido);
                    final TextView repartidorD = (TextView) dialogVer.findViewById(R.id.repartidorMiPedido);
                    final TextView comentarioD = (TextView) dialogVer.findViewById(R.id.comentarioMiPedido);
                    final TextView estadoD = (TextView) dialogVer.findViewById(R.id.estadoMiPedido);
                    final TextView total = (TextView) dialogVer.findViewById(R.id.totalMiPedido);
                    //AQUÍ AGREGO LA LÓGICA PARA AUMENTAR CANTIDAD
                    if (isOnlineNet()) {
                        requestQueue = Volley.newRequestQueue(context);
                        String URL_AUMENTARDP = Utilidades.WEB_SERVICE + "?accion=VER_DETALLE_PEDIDO";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_AUMENTARDP,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        //BASTANTE ÚTIL PARA VER ERRORES
                                        Log.i("tagconvertstr", "[" + response + "]");
                                        try {
                                            JSONObject rptaJson = new JSONObject(response);
                                            String respuesta = rptaJson.getString("1");
                                            String arrayJson = rptaJson.getString("2");
                                            if (respuesta.equals("1")) {
                                                //RECORRO JSON
                                                JSONArray jsonArray = new JSONArray(arrayJson);
                                                listaDetallesPedido = new ArrayList<ProductoPo>();
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    ProductoPo p = new ProductoPo();
                                                    p.setNombre(jsonArray.getJSONObject(i).getString("nombre_producto") + "");
                                                    p.setDescripcion(jsonArray.getJSONObject(i).getString("descripcion_producto") + "");
                                                    p.setPrecio(Float.parseFloat(jsonArray.getJSONObject(i).getString("precio")));
                                                    p.setSubtotal(Float.parseFloat(jsonArray.getJSONObject(i).getString("subtotal")));
                                                    p.setCantidad(Integer.parseInt(jsonArray.getJSONObject(i).getString("cantidad")));
                                                    turnoD.setText(jsonArray.getJSONObject(i).getString("turno"));
                                                    //fechapA.setText(String.valueOf(jsonArray.getJSONObject(i).getString("fecha_atencion")));
                                                    fechaeD.setText(String.valueOf(jsonArray.getJSONObject(i).getString("fecha_atencion")));
                                                    tipopagoD.setText(jsonArray.getJSONObject(i).getString("tipo_pago"));
                                                    repartidorD.setText(jsonArray.getJSONObject(i).getString("repartidor"));
                                                    comentarioD.setText(jsonArray.getJSONObject(i).getString("comentario"));
                                                    estadoD.setText(jsonArray.getJSONObject(i).getString("estado"));
                                                    total.setText("S/. "+jsonArray.getJSONObject(i).getString("total"));
                                                    listaDetallesPedido.add(p);
                                                }
                                                adapterProductosPedidoPasado = new AdapterProductosPedidoPasado(listaDetallesPedido);
                                                recyclerProductosPedidoPasado.setAdapter(adapterProductosPedidoPasado);
                                            } else if (respuesta.equals("2")) {
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
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> parametros = new HashMap<String, String>();
                                parametros.put("id_pedido", listaPedidos.get(position).getId() + "");
                                return parametros;
                            }
                        };
                        requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(stringRequest);
                    }
                    ///////////////////////////////////////////////
                    break;
                case R.id.btnEditarMiPedido:
                    if(!listaPedidos.get(position).getEstado().equals("ANULADO")) {
                        Pedido p = new Pedido();
                        p.setId(listaPedidos.get(position).getId());
                        p.setDireccion(position + "");
                        bus.post(p);
                    }
                    break;
                case R.id.btnEliminarMiPedido:
                    if(!listaPedidos.get(position).getEstado().equals("ANULADO")) {
                        dialogEliminar.show();
                        btnCloseEliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogEliminar.dismiss();
                            }
                        });
                        btnCancelarPedido.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isOnlineNet()) {
                                    requestQueue = Volley.newRequestQueue(context);
                                    String URL_CANCELARP = Utilidades.WEB_SERVICE + "?accion=CANCELAR_PEDIDO";
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CANCELARP,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    //BASTANTE ÚTIL PARA VER ERRORES
                                                    Log.i("tagconvertstr", "[" + response + "]");
                                                    try {
                                                        JSONObject rptaJson = new JSONObject(response);
                                                        String respuesta = rptaJson.getString("1");
                                                        if (respuesta.equals("1")) {
                                                            //ENVÍO UN STRING INDICANDO QUE SE REALIZÓ CORRECTAMENTE LA CANCELACIÓN
                                                            bus.post("S");
                                                            //CAMBIO EL ESTADO A ANULADO
                                                            etiEstado.setText("ANULADO");
                                                            etiEstado.setTextColor(Color.parseColor("#FF0000"));
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                etiEditar.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorNaranjaBajo));
                                                            }
                                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                                etiEliminar.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.colorRojoBajo));
                                                            }
                                                            listaPedidos.get(position).setEstado("ANULADO");
                                                            dialogEliminar.dismiss();
                                                        } else if (respuesta.equals("2")) {
                                                            //ENVÍO UN STRING INDICANDO QUE NO SE REALIZÓ CORRECTAMENTE LA CANCELACIÓN
                                                            bus.post("N");
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
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> parametros = new HashMap<String, String>();
                                            parametros.put("id_pedido", listaPedidos.get(position).getId() + "");
                                            return parametros;
                                        }
                                    };
                                    requestQueue = Volley.newRequestQueue(context);
                                    requestQueue.add(stringRequest);
                                }
                            }
                        });
                    }

                    ///////////////////////////////////////////////
                    break;
            }
        }

        public Boolean isOnlineNet() {
            try {
            /*Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");
            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;*/
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
