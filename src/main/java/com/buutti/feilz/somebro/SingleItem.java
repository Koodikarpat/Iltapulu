package com.buutti.feilz.somebro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class SingleItem extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_item);


      /*  Point s = new Point();
        getWindowManager().getDefaultDisplay().getSize(s);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height=s.y/5;
        p.width=(int)(s.x/1.2);

        this.getWindow().setAttributes(p);*/
        this.setFinishOnTouchOutside(true);

        Intent i = getIntent(); // gets the previously created intent
        String imageId = i.getStringExtra("ImageID");

        ImageView imageView = (ImageView) findViewById(R.id.singleImageView);
        Bitmap myBitmap = BitmapFactory.decodeFile(imageId);
        imageView.setImageBitmap(myBitmap);

    }
}
