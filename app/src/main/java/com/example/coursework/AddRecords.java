package com.example.coursework;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.data.DAO;
import com.example.coursework.data.DatabaseHelper;

import java.io.IOException;
import java.util.List;

public class AddRecords extends AppCompatActivity {


    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    DAO dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_records);

        databaseHelper = new DatabaseHelper(this);
        try{
            databaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        db = databaseHelper.openDataBase();
        dao = new DAO(db);

        List<String> nameNarushenia = dao.getNameNarushenia();

        AutoCompleteTextView autoComplete = findViewById(R.id.autoList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nameNarushenia);
        autoComplete.setAdapter(adapter);

        int inspectorId = getSharedPreferences("user", MODE_PRIVATE)
                .getInt("inspector_id", -1);


    }
}
