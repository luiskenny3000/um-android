package com.kaisapp.umessenger.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private static int RESULT_LOAD_IMAGE = 1234;

    RecyclerView recyclerView;
    ArrayList<MessageModel> list = new ArrayList<>();
    EditText etMessage;
    ImageButton ivSend;
    MessageAdapter adapter;
    Cloudinary cloudinary;

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
                        getImage();
                        return true;
                    }
                }
                return false;
            }
        });

        Map config = new HashMap();
        config.put("cloud_name", "kaisapp");
        config.put("api_key", "338356817841841");
        config.put("api_secret", "kJKEupW3EXcrdanqdQ3Uywh1P");
        cloudinary = new Cloudinary(config);

        verifyStoragePermissions(this);
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
            handler.postDelayed(runnable, 10000);
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

    private void getImage(){
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            MessageModel messageModel = new MessageModel(resizeImage(picturePath), Util.getPhoneNumber(ChatActivity.this), contact.getCelphone(), MessageModel.IMAGE);
            sendMessage(messageModel);
            Log.i(TAG, "size" + messageModel.getText().length());
        }
    }

    private String resizeImage(String picture){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = DisplayMetrics.DENSITY_LOW;
        //options.outHeight = 160*DisplayMetrics.DENSITY_MEDIUM;

        Bitmap bitmap = BitmapFactory.decodeFile(picture, options);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();

        return Base64.encodeToString(bitmapdata, Base64.DEFAULT);
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
