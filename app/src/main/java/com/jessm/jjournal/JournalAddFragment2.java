package com.jessm.jjournal;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
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
 * Use the {@link JournalAddFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalAddFragment2 extends Fragment {

    public JournalAddFragment2() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment JournalAddFragment2.
     */
    public static JournalAddFragment2 newInstance() {
        return new JournalAddFragment2();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_add2, container, false);

        EditText time = view.findViewById(R.id.timeInput);
        Button nextButton = view.findViewById(R.id.nextButton);
        ImageButton backButton = view.findViewById(R.id.backButton);
        ImageButton timeButton = view.findViewById(R.id.timeButton);

        Calendar calendar = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat")
        DateFormat format = new SimpleDateFormat("HH:mm");

        time.setText(format.format(calendar.getTime().getTime()));

        timeButton.setOnClickListener(timeView -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);


            TimePickerDialog timePicker = new TimePickerDialog(
                    requireContext(),
                    (pickerView, givenHour, givenMinute) -> {
                        Calendar outputTime = Calendar.getInstance();
                        outputTime.set(year, month, day, givenHour, givenMinute);

                        time.setText(format.format(outputTime.getTime().getTime()));
                    },
                    hour, minute, true);

            timePicker.show();
        });

        backButton.setOnClickListener(closeView -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        nextButton.setOnClickListener(nextView -> {
            if (!time.getText().toString().isEmpty()) {
                ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
                viewPager.setCurrentItem(2, true);
                JournalActivity parent = (JournalActivity) requireActivity();
                parent.journalArgs.putString("time", time.getText().toString());
            }
            else {
                Snackbar snackbar = Snackbar.make(nextButton, R.string.time_empty_warning, BaseTransientBottomBar.LENGTH_SHORT);
                snackbar.setBackgroundTint(requireContext().getColor(R.color.md_theme_surfaceContainerHigh));
                snackbar.setTextColor(requireContext().getColor(R.color.md_theme_primary));
                snackbar.show();
            }
        });

        return view;
    }
}