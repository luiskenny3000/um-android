package com.kaisapp.umessenger.data.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaisapp.umessenger.R;
import com.kaisapp.umessenger.data.models.MessageModel;

import java.util.ArrayList;

import static android.graphics.BitmapFactory.decodeByteArray;

/**
 * Created by kenny on 5/2/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    Context context;
    ArrayList<MessageModel> list;

    public MessageAdapter(Context context, ArrayList<MessageModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        MessageModel item = list.get(position);

        if(item.getType().equalsIgnoreCase(MessageModel.IMAGE)){
            holder.textView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);

            if (item.isLocal(context)) {
                holder.ll.setGravity(Gravity.RIGHT);
                holder.imageView.setBackgroundResource(R.drawable.bg_square_rounded_send);
                try {
                    byte[] decodedString = Base64.decode(item.getText(), Base64.DEFAULT);
                    holder.imageView.setImageBitmap(decodeByteArray(decodedString, 0, decodedString.length));
                    holder.imageView.setVisibility(View.VISIBLE);
                } catch (Exception e){
                    //e.printStackTrace();
                    holder.imageView.setVisibility(View.GONE);
                }
            } else {
                holder.ll.setGravity(Gravity.LEFT);
                holder.imageView.setBackgroundResource(R.drawable.bg_square_rounded_receive);
                try {
                    byte[] decodedString = Base64.decode(item.getText(), Base64.DEFAULT);
                    holder.imageView.setImageBitmap(decodeByteArray(decodedString, 0, decodedString.length));
                    holder.imageView.setVisibility(View.VISIBLE);
                } catch (Exception e){
                    //e.printStackTrace();
                    holder.imageView.setVisibility(View.GONE);
                }
            }
        } else {
            holder.textView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);

            holder.textView.setText(item.getText());
            if (item.isLocal(context)) {
                holder.ll.setGravity(Gravity.RIGHT);
                holder.textView.setBackgroundResource(R.drawable.bg_square_rounded_send);
                //holder.textView.setTextColor(context.getResources().getColor(android.R.color.white));
            } else {
                holder.ll.setGravity(Gravity.LEFT);
                holder.textView.setBackgroundResource(R.drawable.bg_square_rounded_receive);
                //holder.textView.setTextColor(context.getResources().getColor(R.color.textPrimary));
            }
        }

        /*
        if(item.getImage()!=null && !item.getImage().equalsIgnoreCase("")){
            holder.imageView.setVisibility(View.VISIBLE);

            byte[] image = Base64.decode(item.getImage(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            holder.imageView.setImageBitmap(bitmap);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        */
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_message_image, parent, false));
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll;
        TextView textView;
        ImageView imageView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ll = (LinearLayout)itemView.findViewById(R.id.ll);
            textView = (TextView)itemView.findViewById(R.id.tv_message);
            imageView = (ImageView)itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
