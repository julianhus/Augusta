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
import com.traffico.manhattan.entidades.Mercado;

import java.util.ArrayList;

public class CustomAdapterListViewShopping extends BaseAdapter {

        Context context;
        ArrayList<Mercado> mercados;
        int imageEdit;
        LayoutInflater inflater;

        public CustomAdapterListViewShopping(Context context, ArrayList<Mercado> mercados, int imageEdit) {
            this.context = context;
            this.mercados = mercados;
            this.imageEdit = imageEdit;
        }

        @Override
        public int getCount() {
            return mercados.size();
        }

        @Override
        public Object getItem(int position) {
            return mercados.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mercados.get(position).getId();
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
            txtTitle.setText(mercados.get(position).toString());
            txtTitle.setTextColor(Color.BLACK);
            imgImg.setImageResource(imageEdit);

            return itemView;


        }
    }