package com.example.coursework.ui.theme;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.ForgotPassword;
import com.example.coursework.Home;
import com.example.coursework.R;
import com.example.coursework.SignIn;
import com.example.coursework.data.DAO;
import com.example.coursework.data.DatabaseHelper;

import java.io.IOException;

public class Main extends AppCompatActivity {

    ImageButton signOn;
    EditText inputEmail, inputPassword;
    TextView forgotPassword, createAccount;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    DAO dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        createAccount= findViewById(R.id.createAccount);
        signOn = findViewById(R.id.signOn);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        forgotPassword = findViewById(R.id.forgotPassword);

        databaseHelper = new DatabaseHelper(this);
        try{
            databaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        db = databaseHelper.openDataBase();
        dao = new DAO(db);

        signOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                if(email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Main.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                } else {
                    checkUser(email, password);
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Main.this, ForgotPassword.class));
            }
        });

        //обработка кликабельного текста "создать аккаунт"
        String text = "Ещё не зарегистрированы? Создать аккаунт.";
        SpannableString spannableString = new SpannableString(text);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(Main.this, SignIn.class));
                overridePendingTransition(0,0); //убрать анимацию перехода экранов
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.darkset));
                ds.setUnderlineText(false);  //убрать подчеркивание текста
            }
        };

        int start = text.indexOf("Создать аккаунт");
        int end = start + "Создать аккаунт".length();

        spannableString.setSpan(clickableSpan,
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        createAccount.setText(spannableString);
        createAccount.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void checkUser(String email, String password) {
        if(dao.checkUser(email,password)){
            Toast.makeText(this, "Вход совершен", Toast.LENGTH_SHORT).show();

            String userName = dao.getName(email);

            Intent intent = new Intent(this, Home.class);
            intent.putExtra("user_name",userName);  //передача имени при успешном входе
            intent.putExtra("user_email", email);
            startActivity(intent);

        } else {
            Toast.makeText(this, "Неправильно введен логин или пароль", Toast.LENGTH_SHORT).show();
        }
    }



}
