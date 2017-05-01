package com.kaisapp.umessenger.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaisapp.umessenger.BuildConfig;
import com.kaisapp.umessenger.Constans;
import com.kaisapp.umessenger.InputThread;
import com.kaisapp.umessenger.R;
import com.kaisapp.umessenger.data.adapters.MessageAdapter;
import com.kaisapp.umessenger.data.models.ContactModel;
import com.kaisapp.umessenger.data.models.MessageModel;
import com.kaisapp.umessenger.utils.Util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity implements InputThread.MessageListener {
    ContactModel contact;
    OkHttpClient client;

    private static final String TAG = ChatActivity.class.getSimpleName();

    RecyclerView recyclerView;
    ArrayList<MessageModel> list = new ArrayList<>();
    EditText etMessage;
    ImageButton ivSend;
    MessageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        client = new OkHttpClient();

        setupView();
        getData();
        setupData();

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        etMessage = (EditText)findViewById(R.id.et_message);
        ivSend =  (ImageButton) findViewById(R.id.iv_send);

        adapter = new MessageAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(etMessage.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(ChatActivity.this, "Mensaje Vacío", Toast.LENGTH_LONG).show();
                } else {
                    list.add(new MessageModel(etMessage.getText().toString(), Util.getPhoneNumber(ChatActivity.this), contact.getCelphone()));
                    adapter.notifyDataSetChanged();
                    sendMessage(new MessageModel(etMessage.getText().toString(), Util.getPhoneNumber(ChatActivity.this), contact.getCelphone()));
                    etMessage.setText("");
                }
            }
        });

        etMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etMessage.getRight() - etMessage.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void getData(){
        contact = (ContactModel)getIntent().getExtras().getSerializable(Constans.BUNDLE);
    }

    private void setupView(){
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupData(){
        setTitle(contact.getName());
    }

    @Override
    public void addMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //list.add(new MessageModel(message, false));
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getMessages(){
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BuildConfig.SERVER + "/message/getmessage").newBuilder();
        urlBuilder.addQueryParameter("transmitter", Util.getPhoneNumber(this));
        urlBuilder.addQueryParameter("receiver", contact.getCelphone());
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "error "+e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                if(json == null || json.equalsIgnoreCase("") || !json.substring(0,1).equalsIgnoreCase("[")) return;

                Log.i(TAG, "getText " + json);

                Type listType = new TypeToken<ArrayList<MessageModel>>(){}.getType();
                ArrayList<MessageModel> listTemp = new Gson().fromJson(json, listType);

                if(listTemp.size()>0) {
                    list.clear();
                    list.addAll(listTemp);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void sendMessage(MessageModel message){

        recyclerView.scrollToPosition(adapter.getItemCount() - 1);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("transmitter", message.getTransmitter())
                .addFormDataPart("receiver", message.getReceiver())
                .addFormDataPart("date", message.getDate())
                .addFormDataPart("type", message.getType())
                .addFormDataPart("text", message.getText())
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER + "/message/setmessage")
                //.url("https://pure-tundra-47231.herokuapp.com/web/message/setmessage")
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

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getMessages();
                }
            });
            handler.postDelayed(runnable, 1000);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        //SERVER_IP = getIp(ChatActivity.this);
        getMessages();
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1000);
    }
}
