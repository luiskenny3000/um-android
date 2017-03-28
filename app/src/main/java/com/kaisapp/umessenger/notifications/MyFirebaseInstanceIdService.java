package com.kaisapp.umessenger.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.message;
import static com.kaisapp.umessenger.utils.Util.getIp;
import static com.kaisapp.umessenger.utils.Util.getPhoneNumber;

/**
 * Created by kennyorellana on 26/3/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static String SERVER_IP = "192.168.2.4";
    OkHttpClient client;
    private final static String TAG =  MyFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        SERVER_IP = getIp(this);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        updateToken(getPhoneNumber(this), refreshedToken);

        super.onTokenRefresh();
    }

    private void updateToken(String phoneNumber, String token){

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("celphone", phoneNumber)
                .addFormDataPart("device", token)
                .build();

        Request request = new Request.Builder()
                .url("http://" + SERVER_IP + "/chat/web/user/updateuser")
                .post(requestBody)
                .build();

        Log.i(TAG, "sendMessage " + new Gson().toJson(message));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "error "+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i(TAG, "response " + response.toString());
            }
        });
    }
}
