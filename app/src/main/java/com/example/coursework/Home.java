package com.example.coursework;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.coursework.R;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen_bot_menu);
        BottomNavigationView bottomMenu = findViewById(R.id.bottom_menu);

        String userName = getIntent().getStringExtra("user_name");
        String email = getIntent().getStringExtra("user_email");


        if (savedInstanceState==null) {
            loadFragment(new FragmentRecords());
        }
        bottomMenu.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            int id = item.getItemId();

            if (id == R.id.records){
                fragment = new FragmentRecords();
            } else if (id == R.id.reports) {
                fragment = new FragmentReports();
            } else if (id == R.id.profile){
                fragment = new FragmentProfile();
                Bundle bundle = new Bundle();
                bundle.putString("user_name", userName);
                bundle.putString("user_email", email);
                int inspectorId = getSharedPreferences("user", MODE_PRIVATE)
                        .getInt("inspector_id", -1);

                fragment.setArguments(bundle);
            }
                loadFragment(fragment);
            return true;
        });
    }

    //метод загрузки контейнеров
    public void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
