package com.kaisapp.umessenger.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.kaisapp.umessenger.utils.Util;

import okhttp3.OkHttpClient;

import static com.kaisapp.umessenger.utils.Util.getPhoneNumber;

/**
 * Created by kennyorellana on 26/3/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    OkHttpClient client = new OkHttpClient();
    private final static String TAG =  MyFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        Util.updateToken(getPhoneNumber(this), refreshedToken);

        super.onTokenRefresh();
    }
}
