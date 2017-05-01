package com.kaisapp.umessenger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kaisapp.umessenger.BuildConfig;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kenny on 12/2/17.
 */

public class Util {
    private static final Pattern REGEX_IP = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final String DATA = "data";
    private static final String LOGGED = "logged";
    private static final String PHONE_NUMBER ="phoneNumber";

    public static boolean isLogged(Context context){
        if(context==null) return false;

        SharedPreferences sp = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sp.getBoolean(LOGGED, false);
    }

    public static void setLogged(Context context, boolean logged){
        if(context==null) return;

        SharedPreferences sp = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(LOGGED, logged);
        editor.apply();
    }

    public static String getPhoneNumber(Context context){
        if(context==null) return "";

        String phoneNumber;

        SharedPreferences sp = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        phoneNumber = sp.getString(PHONE_NUMBER, "");

        return phoneNumber;
    }

    public static void setPhoneNumber(Context context, String port){
        if(context==null ) return;

        SharedPreferences sp = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(PHONE_NUMBER, port);
        editor.apply();
    }

    public static String getDateString(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = "";

        try {
            date = format.format(calendar.getTime());
        } catch (Exception e){
            e.printStackTrace();
        }

        return date;
    }

    public static String getDeviceToken(){
        String token = "";
        try {
            token = FirebaseInstanceId.getInstance().getToken();
        } catch (Exception e) {
            Log.i("Firebase", "Error getDeviceToken");
        }

        return token;
    }

    public static void updateToken(String phoneNumber, String token){
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("celphone", phoneNumber)
                .addFormDataPart("device", token)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER + "user/updateuser")
                .post(requestBody)
                .build();

        Log.i("UpdateToken", "token: " + token);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("UpdateToken", "error "+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String message = response.message();
                Log.i("UpdateToken", "response " + message);
            }
        });
    }

    public static void updateToken(Context context){
        updateToken(getPhoneNumber(context), getDeviceToken());
    }
}
