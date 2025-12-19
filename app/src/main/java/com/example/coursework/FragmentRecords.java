package com.example.coursework;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.appcompat.widget.SearchView;


import com.example.coursework.Controller.RecordController;
import com.example.coursework.data.DAO;
import com.example.coursework.data.DatabaseHelper;

import java.io.IOException;


public class FragmentRecords extends Fragment {

    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    DAO dao;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public FragmentRecords() {

    }

    public static FragmentRecords newInstance(String param1, String param2) {
        FragmentRecords fragment = new FragmentRecords();
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

        int inspectorId = requireContext()
                .getSharedPreferences("user", Context.MODE_PRIVATE)
                .getInt("inspector_id", -1);

        databaseHelper = new DatabaseHelper(requireContext());
        try{
            databaseHelper.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        db = databaseHelper.openDataBase();
        dao = new DAO(db);

        View view = inflater.inflate(R.layout.fragment_records, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        RecordController adapter = new RecordController(requireContext(), dao.getRecordsByInspector(inspectorId));
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        //поиск по записям
        SearchView searchView = view.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return false;
            }
        });



        ImageButton addRecords = view.findViewById(R.id.addRecords);
        addRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddRecords.class));
            }
        });

        return view;
    }
}