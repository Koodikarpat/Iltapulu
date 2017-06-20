package com.buutti.feilz.somebro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.github.gorbin.asne.core.SocialNetwork;


public class SMLoginHandler extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {
    private static ProgressDialog pd;
    public static final String SOCIAL_NETWORK_TAG = "SocialIntegrationMain.SOCIAL_NETWORK_TAG";
    static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);

        /*Point s = new Point();
        getWindowManager().getDefaultDisplay().getSize(s);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height=(int)(s.y/1.5);
        p.width=(int)(s.x/1.2);
        this.getWindow().setAttributes(p);
        */
        context = this;
        Intent i = new Intent(SMLoginHandler.this, LoginActivity.class);
        startActivity(i);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        homeAsUpByBackStack();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }
    }
    protected static void showProgress(String message) {
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    protected static void hideProgress() {
        pd.dismiss();
    }


    @Override
    public void onBackStackChanged() {
        homeAsUpByBackStack();
    }

    private void homeAsUpByBackStack() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SOCIAL_NETWORK_TAG);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        SocialNetwork sn = LoginFragment.mSocialNetworkManager.getSocialNetwork(LoginFragment.TWITTER);
        if (sn.isConnected()) sn.logout();
        sn=LoginFragment.mSocialNetworkManager.getSocialNetwork(LoginFragment.FACEBOOK);
        if (sn.isConnected()) sn.logout();
    }
}
