package com.example.coursework;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.data.DAO;
import com.example.coursework.data.DatabaseHelper;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kotlinx.serialization.StringFormat;

public class AddRecords extends AppCompatActivity {

    EditText adress, passport, coment;
    ImageButton back, datePicker, create;
    TextView dateText;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    DAO dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_records);
        datePicker = findViewById(R.id.datePicker);
        dateText = findViewById(R.id.inputDate);
        adress = findViewById(R.id.inputName);
        passport = findViewById(R.id.inputPassport);
        coment = findViewById(R.id.inputComment);
        create = findViewById(R.id.buttonCreate);

        //подключение к бд
        back = findViewById(R.id.buttonBack);
        databaseHelper = new DatabaseHelper(this);
        try{
            databaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        db = databaseHelper.openDataBase();
        dao = new DAO(db);

        List<String> nameNarushenia = dao.getNameNarushenia();

        //настройка выпадающего списка
        AutoCompleteTextView autoComplete = findViewById(R.id.autoList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, nameNarushenia);
        autoComplete.setAdapter(adapter);

        //обработка кнопки "назад"
        back.setOnClickListener(v-> finish());

        //выбор даты
        datePicker.setOnClickListener(v -> showDatePicker());


        //кнопка создании записи
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //получение данных для сохраннеия записи
                int inspectorId = getSharedPreferences("user", MODE_PRIVATE)
                        .getInt("inspector_id", -1);

                String dateDb = dateText.getText().toString().trim();
                String narusheniaDb = autoComplete.getText().toString().trim();
                String adressDb = adress.getText().toString().trim();
                String passportDb = passport.getText().toString().trim();
                String comentDb = coment.getText().toString().trim();

                //првоерка заполненности полей
                if (dateDb.isEmpty() || narusheniaDb.isEmpty() ||
                        adressDb.isEmpty() || passportDb.isEmpty()) {

                    Toast.makeText(AddRecords.this,
                            "Заполните все обязательные поля",
                            Toast.LENGTH_SHORT).show();
                }

                //запись в бд
                boolean result = dao.addRecords(
                        dateDb,
                        adressDb,
                        passportDb,
                        comentDb,
                        narusheniaDb,
                        inspectorId
                );

                if (result) {
                    Toast.makeText(AddRecords.this,
                            "Запись успешно создана!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddRecords.this,
                            "Ошибка сохранения записи",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //показываем выборку даты для кнопки
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(
                            Locale.getDefault(),
                            "%02d.%02d.%d",
                            selectedDay,
                            selectedMonth + 1,
                            selectedYear
                    );
                    dateText.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()); //не ставим дату в будущем
    }
}
