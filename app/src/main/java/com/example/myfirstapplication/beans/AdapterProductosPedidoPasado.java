package com.example.myfirstapplication.beans;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myfirstapplication.R;

import java.util.ArrayList;

public class AdapterProductosPedidoPasado extends RecyclerView.Adapter<AdapterProductosPedidoPasado.ViewHolderProductos> {

    ArrayList<ProductoPo> listaProductos;

    public AdapterProductosPedidoPasado(ArrayList<ProductoPo> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @Override
    public ViewHolderProductos onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_ver_pedido, null, false);
        return new ViewHolderProductos(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderProductos holder, int position) {
        holder.etiNombre.setText(listaProductos.get(position).getNombre());
        holder.etiDescripcion.setText(listaProductos.get(position).getDescripcion());
        holder.etiPrecio.setText("PRECIO: S/. " + listaProductos.get(position).getPrecio().toString());
        holder.etiCantidad.setText(listaProductos.get(position).getCantidad() + " UNID.");
        holder.etiSubtotal.setText("S/. " + listaProductos.get(position).getSubtotal().toString());
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public class ViewHolderProductos extends RecyclerView.ViewHolder {
        TextView etiNombre, etiDescripcion, etiCantidad, etiSubtotal, etiPrecio;

        public ViewHolderProductos(View itemView) {
            super(itemView);
            etiNombre = (TextView) itemView.findViewById(R.id.nombreProductoMiPedido);
            etiDescripcion = (TextView) itemView.findViewById(R.id.descripcionProductoMiPedido);
            etiPrecio = (TextView) itemView.findViewById(R.id.precioProductoMiPedido);
            etiCantidad = (TextView) itemView.findViewById(R.id.cantidadProductoMiPedido);
            etiSubtotal = (TextView) itemView.findViewById(R.id.subtotalProductoMiPedido);
        }
    }
}
