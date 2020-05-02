package com.example.myfirstapplication.beans;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myfirstapplication.R;
import com.example.myfirstapplication.utilidades.Utilidades;

import java.util.ArrayList;
import java.util.Random;

public class AdapterProductos
        extends RecyclerView.Adapter<AdapterProductos.ViewHolderProductos>
        implements View.OnClickListener {

    ArrayList<Producto> listaProductos;
    private View.OnClickListener listener;
    private Context context;

    public AdapterProductos(ArrayList<Producto> listaProductos, Context context) {
        this.listaProductos = listaProductos;this.context = context;
    }

    @Override
    public ViewHolderProductos onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler, null, false);
        view.setOnClickListener(this);
        return new ViewHolderProductos(view);
    }

    @Override
    public void onBindViewHolder(ViewHolderProductos holder, int position) {
        float stockp = Float.parseFloat(listaProductos.get(position).getStock().toString());
        int stock = (int) stockp;
        holder.etiId.setText(listaProductos.get(position).getId()+"");
        holder.etiNombre.setText(listaProductos.get(position).getNombre());
        holder.etiDescripcion.setText(listaProductos.get(position).getDescripcion());
        holder.etiStock.setText(stock+"");
        holder.etiPrecio.setText("S/. " +listaProductos.get(position).getPrecio().toString());
        /*if (listaProductos.get(position).getFoto() != null) {
            holder.foto.setImageBitmap(listaProductos.get(position).getFoto());
        } else {
            holder.foto.setImageResource(R.mipmap.ic_launcher);
        }*/
        Glide.with(context)
                .load(Utilidades.WEB_IMAGEN_PRODUCTO + listaProductos.get(position).getRuta())
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .placeholder(R.drawable.panadero)
                .error(R.drawable.panadero)
                .into(holder.foto);
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null) {
            listener.onClick(v);
        }
    }

    public class ViewHolderProductos extends RecyclerView.ViewHolder {
        TextView etiId, etiNombre, etiDescripcion, etiPrecio, etiStock;
        ImageView foto;
        Button btnAgregarCarrito;
        public ViewHolderProductos(View itemView) {
            super(itemView);
            etiId = (TextView) itemView.findViewById(R.id.idProducto);
            etiNombre = (TextView) itemView.findViewById(R.id.nombreProducto);
            etiDescripcion = (TextView) itemView.findViewById(R.id.descripcionProducto);
            etiPrecio = (TextView) itemView.findViewById(R.id.precioProducto);
            etiStock = (TextView) itemView.findViewById(R.id.stockProducto);
            foto = (ImageView) itemView.findViewById(R.id.imagenProducto);
        }
    }
}