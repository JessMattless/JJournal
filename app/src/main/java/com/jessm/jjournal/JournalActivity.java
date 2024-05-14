package com.jessm.jjournal;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.elevation.SurfaceColors;

public class JournalActivity extends AppCompatActivity {

    private static final int PAGE_COUNT = 4;
    public ViewPager2 viewPager;

    public Bundle journalArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_journal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        journalArgs = new Bundle();

        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                }
                else {
                    remove();
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        viewPager = findViewById(R.id.viewPager);
        FragmentStateAdapter pagerAdapter = new JournalPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(false);

    }

    private static class JournalPagerAdapter extends FragmentStateAdapter {
        public JournalPagerAdapter(AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 1: return new JournalAddFragment2();
                case 2: return new JournalAddFragment3();
                case 3: return new JournalAddFragment4();
                case 0: default: return new JournalAddFragment1();
            }
        }

        @Override
        public int getItemCount() {
            return PAGE_COUNT;
        }
    }
}