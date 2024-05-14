package com.jessm.jjournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<String> tabNames = new ArrayList<>();
    private static final int PAGE_COUNT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String username = settings.getString("name", "");
        if (username.isEmpty()) {
            startActivity(new Intent(MainActivity.this, SplashActivity.class));
        }

        AppCompatDelegate.setDefaultNightMode(
                (settings.getBoolean("darkMode", true)) ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

//        tabNames.add("Home");
        tabNames.add("Journal");
        tabNames.add("Apps");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home_page_title);
        setSupportActionBar(toolbar);

        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (tab.getPosition()) {
//                    case 0:
//                        toolbar.setTitle(R.string.home_page_title);
//                        break;
                    case 0:
                        toolbar.setTitle(R.string.journal_page_title);
                        break;
                    case 1:
                        toolbar.setTitle(R.string.apps_page_title);
                        break;
                    default:
                        break;
                }

                transaction.commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        FragmentStateAdapter pagerAdapter = new TabPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(tabNames.get(position))).attach();

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, JournalActivity.class)));

    }

    private static class TabPagerAdapter extends FragmentStateAdapter {
        public TabPagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
//            if (position == 0) {
//                return new HomeFragment();
//            }
            if (position == 0) {
                return new JournalFragment();
            }
            else {
                return new AppsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return PAGE_COUNT;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


}