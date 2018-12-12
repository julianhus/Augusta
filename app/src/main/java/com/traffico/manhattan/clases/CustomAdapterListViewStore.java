package com.traffico.manhattan.clases;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.traffico.manhattan.R;
import com.traffico.manhattan.entidades.Tienda;

import java.util.ArrayList;

public class CustomAdapterListViewStore extends BaseAdapter {
    Context context;
    ArrayList<Tienda> tiendas;
    int imageEdit;
    LayoutInflater inflater;

    public CustomAdapterListViewStore(Context context, ArrayList<Tienda> tiendas, int imageEdit) {
        this.context = context;
        this.tiendas = tiendas;
        this.imageEdit = imageEdit;
    }

    @Override
    public int getCount() {
        return tiendas.size();
    }

    @Override
    public Object getItem(int position) {
        return tiendas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tiendas.get(position).getId();
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
        txtTitle.setText(tiendas.get(position).toString());
        txtTitle.setTextColor(Color.BLACK);
        imgImg.setImageResource(imageEdit);

        return itemView;


    }


    /*
    Context context;
    String[] titulos;
    int[] imagenes;
    LayoutInflater inflater;

    public CustomAdapterListViewStore(Context context, String[] titulos, int[] imagenes) {
        this.context = context;
        this.titulos = titulos;
        this.imagenes = imagenes;
    }

    @Override
    public int getCount() {
        return titulos.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Declare Variables
        TextView txtTitle;
        ImageView imgImg;

        //http://developer.android.com/intl/es/reference/android/view/LayoutInflater.html
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.list_view, parent, false);

        // Locate the TextViews in listview_item.xml
        txtTitle = (TextView) itemView.findViewById(R.id.list_row_title);
        imgImg = (ImageView) itemView.findViewById(R.id.list_row_image);

        // Capture position and set to the TextViews
        txtTitle.setText(titulos[position]);
        imgImg.setImageResource(imagenes[position]);

        return itemView;
    }
    */
}
