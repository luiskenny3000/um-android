package com.kaisapp.umessenger.data.models;

import android.content.Context;

import com.kaisapp.umessenger.utils.Util;

import java.io.Serializable;

/**
 * Created by kenny on 5/2/17.
 */

public class MessageModel implements Serializable {
    private transient boolean isLocal;
    private String transmitter;
    private String receiver;
    private String text;
    private String date;
    private String type;

    public MessageModel(String message) {
        this.text = message;
    }

    public MessageModel(String message, String transmitter, String receiver) {
        isLocal = true;
        this.transmitter = transmitter;
        this.receiver = receiver;
        this.text = message;
        this.date = Util.getDateString();
        this.type = "0";
    }

    public boolean isLocal(Context context) {
        if(context==null) {
            return isLocal;
        } else {
            return isLocal || Util.getPhoneNumber(context).equalsIgnoreCase(transmitter);
        }
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public String getTransmitter() {
        if(transmitter ==null){
            transmitter = "";
        }
        return transmitter;
    }

    public void setTransmitter(String transmitter) {
        this.transmitter = transmitter;
    }

    public String getReceiver() {
        if(receiver==null){
            receiver = "";
        }
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getText() {
        if(text ==null){
            text = "";
        }
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        if(date==null || date.equalsIgnoreCase("")){
            date = Util.getDateString();
        }
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        if(type==null){
            type = "0";
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
