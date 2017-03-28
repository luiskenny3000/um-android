package com.kaisapp.umessenger.data.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaisapp.umessenger.R;
import com.kaisapp.umessenger.data.models.ContactModel;

/**
 * Created by kennyorellana on 25/3/17.
 */

public class Contacts2Adapter extends RecyclerView.Adapter<Contacts2Adapter.ViewHolder> {
    private final static String TAG = ContactsContract.Contacts.class.getSimpleName();
    Context context;
    Cursor cursor;
    ContactsListener listener;

    public interface ContactsListener{
        public void onClick(ContactModel contact);
    }

    public Contacts2Adapter(Context context) {
        this.context = context;
        this.listener = (ContactsListener)context;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvPhoneNumber;
        ImageView ivMessage;
        public ViewHolder(View v){
            super(v);
            tvName = (TextView)v.findViewById(R.id.tv_name);
            tvPhoneNumber = (TextView)v.findViewById(R.id.tv_phone_number);
            ivMessage = (ImageView)v.findViewById(R.id.iv_message);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(cursor==null){
            holder.tvName.setText("");
            holder.tvPhoneNumber.setText("");
            return;
        }

        cursor.moveToPosition(position);

        final ContactModel contact = new ContactModel();
        contact.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

        Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = "+cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)),null, null);

        if(phones!=null && phones.moveToNext()){
            contact.setPhoneNumber(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            phones.close();
        }

        holder.tvName.setText(contact.getName());
        holder.tvPhoneNumber.setText(contact.getPhoneNumber());

        holder.ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(cursor!=null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

}
