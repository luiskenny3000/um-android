package com.kaisapp.umessenger.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaisapp.umessenger.BuildConfig;
import com.kaisapp.umessenger.Constans;
import com.kaisapp.umessenger.R;
import com.kaisapp.umessenger.data.adapters.ContactsAdapter;
import com.kaisapp.umessenger.data.models.ContactModel;
import com.kaisapp.umessenger.utils.Util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.message;

/**
 * Created by kennyorellana on 25/3/17.
 */

public class HomeActivity extends AppCompatActivity implements ContactsAdapter.ContactsListener{
    private static final String TAG = HomeActivity.class.getSimpleName();
    public final static int PERMISSION_REQUEST_CONTACT = 1234;
    public final static int LOGIN = 9876;

    OkHttpClient client;

    SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    Cursor cursor;
    ContactsAdapter contactsAdapter;
    ArrayList<ContactModel> list = new ArrayList<>();
    ArrayList<ContactModel> listContacts = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        client = new OkHttpClient();
        setupView();
        askForContactPermission();
        Util.updateToken(this);
    }

    private void create(){

    }

    private void setupView(){
        searchView = (SearchView)findViewById(R.id.search_view);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL));
        contactsAdapter = new ContactsAdapter(this, list);
        recyclerView.setAdapter(contactsAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                contactsAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText!=null && !newText.equalsIgnoreCase("")){
                    list.clear();
                    for (int k = 0; k < listContacts.size(); k++) {
                        if (listContacts.get(k).contains(newText)) {
                            list.add(listContacts.get(k));
                        }
                    }
                } else {
                    list.clear();
                    list.addAll(listContacts);
                }
                contactsAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void getContacts(){
        String[] columns = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER, ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection = "(" + ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '1' AND (" + ContactsContract.Contacts.HAS_PHONE_NUMBER + " != 0 ))";

        ContentResolver cr = getContentResolver();
        cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, selection, null, null);
        //contactsAdapter.setCursor(cursor);
        //contactsAdapter.notifyDataSetChanged();

        getListNumbers();
    }

    private void getListNumbers(){
        if(cursor==null || cursor.getCount()==0) return;

        Cursor phones;
        while (cursor.moveToNext()){
            phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = "+cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)),null, null);

            if(phones!=null && phones.moveToNext()){
                listContacts.add(new ContactModel(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)), phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));
                phones.close();
            }
        }

        ArrayList<String> contacts = new ArrayList<>();

        for(ContactModel contact: listContacts){
            contacts.add(contact.getCelphone());
        }

        sendContacts(new Gson().toJson(contacts));
        //list.addAll(listContacts);
        //contactsAdapter.notifyDataSetChanged();
    }

    public void askForContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                            requestPermissions( new String[] { Manifest.permission.READ_CONTACTS }, PERMISSION_REQUEST_CONTACT);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CONTACT);
                }
            } else {
                getContacts();
            }
        } else{
            getContacts();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(this, "No tiene permiso para ver contactos", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onClick(ContactModel contact) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constans.BUNDLE, contact);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case LOGIN:
                    if(resultCode==Activity.RESULT_OK){
                        create();
                    } else {
                        finish();
                    }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    private void sendContacts(String contacts){

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("cellphones", contacts)
                .build();

        Request request = new Request.Builder()
                .url(BuildConfig.SERVER + "/user/getusers")
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
                String message = response.body().string();
                Log.i(TAG, "response " + message);

                Type listType = new TypeToken<ArrayList<ContactModel>>(){}.getType();
                ArrayList<ContactModel> listTemp = new Gson().fromJson(message, listType);

                for(ContactModel contact: listContacts){
                    for(ContactModel c: listTemp){
                        if(contact.getCelphone().equalsIgnoreCase(c.getCelphone())){
                            list.add(contact);
                        }
                    }
                }

                listContacts.clear();
                listContacts.addAll(list);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactsAdapter.notifyDataSetChanged();
                        checkDeepLink();
                    }
                });
            }
        });
    }

    private void checkDeepLink(){
        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            String number = bundle.getString("number","");

            if(!number.equalsIgnoreCase("")){
                Log.i("PushNotification", number);

                for(ContactModel contact: listContacts){
                    if(contact.getCelphone().equalsIgnoreCase(number)){
                        onClick(contact);
                        break;
                    }
                }
            }

        }
    }
}
