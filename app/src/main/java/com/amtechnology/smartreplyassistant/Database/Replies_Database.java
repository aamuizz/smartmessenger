package com.amtechnology.smartreplyassistant.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stfalcon.chatkit.messages.MessageInput;

public class Replies_Database extends SQLiteOpenHelper {
    public static final String Databasefilename = "allreplies.db";
    public static final int Databaseversion = 1;
    public static final String AllReplies_Tablename = "AllReplies";
    public static final String Columnid = "_id";
    public static final String Message = "Message";
    public static final String Reply = "Reply";
    public Replies_Database(Context context) {
        super(context, Databasefilename, null, Databaseversion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        sqLiteDatabase.execSQL("CREATE TABLE "+
//                Notification_Tablename+"("+Notification_Columnid
//        +"INTEGER PRIMARY KEY, "+Notification_Message);
        sqLiteDatabase.execSQL("create table "+AllReplies_Tablename+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,Message TEXT,Reply TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+AllReplies_Tablename);
        onCreate(sqLiteDatabase);

    }
    public boolean insert_Reply(String message,String reply){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Message,message);
        contentValues.put(Reply,reply);
        db.insert(AllReplies_Tablename,null,contentValues);
        return true;
    }

    public Cursor getAllReplies(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+AllReplies_Tablename,null);
    }
}
