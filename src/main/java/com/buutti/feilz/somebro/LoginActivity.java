package com.buutti.feilz.somebro;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
    EditText loginText;
    Button btn;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Point s = new Point();
        getWindowManager().getDefaultDisplay().getSize(s);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height=s.y/3;
        p.width=(int)(s.x/1.2);

        this.getWindow().setAttributes(p);
        this.setFinishOnTouchOutside(false);

        sp = getSharedPreferences("myLogin",MODE_PRIVATE);
        loginText = (EditText)findViewById(R.id.loginDialog);
        btn = (Button)findViewById(R.id.unblockButton);
        if (sp.getString("userPW",null)==null) {
            newUser();
        }else{
            oldUser();
        }
    }
    private void newUser(){
        final SharedPreferences.Editor spn= getSharedPreferences("myLogin",MODE_PRIVATE).edit();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("newUser","task");
                spn.putString("userPW",loginText.getText().toString());
                Toast.makeText(getApplicationContext(),"This password is required in the future to access this app",Toast.LENGTH_LONG).show();
                spn.apply();
                finish();
            }
        });

    }
    private void oldUser(){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("oldUser","task");
                if (sp.getString("userPW","null").equals(loginText.getText().toString())){
                    finish();
                } else{
                    Toast.makeText(getApplicationContext(),"Wrong password",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
