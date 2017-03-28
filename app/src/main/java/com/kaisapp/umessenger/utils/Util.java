package com.kaisapp.umessenger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by kenny on 12/2/17.
 */

public class Util {
    private static final Pattern REGEX_IP = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final String DATA = "data";
    private static final String IP = "ip";
    private static final String LOGGED = "logged";
    private static final String IP_DEFAULT = "192.168.1.5";
    private static final String PHONE_NUMBER ="phoneNumber";
    private static final int PORT_DEFAULT = 10000;


    public static String getIp(Context context){
        if(context==null) return IP_DEFAULT;

        String ip;

        SharedPreferences sp = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        ip = sp.getString(IP, IP_DEFAULT);

        if(isValidIp(ip)){
            return ip;
        } else {
            return IP_DEFAULT;
        }
    }

    public static void setIp(Context context, String ip){
        if(context==null || !isValidIp(ip)) return;

        SharedPreferences sp = context.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(IP, ip);
        editor.apply();
    }

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

    public static boolean isValidIp(String ip){
        if(ip==null || ip.equalsIgnoreCase("")) return false;

        return REGEX_IP.matcher(ip).matches();
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

    public static boolean isPhoneNumber(String ip){
        return ip.length()==8;
    }

    public static String getDateString(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
}
