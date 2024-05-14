package com.jessm.jjournal;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JournalAddFragment4#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalAddFragment4 extends Fragment {

    public JournalAddFragment4() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment JournalAddFragment4.
     */
    public static JournalAddFragment4 newInstance() {
        return new JournalAddFragment4();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_add4, container, false);

        EditText title = view.findViewById(R.id.entryTitle);
        EditText content = view.findViewById(R.id.extraInput);
        Button doneButton = view.findViewById(R.id.doneButton);
        ImageButton backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(closeView -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        doneButton.setOnClickListener(nextView -> {
            JournalActivity parent = (JournalActivity) requireActivity();
            parent.journalArgs.putString("title", title.getText().toString());
            parent.journalArgs.putString("content", content.getText().toString());

            writeEntryToFile(parent.journalArgs);

            startActivity(new Intent(requireActivity(), MainActivity.class));
        });

        return view;
    }

    private void writeEntryToFile(Bundle entry) {
        String journalFile = requireContext().getFilesDir() + "/journal";
        try (FileOutputStream fos = new FileOutputStream(journalFile, true)) {
            String title = entry.getString("title");
            String content = entry.getString("content");
            String mood = entry.getString("mood");
            String dateTime = entry.getString("date") + " " + entry.getString("time");
            String journalString = title + "\n" + mood + "\n" + dateTime + "\n" + content + "\n" + "``````" + "\n";
            fos.write(journalString.getBytes());
        }
        catch (IOException ignore) {

        }
    }
}