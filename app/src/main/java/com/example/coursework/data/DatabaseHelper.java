package com.example.coursework.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//связующий класс для бд и приложения
public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String dbname = "course_work.db";
    private static final int dbversion = 1;

    private final Context context;
    private final String dbpath;

    public DatabaseHelper(Context context) {
        super(context, dbname, null, dbversion);
        this.context=context;
        this.dbpath = context.getDatabasePath("course_work.db").getPath(); //по факту прописываем путь к файлу с бд
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //пропуск, т.к. бд уже готовая
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //создаем бд, по факту копируя уже готовую из папки assets
    public void createDataBase() throws IOException {
        if (!databaseExists()) {
            getReadableDatabase();
            copyDatabaseFromAssets();
        }
    }

    private boolean databaseExists() {
        File dbFile = new File(dbpath);
        return dbFile.exists();   //проверка на то, существует ли файл с бд
    }

    private void copyDatabaseFromAssets() throws IOException {

        InputStream input = context.getAssets().open("course_work.db");
        OutputStream output = new FileOutputStream(dbpath);  //создание потоков для считывания и записи бд

        byte[] buffer = new byte[1024];
        int length;

        while ((length = input.read(buffer))>0) {
            output.write(buffer, 0, length); //создаем буфер и считываем данные
        }

        output.flush();
        output.close();
        input.close();

    }

    public SQLiteDatabase openDataBase(){
        return SQLiteDatabase.openDatabase(dbpath, null, SQLiteDatabase.OPEN_READWRITE); //открываем бд для записи и чтения
    }

}
