package com.jessm.jjournal;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private boolean confirmClearData = false;

    String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.settings_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getWindow().setNavigationBarContrastEnforced(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings_page_title);
        setSupportActionBar(toolbar);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        AppCompatDelegate.setDefaultNightMode(
                (settings.getBoolean("darkMode", true)) ?
                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        currentName = settings.getString("name", "");

        SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
            if (key != null) {
                if (key.equals("darkMode")) {
                    recreate();
                }
                else if (key.equals("name")) {
                    if (sharedPreferences.getString("name", "").isEmpty()) {
                        settings.edit().putString("name", currentName).apply();
                    }
                }
            }
        };

        settings.registerOnSharedPreferenceChangeListener(listener);

        Button permissionsButton = findViewById(R.id.permissionsButton);
        if (!JournalApp.permissionsGranted()) {
            permissionsButton.setVisibility(View.VISIBLE);


            permissionsButton.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.app_permission_notice)
                        .setTitle(R.string.app_permission_title);

                builder.setPositiveButton(R.string.grant_permissions, (dialogInterface, i) -> {
                    if (!JournalApp.permissionsGranted()) {
                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    }
                    else {
                        Snackbar snackbar = Snackbar.make(permissionsButton, R.string.permissions_already_granted, BaseTransientBottomBar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(getColor(R.color.md_theme_surfaceContainerHigh));
                        snackbar.setTextColor(getColor(R.color.md_theme_primary));
                        snackbar.show();
                    }

                    dialogInterface.dismiss();

                    Snackbar snackbar = Snackbar.make(permissionsButton, R.string.permissions_notice, BaseTransientBottomBar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(getColor(R.color.md_theme_surfaceContainerHigh));
                    snackbar.setTextColor(getColor(R.color.md_theme_primary));
                    snackbar.show();
                });
                builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel());

                AlertDialog dialog = builder.create();

                dialog.show();
            });
        }

        Button clearDataButton = findViewById(R.id.clearDataButton);
        clearDataButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.clear_data_warning)
                    .setTitle(R.string.clear_data_button);

            builder.setPositiveButton(R.string.im_sure, (dialogInterface, i) -> { });
            builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                confirmClearData = false;
                dialogInterface.cancel();
            });

            AlertDialog dialog = builder.create();

            dialog.setOnShowListener(dialogInterface -> {
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(buttonView -> {
                    if (!confirmClearData) {
                        confirmClearData = true;

                        button.setText(R.string.clear_data_extra_warning);
                    }
                    else {
                        confirmClearData = false;
                        dialog.dismiss();

                        SharedPreferences.Editor editor = settings.edit();
                        editor.clear();
                        editor.apply();

                        File journalFile = new File(getFilesDir(), "journal");
                        Log.d("SettingsActivity", "journalFile.delete() returned: " + journalFile.delete());

                        startActivity(new Intent(SettingsActivity.this, SplashActivity.class));
                    }
                });
            });

            dialog.show();
        });

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        return true;//super.onNavigateUp();
    }


}