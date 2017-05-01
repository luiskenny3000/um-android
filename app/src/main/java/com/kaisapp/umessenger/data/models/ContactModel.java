package com.kaisapp.umessenger.data.models;

import java.io.Serializable;

/**
 * Created by kennyorellana on 25/3/17.
 */

public class ContactModel implements Serializable {
    private String name;
    private String celphone;

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

    public ContactModel(String name, String celphone) {
        this.name = name;
        this.celphone = celphone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCelphone() {
        if(celphone ==null){
            celphone = "";
        }
        celphone = celphone.replace("-","").replace(" ","").replace("+504","");
        return celphone;
    }

    public void setCelphone(String celphone) {
        this.celphone = celphone;
    }
}
