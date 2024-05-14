package com.jessm.jjournal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JournalAddFragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalAddFragment3 extends Fragment {

    public JournalAddFragment3() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment JournalAddFragment3.
     */
    public static JournalAddFragment3 newInstance() {
        return new JournalAddFragment3();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_add3, container, false);

        Spinner mood = view.findViewById(R.id.moodSpinner);
        Button nextButton = view.findViewById(R.id.nextButton);
        ImageButton backButton = view.findViewById(R.id.backButton);

        backButton.setOnClickListener(closeView -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        nextButton.setOnClickListener(nextView -> {
            ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
            viewPager.setCurrentItem(3, true);
            JournalActivity parent = (JournalActivity) requireActivity();
            String moodString = mood.getSelectedItem().toString();
            moodString = moodString.replaceAll("\\s+", "");
            parent.journalArgs.putString("mood", moodString);
        });

        return view;
    }
}