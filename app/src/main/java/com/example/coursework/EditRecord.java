package com.example.coursework;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coursework.Controller.ModelRecords;
import com.example.coursework.data.DAO;
import com.example.coursework.data.DatabaseHelper;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditRecord extends AppCompatActivity {

    EditText adress, passport, coment;
    ImageButton back, datePicker, saveButton;
    TextView dateText;
    AutoCompleteTextView autoComplete;
    DatabaseHelper databaseHelper;
    DAO dao;

    int recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_record);

        dateText = findViewById(R.id.inputDate);
        datePicker = findViewById(R.id.datePicker);
        adress = findViewById(R.id.inputName);
        passport = findViewById(R.id.inputPassport);
        coment = findViewById(R.id.inputComment);
        autoComplete = findViewById(R.id.autoList);
        saveButton = findViewById(R.id.buttonCreate);
        back = findViewById(R.id.buttonBack);

        // Получаем ID записи из интента
        recordId = getIntent().getIntExtra("record_id", -1);
        if (recordId == -1) {
            Toast.makeText(this, "Ошибка загрузки записи", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Инициализация БД и DAO
        databaseHelper = new DatabaseHelper(this);
        try {
            databaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dao = new DAO(databaseHelper.openDataBase());

        // Заполняем список нарушений
        List<String> violations = dao.getNameNarushenia();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, violations);
        autoComplete.setAdapter(adapter);

        // Загрузка данных записи
        ModelRecords record = dao.getRecordById(recordId);
        if (record != null) {
            dateText.setText(record.date);
            adress.setText(record.adress);
            passport.setText(record.passport);
            coment.setText(record.coment);
            autoComplete.setText(record.narushenia_name);
        }

        // Кнопка выбора даты
        datePicker.setOnClickListener(v -> showDatePicker());

        // Кнопка назад
        back.setOnClickListener(v -> finish());

        // Кнопка сохранения изменений с подтверждением
        saveButton.setOnClickListener(v -> confirmSaveChanges());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%02d.%02d.%d",
                            dayOfMonth, month + 1, year);
                    dateText.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void confirmSaveChanges() {
        String date = dateText.getText().toString().trim();
        String narushenia = autoComplete.getText().toString().trim();
        String address = adress.getText().toString().trim();
        String pass = passport.getText().toString().trim();
        String comment = coment.getText().toString().trim();

        if (date.isEmpty() || narushenia.isEmpty() || address.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Заполните все обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Подтверждение")
                .setMessage("Вы уверены, что хотите изменить эту запись?")
                .setPositiveButton("Да", (dialog, which) -> saveChanges(date, address, pass, comment))
                .setNegativeButton("Нет", null)
                .show();
    }

    private void saveChanges(String date, String address, String pass, String comment) {
        boolean result = dao.updateRecord(recordId, date, address, pass, comment, autoComplete.getText().toString().trim());
        if (result) {
            Toast.makeText(this, "Запись успешно изменена", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Ошибка при сохранении изменений", Toast.LENGTH_SHORT).show();
        }
    }
}
