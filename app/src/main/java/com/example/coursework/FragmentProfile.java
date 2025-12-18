package com.example.coursework;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coursework.data.DAO;
import com.example.coursework.data.DatabaseHelper;
import com.example.coursework.ui.theme.Main;

import java.io.IOException;

public class FragmentProfile extends Fragment {

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    DAO dao;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FragmentProfile() {

    }

    public static FragmentProfile newInstance(String param1, String param2) {
        FragmentProfile fragment = new FragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        databaseHelper = new DatabaseHelper(requireContext());
        try{
            databaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        db = databaseHelper.openDataBase();
        dao = new DAO(db);

        TextView backToAccount = view.findViewById(R.id.backToAccount);
        TextView countText = view.findViewById(R.id.countText);
        TextView name = view.findViewById(R.id.name);
        TextView login = view.findViewById(R.id.login);
        String template = "Инспектор %s";

        int inspectorId = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
                .getInt("inspector_id", -1);

        //Ставим количество сделанных записей в профиле
        int count = dao.getRecordsCountByInspector(inspectorId);
        countText.setText(String.valueOf(count));

        SharedPreferences prefs = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        String userName = prefs.getString("user_name", ""); // дефолтное значение пустое
        String userEmail = prefs.getString("user_email", "");

        // Подставляем в TextView
        name.setText(String.format(template, userName));
        login.setText(userEmail);

        // Обработка выхода из аккаунта
        backToAccount.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear(); // очищаем данные сессии
            editor.apply();

            // Переход на экран входа
            Intent intent = new Intent(requireContext(), Main.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}