package com.amtechnology.smartreplyassistant;

public class Contact {
    String name;
    String uid;

    public Contact(String name, String uid, String phoneno) {
        this.name = name;
        this.uid = uid;
        this.phoneno = phoneno;
    }

    String phoneno;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }
}
