package com.amtechnology.smartreplyassistant;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Messages implements IMessage {
    private String id,text;
    private String created_at;
    private User user;

    public Messages(String id, String text, User user, String created_at) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.created_at = created_at;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Date getCreatedAt() {
        try {
            return new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH).parse(created_at);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}
