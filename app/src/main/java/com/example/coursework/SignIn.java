package com.example.coursework;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.data.DAO;
import com.example.coursework.data.DatabaseHelper;
import com.example.coursework.ui.theme.Main;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;


public class SignIn extends AppCompatActivity {

    ImageButton button, back;
    EditText inputName, inputEmail, createPassword, coniformPassword, inputPost, inputPatronymic, inputDepartament, inputSurname;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    DAO dao;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        button = findViewById(R.id.signIn);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        createPassword = findViewById(R.id.CreatePassword);
        coniformPassword = findViewById(R.id.ConfirmPassword);
        inputPost = findViewById(R.id.inputPosition);
        inputDepartament = findViewById(R.id.inputDepartment);
        inputPatronymic = findViewById(R.id.inputPatronymic);
        inputSurname = findViewById(R.id.inputSurname);
        back = findViewById(R.id.back);

        databaseHelper = new DatabaseHelper(this);
        try{
            databaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        db = databaseHelper.openDataBase();
        dao = new DAO(db);

        //обработка по кнопке "зарегистрироваться"
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String name = inputName.getText().toString().trim();
                String password = createPassword.getText().toString();
                String toPassword = coniformPassword.getText().toString();
                String post = inputPost.getText().toString().trim();
                String departament = inputDepartament.getText().toString().trim();
                String patronymic = inputPatronymic.getText().toString().trim();
                String surname = inputSurname.getText().toString().trim();

                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || toPassword.isEmpty()
                || post.isEmpty() || departament.isEmpty() || surname.isEmpty()) {
                    Toast.makeText(SignIn.this,"Заполните все пустые поля", Toast.LENGTH_SHORT).show();
                }
                else if (!(password.equals(toPassword))) {
                    Toast.makeText(SignIn.this,"Введённые пароли не совпадают", Toast.LENGTH_SHORT).show();
                    //паттерн для проверки правильного написания email
                } else if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignIn.this, "Неверный формат email", Toast.LENGTH_SHORT).show();
                }
                    else { //запись в бд + переход на след. активити

                        if(createAccount(password, email, name, surname, patronymic, departament, post)){
                            int inspectorId = dao.getIdInspector(email); // получаем id нового инспектора
                            String userName = dao.getName(email);        // получаем имя нового пользователя

                            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                            prefs.edit()
                                    .putString("user_name", userName)
                                    .putString("user_email", email)
                                    .putInt("inspector_id", inspectorId)
                                    .apply();
                            startActivity(new Intent(SignIn.this, Home.class));
                            overridePendingTransition(0,0);}
                        }
            }
        });
        //обработка по кнопке "назад"
        back.setOnClickListener(v -> finish());
    }

    public boolean createAccount(String password, String email, String name,
                              String surname, String lastname,
                              String departament, String post) {
        boolean res = false;
            long result = dao.insertUser(password, email, name, surname,lastname.isEmpty() ? null : lastname,
                    departament,post);

            if (result == -1) {
                Toast.makeText(this, "Такой пользователь уже существует", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Регистрация завершена", Toast.LENGTH_SHORT).show();
                res = true;
            }
            return res;
    }

}
