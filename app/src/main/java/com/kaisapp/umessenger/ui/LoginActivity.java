package com.kaisapp.umessenger.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kaisapp.umessenger.BuildConfig;
import com.kaisapp.umessenger.R;
import com.kaisapp.umessenger.utils.Util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.message;

/**
 * Created by kennyorellana on 27/3/17.
 */

public class LoginActivity extends AppCompatActivity {
    private final String TAG = LoginActivity.class.getSimpleName();

    OkHttpClient client;
    ProgressDialog progressDialog;
    EditText etPhoneNumber;
    EditText etPassword;
    Button buttonLogin;
    Button buttonSignup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Util.isLogged(this)) {
            showHomeActivity();
        } else {
            setContentView(R.layout.activity_login);
            setupView();
        }
    }

    private void setupView(){
        client = new OkHttpClient();

        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        etPhoneNumber = (EditText)findViewById(R.id.et_phone_number);
        etPassword = (EditText)findViewById(R.id.et_password);
        buttonLogin = (Button)findViewById(R.id.button_login);
        buttonSignup = (Button)findViewById(R.id.button_signup);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    login(etPhoneNumber.getText().toString(), etPassword.getText().toString());
                }

            }
        });

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValid()){
                    signup(etPhoneNumber.getText().toString(), etPassword.getText().toString());
                }
            }
        });

        etPhoneNumber.setText(Util.getPhoneNumber(this));
    }

    private boolean isValid(){
        boolean valid = true;

        if(!(etPhoneNumber.getText().toString().length()==8)){
            valid = false;
        }

        if(etPassword.getText().toString().length()<4){
            valid = false;
        }

        return valid;
    }

    private void login(String phoneNumber, String password){
        progressDialog.setMessage("Iniciando Sesión");
        progressDialog.show();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("celphone", phoneNumber)
                .addFormDataPart("password", password)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER + "/user/login")
                .post(requestBody)
                .build();

        Log.i(TAG, "sendMessage");

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "error "+e.toString());
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();

                String message = response.body().string();
                Log.i(TAG, "response " + response.toString()+", "+ message);
                if(message!=null && message.equalsIgnoreCase("true")) {
                    Util.setPhoneNumber(LoginActivity.this, etPhoneNumber.getText().toString());
                    Util.setLogged(LoginActivity.this, true);
                    showHomeActivity();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void signup(String phoneNumber, String password){
        progressDialog.setMessage("Registrando Usuario");
        progressDialog.show();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("celphone", phoneNumber)
                .addFormDataPart("password", password)
                .addFormDataPart("name", "")
                .addFormDataPart("photo","")
                .addFormDataPart("state", "")
                .addFormDataPart("deviceid", Util.getDeviceToken())
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER + "/user/setuser")
                .post(requestBody)
                .build();

        Log.i(TAG, "sendMessage " + new Gson().toJson(message));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "error "+e.toString());
                progressDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();

                String message = response.body().string();
                Log.i(TAG, "response " + response.toString()+", "+ message);
                if(message!=null && message.equalsIgnoreCase("true")) {
                    Util.setPhoneNumber(LoginActivity.this, etPhoneNumber.getText().toString());
                    Util.setLogged(LoginActivity.this, true);
                    showHomeActivity();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            intent.putExtras(getIntent().getExtras());
        }
        startActivity(intent);
        finish();
    }

}
