package com.traffico.manhattan.clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.traffico.manhattan.R;
import com.traffico.manhattan.entidades.Producto;

import java.util.ArrayList;

public class CustomAdapterListViewProduct extends BaseAdapter {

    Context context;
    ArrayList<Producto> productos;
    int imageEdit;
    LayoutInflater inflater;

    public CustomAdapterListViewProduct(Context context, ArrayList<Producto> productos, int imageEdit) {
        this.context = context;
        this.productos = productos;
        this.imageEdit = imageEdit;
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Override
    public Object getItem(int position) {
        return productos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productos.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txtTitle;
        ImageView imgImg;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.list_view, parent, false);

        // Locate the TextViews in listview_item.xml
        txtTitle = itemView.findViewById(R.id.list_row_title);
        imgImg = itemView.findViewById(R.id.list_row_image);

        // Capture position and set to the TextViews
        txtTitle.setText(productos.get(position).toString());
        imgImg.setImageResource(imageEdit);

        return itemView;


    }
}
