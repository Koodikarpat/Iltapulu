package com.buutti.feilz.somebro;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
//import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.instagram.InstagramSocialNetwork;
import com.github.gorbin.asne.twitter.TwitterSocialNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LoginFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener {
    public static SocialNetworkManager mSocialNetworkManager;
    /**
     * SocialNetwork Ids in ASNE:
     * 1 - Twitter
     * 2 - LinkedIn
     * 3 - Google Plus
     * 4 - Facebook
     * 5 - Vkontakte
     * 6 - Odnoklassniki
     * 7 - Instagram
     */
    protected SMLoginHandler smlh;

    public static final int TWITTER =1;
    public static final int LINKEDIN =2;
    public static final int GOOGLE =3;
    public static final int FACEBOOK =4;
    public static final int INSTAGRAM =7;
    private LoginButton facebook;
    private Button twitter;
    private Button instagram;
    private Button nextButton;

    public CheckBox fbCB;
    public CheckBox twCB;

    public boolean isFbLoggedIn;
    public boolean isTwitterLoggedIn;

    private CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;

    MainActivity mainActivity;

    public LoginFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
     /*   accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newAccessToken) {
                updateWithToken(newAccessToken);
            }
        };*/
        super.onCreate(savedInstanceState);
        isTwitterLoggedIn = false;
        updateWithToken(AccessToken.getCurrentAccessToken());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.loginfragment, container, false);
        ((SMLoginHandler)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        // init buttons and set Listener

        facebook = (LoginButton) rootView.findViewById(R.id.facebook);

        callbackManager = CallbackManager.Factory.create();
        //facebook.setOnClickListener(loginClick);

        facebook.setFragment(this);
        facebook.setReadPermissions("email");


        facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                isFbLoggedIn = true;
                Log.i("SUCCESS", "SUCCESS");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(com.facebook.FacebookException error) {

            }
        });



        twitter = (Button) rootView.findViewById(R.id.twitter);
        twitter.setOnClickListener(loginClick);
        nextButton = (Button) rootView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(nextClick);
     /*   instagram = (Button) rootView.findViewById(R.id.instagram);
        instagram.setOnClickListener(loginClick);*/


        //Get Keys for initiate SocialNetworks
        String TWITTER_CONSUMER_KEY = getActivity().getString(R.string.twitter_consumer_key);
        String TWITTER_CONSUMER_SECRET = getActivity().getString(R.string.twitter_consumer_secret);
        String TWITTER_CALLBACK_URL = "oauth://ASNE";


        //Chose permissions
        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile, email, publish_actions, user_status"));
        //String linkedInScope = "r_basicprofile+r_fullprofile+rw_nus+r_network+w_messages+r_emailaddress+r_contactinfo";

        //Use manager to manage SocialNetworks
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SMLoginHandler.SOCIAL_NETWORK_TAG);

        //Check if manager exist
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            //Init and add to manager FacebookSocialNetwork
           // FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
            //mSocialNetworkManager.addSocialNetwork(fbNetwork);

            //Init and add to manager TwitterSocialNetwork
            TwitterSocialNetwork twNetwork = new TwitterSocialNetwork(this, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, TWITTER_CALLBACK_URL);
            mSocialNetworkManager.addSocialNetwork(twNetwork);

            /*InstagramSocialNetwork igNetwork = new InstagramSocialNetwork(this, "6ee691c04df7411a80e886bd73772f82", "cf62a9346c8c4c39afec109e282256a0", "127.0.0.1://redirect", "");*/
            //Initiate every network from mSocialNetworkManager

            getFragmentManager().beginTransaction().add(mSocialNetworkManager, SMLoginHandler.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);
        } else {
            //if manager exist - get and setup login only for initialized SocialNetworks
            if(!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    initSocialNetwork(socialNetwork);
                }
            }
        }
        mainActivity = new MainActivity();

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.i("SUCCESS", "SUCCESS2");
    }

    private void initSocialNetwork(SocialNetwork socialNetwork){
        if(socialNetwork.isConnected()){
            switch (socialNetwork.getID()){
             /*   case FacebookSocialNetwork.ID:
                    facebook.setText("Show Facebook profile");
                    isFbLoggedIn = true;
                    break;*/
                case TwitterSocialNetwork.ID:
                    twitter.setText("Logged in!");
                    isTwitterLoggedIn = true;
                    break;
                case InstagramSocialNetwork.ID:
                    instagram.setText("Show Instagram profile");
                    break;

            }
        }
    }
    @Override
    public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            initSocialNetwork(socialNetwork);
        }
    }

    //Login listener



    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int networkId = 0;
            Log.i("LOG IN", "click");
            switch (view.getId()){

                case R.id.twitter:
                    networkId = TwitterSocialNetwork.ID;
                    break;


            }
            SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
            if(!socialNetwork.isConnected()) {
                if(networkId != 0) {
                    socialNetwork.requestLogin();
                    SMLoginHandler.showProgress("Loading social person");
                } else {
                    Toast.makeText(getActivity(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
                socialNetwork.logout();
                twitter.setText("Log back in!");
            }
        }
    };

    @Override
    public void onLoginSuccess(int networkId) {
        SMLoginHandler.hideProgress();
        //startProfile(networkId);
        switch (networkId) {
           /* case FacebookSocialNetwork.ID:
                facebook.setText("Show Facebook profile");
                isFbLoggedIn = true;
                break;*/
            case TwitterSocialNetwork.ID:
                twitter.setText("Log out!");
                isTwitterLoggedIn = true;
                break;
            case InstagramSocialNetwork.ID:
                instagram.setText("Show Instagram profile");
                break;
        }
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        SMLoginHandler.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }

/*
    private void startProfile(int networkId){
        ProfileFragment profile = ProfileFragment.newInstance(networkId);
        getActivity().getSupportFragmentManager().beginTransaction()
                .addToBackStack("profile")
                .replace(R.id.container, profile)
                .commit();
    } */

private View.OnClickListener nextClick = new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        Log.i("LOG IN", "click");
        Intent i = new Intent(view.getContext(), MainActivity.class);

        String strFb;
        String strTwitter;

        if(isFbLoggedIn){
            strFb = "true";
        }else {
            strFb = "false";
        }
        if(isTwitterLoggedIn){
            strTwitter = "true";
        }else{
            strTwitter = "false";
        }

        i.putExtra("isFbLoggedIn", strFb);
        i.putExtra("isTwitterLoggedIn", strTwitter);
        startActivity(i);
    }
};

    private void updateWithToken(AccessToken currentAccessToken) {

        if (currentAccessToken != null) {
            isFbLoggedIn = true;
            Log.i("FB", "LOGGED IN");
        } else {
            isFbLoggedIn = false;
            Log.i("FB", "NOT LOGGED IN");
            }
        }
    }

