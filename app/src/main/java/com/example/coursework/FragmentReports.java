package com.example.coursework;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.coursework.Controller.ModelRecords;
import com.example.coursework.Controller.ReportGenerator;
import com.example.coursework.R;
import com.example.coursework.data.DAO;
import com.example.coursework.data.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FragmentReports extends Fragment {

    private TextView startDate, endDate, tvRecordCount;
    private MaterialButtonToggleGroup toggleGroup;
    private MaterialButton btnGenerate;
    private DAO dao;
    private int inspectorId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        startDate = view.findViewById(R.id.startDate);
        endDate = view.findViewById(R.id.endDate);
        tvRecordCount = view.findViewById(R.id.tvRecordCount);
        toggleGroup = view.findViewById(R.id.exportToggle);
        btnGenerate = view.findViewById(R.id.btnGenerate);

        dao = new DAO(new DatabaseHelper(requireContext()).openDataBase());
        inspectorId = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
                .getInt("inspector_id", -1);

        // выбор дат
        startDate.setOnClickListener(v -> showDatePicker(startDate));
        endDate.setOnClickListener(v -> showDatePicker(endDate));

        // генерация отчёта
        btnGenerate.setOnClickListener(v -> {
            String start = startDate.getText().toString();
            String end = endDate.getText().toString();

            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(requireContext(), "Выберите обе даты", Toast.LENGTH_SHORT).show();
                return;
            }

            List<ModelRecords> records = dao.getRecordsByInspectorAndDate(inspectorId, start, end);

            if (records.isEmpty()) {
                Toast.makeText(requireContext(), "Нет записей за этот период", Toast.LENGTH_SHORT).show();
                tvRecordCount.setText("Записей: 0");
                return;
            }

            tvRecordCount.setText("Записей: " + records.size());

            boolean isPdf = toggleGroup.getCheckedButtonId() == R.id.btn_pdf;
            File file = ReportGenerator.generateReport(requireContext(), records, isPdf);

            Toast.makeText(requireContext(),
                    "Отчёт создан: " + file.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        });

        return view;
    }

    private void showDatePicker(TextView target) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, day) -> target.setText(String.format(Locale.getDefault(), "%02d.%02d.%d", day, month + 1, year)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();
    }
}
