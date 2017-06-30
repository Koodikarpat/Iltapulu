package com.buutti.feilz.somebro;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.buutti.feilz.somebro.R.id.parent;


public class MessageHistory extends AppCompatActivity {

    ViewTreeObserver vto;
    ProgressDialog pd;
    MainActivity mainActivity;
    ListView parent;
    CustomList adapter;

    DatabaseOperations db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagehistory);
        parent = (ListView)findViewById(R.id.msgHistory);
        parent.setAdapter(null);


            db=new DatabaseOperations(MessageHistory.this);
            //ViewGroup parent = (ViewGroup)findViewById(R.id.msgHistoryParent);

            ArrayList<String> vals = new ArrayList<>();
            ArrayList<String> valsPic = new ArrayList<>();
            Cursor res = db.getAllData();



            if (!(res.getCount()==0)){
                res.moveToFirst();
                do {
                    String pic=res.getString(res.getColumnIndex(DatabaseOperations.COL_2));
                    String post=res.getString(res.getColumnIndex(DatabaseOperations.COL_3));

                    valsPic.add(pic);
                    vals.add(post);

                } while (res.moveToNext());
            } else {
            /*TextView tv=new TextView(this);
            tv.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setText(R.string.noposts);
            parent.addView(tv);
                vals.add("No posts in the database");*/
            }

            adapter = new CustomList(MessageHistory.this, vals, valsPic);


            parent.setAdapter(adapter);
}
    }
/*    protected void showProgress(String message) {
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }
    protected void hideProgress() {
        pd.dismiss();
    }*/

