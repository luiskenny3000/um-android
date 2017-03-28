package com.kaisapp.umessenger.data.models;

import java.io.Serializable;

/**
 * Created by kennyorellana on 25/3/17.
 */

public class ContactModel implements Serializable {
    private String name;
    private String phoneNumber;

    public String getName() {
        if(name==null){
            name = "";
        }
        return name;
    }

    public boolean contains(String query) {
        if(name==null || query==null) return false;

        return name.toLowerCase().contains(query.toLowerCase());
    }

    public ContactModel() {
    }

    public ContactModel(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        if(phoneNumber==null){
            phoneNumber = "";
        }
        phoneNumber = phoneNumber.replace("-","").replace(" ","").replace("+504","");
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
