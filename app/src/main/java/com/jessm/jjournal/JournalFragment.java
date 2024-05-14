package com.jessm.jjournal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JournalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalFragment extends Fragment {

    public JournalFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment JournalFragment.
     */
    public static JournalFragment newInstance() {
        return new JournalFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);

        JournalApp.journalList.clear();

        try {
            FileInputStream fis = requireContext().openFileInput("journal");
            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                List<String> entryString = new ArrayList<>();
                String line = reader.readLine();
                while (line != null) {

                    if (!line.equals("``````")) {
                        entryString.add(line);
                    }
                    else {
                        MoodType mood = MoodType.valueOf(entryString.get(1));

                        JournalEntry entry = getJournalEntry(entryString, mood);
                        JournalApp.journalList.add(entry);

                        entryString.clear();
                    }

                    line = reader.readLine();
                }
            } catch (ParseException e) {
//                throw new RuntimeException(e);
            }
        }
        catch (IOException e) {
//            throw new RuntimeException(e);
        }

        JournalApp.journalList.sort(Comparator.comparing(JournalEntry::getDate));
        Collections.reverse(JournalApp.journalList);

        return view;
    }

    @NonNull
    private static JournalEntry getJournalEntry(List<String> entryString, MoodType mood) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        Calendar dateTime = Calendar.getInstance();
        Date givenDate = dateFormat.parse(entryString.get(2));
        if (givenDate != null) { dateTime.setTime(givenDate); }

        StringBuilder contentString = new StringBuilder();
        for (int i = 3; i < entryString.size(); i++) {
            contentString.append(entryString.get(i)).append("\n");
        }

        return new JournalEntry(entryString.get(0), contentString.toString(), dateTime, mood);
    }

    @Override
    public void onStart() {
        super.onStart();

        FragmentManager fragmentManager = getParentFragmentManager();
        boolean alreadyOpen = false;

        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment instanceof JournalEntryFragment) {
                alreadyOpen = true;
                break;
            }
        }

        if (this.isVisible() && !alreadyOpen) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            for (JournalEntry entry : JournalApp.journalList) {
                transaction.add(R.id.entryView, JournalEntryFragment.newInstance(entry));
            }

            transaction.commit();
        }
    }
}