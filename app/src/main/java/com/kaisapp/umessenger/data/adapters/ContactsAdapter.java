package com.kaisapp.umessenger.data.adapters;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaisapp.umessenger.R;
import com.kaisapp.umessenger.data.models.ContactModel;

import java.util.ArrayList;

/**
 * Created by kennyorellana on 25/3/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private final static String TAG = ContactsContract.Contacts.class.getSimpleName();
    Context context;
    ContactsListener listener;
    ArrayList<ContactModel> list;

    public interface ContactsListener{
        public void onClick(ContactModel contact);
    }

    public ContactsAdapter(Context context, ArrayList<ContactModel> list) {
        this.context = context;
        this.listener = (ContactsListener)context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout rl;
        TextView tvName;
        TextView tvPhoneNumber;
        ImageView ivMessage;
        public ViewHolder(View v){
            super(v);
            rl = (RelativeLayout)v.findViewById(R.id.rl);
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
        final ContactModel contact = list.get(position);

        holder.tvName.setText(contact.getName());
        holder.tvPhoneNumber.setText(contact.getCelphone());

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null) {
            return list.size();
        } else {
            return 0;
        }
    }

}
