package com.buutti.feilz.somebro;
/*
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class CustomList extends ArrayAdapter<Product>{

    private final Activity context;
    private final String[] web;
    private final Integer[] imageId;
    DatabaseOperations db;

    public CustomList(Activity context,
                      String[] web, Integer[] imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(web[position]);

        //imageView.setImageBitmap(MainActivity.bitmap);

        imageView.setImageResource(getRecentImages);
        return rowView;
    }
    private ArrayList<String> getRecentImages(long from, long to,
                                              ArrayList<String> list) {
        ArrayList<String> sortedList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            File file = new File(list.get(i));
            long modified = file.lastModified();
            if (modified > from && modified <= to) {
                sortedList.add(list.get(i));
            }
        }
        return sortedList;
    }
}*/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomList extends ArrayAdapter<String>{
    private final Activity context;
    View rowView;
    private final ArrayList<String> web;
    private final ArrayList<String> imageId;
    public CustomList(Activity context,
                      ArrayList<String> web, ArrayList<String> imageId) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;


    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {

            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_single, null, true);


        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
            }
        });

        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);

        txtTitle.setText(web.get(position));

        if(!imageId.isEmpty()) {
            //imageView.setImageResource(imageId.get(position));
            Bitmap myBitmap = BitmapFactory.decodeFile(imageId.get(position));

            imageView.setImageBitmap(myBitmap);
        }

        return rowView;
    }
}