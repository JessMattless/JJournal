package com.jessm.jjournal;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JournalAddFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalAddFragment1 extends Fragment {

    public JournalAddFragment1() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment JournalAddFragment1.
     */
    public static JournalAddFragment1 newInstance() {
        return new JournalAddFragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_add1, container, false);

        EditText date = view.findViewById(R.id.dateInput);
        Button nextButton = view.findViewById(R.id.nextButton);
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        ImageButton dateButton = view.findViewById(R.id.dateButton);

        @SuppressLint("SimpleDateFormat")
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calendar = Calendar.getInstance();
        date.setText(format.format(calendar.getTime().getTime()));

        dateButton.setOnClickListener(dateView -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(
                    requireContext(),
                    (pickerView, givenYear, givenMonth, givenDay) -> {
                        Calendar outputDate = Calendar.getInstance();
                        outputDate.set(givenYear, givenMonth, givenDay);

                        date.setText(format.format(outputDate.getTime().getTime()));
                    },
                    year, month, day);

            datePicker.show();
        });

        closeButton.setOnClickListener(closeView -> startActivity(new Intent(requireActivity(), MainActivity.class)));

        nextButton.setOnClickListener(nextView -> {
            if (!date.getText().toString().isEmpty()) {
                ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
                viewPager.setCurrentItem(1, true);
                JournalActivity parent = (JournalActivity) requireActivity();
                parent.journalArgs.putString("date", date.getText().toString());
            }
            else {
                Snackbar snackbar = Snackbar.make(nextButton, R.string.date_empty_warning, BaseTransientBottomBar.LENGTH_SHORT);
                snackbar.setBackgroundTint(requireContext().getColor(R.color.md_theme_surfaceContainerHigh));
                snackbar.setTextColor(requireContext().getColor(R.color.md_theme_primary));
                snackbar.show();
            }
        });

        date.requestFocus();

        return view;
    }
}