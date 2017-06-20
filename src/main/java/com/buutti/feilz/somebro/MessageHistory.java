package com.buutti.feilz.somebro;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MessageHistory extends AppCompatActivity {

    DatabaseOperations db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagehistory);
        db=new DatabaseOperations(this);
        //ViewGroup parent = (ViewGroup)findViewById(R.id.msgHistoryParent);
        ListView parent = (ListView)findViewById(R.id.msgHistory);
        ArrayList<String> vals = new ArrayList<>();
        Cursor res = db.getAllData();
        if (!(res.getCount()==0)){
            res.moveToFirst();
            do {
                //String pic=res.getString(res.getColumnIndex(DatabaseOperations.COL_2));
                String post=res.getString(res.getColumnIndex(DatabaseOperations.COL_3));
                //TextView tv = new TextView(this);
                //tv.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //tv.setText(post);
                //parent.addView(tv);
                vals.add(post);
            } while (res.moveToNext());
        } else {
            /*TextView tv=new TextView(this);
            tv.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setText(R.string.noposts);
            parent.addView(tv);*/
            vals.add("No posts in the database");
        }
        ArrayAdapter<String> adpt = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,vals);
        parent.setAdapter(adpt);

    }
}
