package com.buutti.feilz.somebro;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
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
    public static final int TWITTER =1;
    public static final int LINKEDIN =2;
    public static final int GOOGLE =3;
    public static final int FACEBOOK =4;
    public static final int INSTAGRAM =7;
    private Button facebook;
    private Button twitter;
    private Button instagram;

    public CheckBox fbCB;
    public CheckBox twCB;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.loginfragment, container, false);
        ((SMLoginHandler)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        // init buttons and set Listener
        facebook = (Button) rootView.findViewById(R.id.facebook);
        facebook.setOnClickListener(loginClick);
        twitter = (Button) rootView.findViewById(R.id.twitter);
        twitter.setOnClickListener(loginClick);


        //Get Keys for initiate SocialNetworks
        String TWITTER_CONSUMER_KEY = getActivity().getString(R.string.twitter_consumer_key);
        String TWITTER_CONSUMER_SECRET = getActivity().getString(R.string.twitter_consumer_secret);
        String TWITTER_CALLBACK_URL = "oauth://ASNE";


        //Chose permissions
        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile, email"));
        //String linkedInScope = "r_basicprofile+r_fullprofile+rw_nus+r_network+w_messages+r_emailaddress+r_contactinfo";

        //Use manager to manage SocialNetworks
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SMLoginHandler.SOCIAL_NETWORK_TAG);

        //Check if manager exist
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            //Init and add to manager FacebookSocialNetwork
            FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
            mSocialNetworkManager.addSocialNetwork(fbNetwork);

            //Init and add to manager TwitterSocialNetwork
            TwitterSocialNetwork twNetwork = new TwitterSocialNetwork(this, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, TWITTER_CALLBACK_URL);
            mSocialNetworkManager.addSocialNetwork(twNetwork);

            //InstagramSocialNetwork igNetwork = new InstagramSocialNetwork(this,)
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
        return rootView;
    }

    private void initSocialNetwork(SocialNetwork socialNetwork){
        if(socialNetwork.isConnected()){
            switch (socialNetwork.getID()){
                case FacebookSocialNetwork.ID:
                    facebook.setText("Show Facebook profile");
                    break;
                case TwitterSocialNetwork.ID:
                    twitter.setText("Show Twitter profile");
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
            switch (view.getId()){
                case R.id.facebook:
                    networkId = FacebookSocialNetwork.ID;
                    break;
                case R.id.twitter:
                    networkId = TwitterSocialNetwork.ID;
                    break;
                case R.id.instagram:
                    networkId=InstagramSocialNetwork.ID;

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
                //startProfile(socialNetwork.getID());
            }
        }
    };

    @Override
    public void onLoginSuccess(int networkId) {
        SMLoginHandler.hideProgress();
        //startProfile(networkId);
        switch (networkId) {
            case FacebookSocialNetwork.ID:
                facebook.setText("Show Facebook profile");
                break;
            case TwitterSocialNetwork.ID:
                twitter.setText("Show Twitter profile");
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
}
