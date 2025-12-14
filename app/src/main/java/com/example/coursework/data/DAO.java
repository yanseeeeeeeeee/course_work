package com.example.coursework.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

//класс для работы с данными, создание методов для извлечения / записи
public class DAO {

    private SQLiteDatabase db;

    public DAO(SQLiteDatabase db) {
        this.db = db;
    }

    public List<String> getNameNarushenia() {
        List<String> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select name from narushenia", null);//запросик к бд

        if(cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public boolean checkUser(String login, String password) {

        String query = "select * from inspector where login = ? and password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{login, password});

        boolean exists = false;
        if(cursor.moveToFirst()){
            exists=true;
        }
        cursor.close();
        return exists;

    }

    public boolean checkLogin(String login) {
        String query = "select * from inspector where login = ?";
        Cursor cursor = db.rawQuery(query,new String[]{login});

        boolean exists = false;
        if(cursor.moveToFirst()) {
            exists=true;
        }
        cursor.close();
        return  exists;
    }

    public long insertUser(String password, String login,
            String name, String surname, String lastName,
                           String departament, String post) {

        ContentValues values = new ContentValues();
        values.put("password", password);
        values.put("login", login);
        values.put("name", name);
        values.put("last_name", surname);
        if (lastName == null || lastName.trim().isEmpty()) {
            values.putNull("second_name");
        } else {
            values.put("second_name", lastName.trim());
        }
        values.put("departament", departament);
        values.put("post", post);

        try {
            return db.insertOrThrow("inspector", null, values);
        } catch (SQLiteConstraintException e) {
            return -1;
        }

    }

    public String getName(String login) {
        String name = null;
        Cursor cursor = db.rawQuery("select name from inspector where login = ?", new String[]{login});

        if (cursor.moveToFirst()) {
            name = cursor.getString(0);
        }

        cursor.close();
        return name;
    }




}
