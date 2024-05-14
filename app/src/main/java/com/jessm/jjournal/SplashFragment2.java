package com.jessm.jjournal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment2 extends Fragment {

    public SplashFragment2() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment SplashFragment2.
     */
    public static SplashFragment2 newInstance() {
        return new SplashFragment2();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash2, container, false);

        ImageButton backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(backView -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        Button confirmButton = view.findViewById(R.id.continueButton);
        confirmButton.setOnClickListener(confirmView -> {
            EditText name = view.findViewById(R.id.nameInput);
            if (name.getText().toString().isEmpty()) {
                Snackbar snackbar = Snackbar.make(name, R.string.name_error_hint, BaseTransientBottomBar.LENGTH_SHORT);
                snackbar.setBackgroundTint(requireContext().getColor(R.color.md_theme_surfaceContainerHigh));
                snackbar.setTextColor(requireContext().getColor(R.color.md_theme_primary));
                snackbar.show();
            }
            else {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(requireContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("name", name.getText().toString());
                editor.apply();

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage(R.string.app_permission_notice)
                        .setTitle(R.string.app_permission_title);

                builder.setPositiveButton(R.string.grant_permissions, (dialogInterface, i) -> {

                    if (!JournalApp.PERMISSIONS) {
                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

                        Snackbar snackbar = Snackbar.make(name, R.string.permissions_notice, BaseTransientBottomBar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(requireContext().getColor(R.color.md_theme_surfaceContainerHigh));
                        snackbar.setTextColor(requireContext().getColor(R.color.md_theme_primary));
                        snackbar.show();
                    }
                    else {
                        Snackbar snackbar = Snackbar.make(name, R.string.permissions_already_granted, BaseTransientBottomBar.LENGTH_SHORT);
                        snackbar.setBackgroundTint(requireContext().getColor(R.color.md_theme_surfaceContainerHigh));
                        snackbar.setTextColor(requireContext().getColor(R.color.md_theme_primary));
                        snackbar.show();

                        startActivity(new Intent(requireActivity(), MainActivity.class));
                    }

                    dialogInterface.dismiss();


                });
                builder.setNegativeButton(R.string.cancel, (dialogInterface, i) -> {
                    dialogInterface.cancel();
                    startActivity(new Intent(requireActivity(), MainActivity.class));
                });

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(requireContext());

        if (JournalApp.permissionsGranted() && settings.contains("name"))
            startActivity(new Intent(requireActivity(), MainActivity.class));
    }
}