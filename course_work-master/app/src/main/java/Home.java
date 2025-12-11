import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.coursework.FragmentProfile;
import com.example.coursework.FragmentRecords;
import com.example.coursework.FragmentReports;
import com.example.coursework.R;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.main_screen_bot_menu);

        BottomNavigationView bottomMenu = findViewById(R.id.bottom_menu);

        if (savedInstanceState == null) {
            loadFragment(new FragmentRecords());
        }

        bottomMenu.setOnItemSelectedListener(item -> {
            Fragment fragment;

            switch(item.getItemId()) {

                case R.id.records:
                    loadFragment(new FragmentRecords());
                    break;
                case R.id.reports:
                    loadFragment(new FragmentReports());
                    break;
                case R.id.profile:
                    loadFragment(new FragmentProfile());
                    break;
                default:
                    return false;
            }

            loadFragment(fragment);
            return true;

        });

    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}
