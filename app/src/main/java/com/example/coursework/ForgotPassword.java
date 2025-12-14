package com.example.coursework;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.data.DAO;
import com.example.coursework.data.DatabaseHelper;
import com.example.coursework.ui.theme.Main;

import java.io.IOException;

public class ForgotPassword extends AppCompatActivity {

    EditText inputEmail;
    ImageButton back, button_continue;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    DAO dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        inputEmail = findViewById(R.id.inputEmail);
        back = findViewById(R.id.back);
        button_continue = findViewById(R.id.button_continue);

        databaseHelper = new DatabaseHelper(this);
        try{
            databaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        db = databaseHelper.openDataBase();
        dao = new DAO(db);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPassword.this, Main.class));
            }
        });

        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputEmail.getText().toString().isEmpty()) {
                    Toast.makeText(ForgotPassword.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                } //проверка существования пользователя по бд
                else {
                    checkLogin(inputEmail.getText().toString().trim());
                }

            }
        });
    }

    public void checkLogin(String email) {
        if(dao.checkLogin(email)) {
            Toast.makeText(this, "Инструкция по восстановлению пароля отправлена на почту", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
        }

    }
}
