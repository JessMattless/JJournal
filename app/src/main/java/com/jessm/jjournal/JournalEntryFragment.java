package com.jessm.jjournal;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JournalEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JournalEntryFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ENTRY_TITLE = "entryTitle";
    private static final String ARG_ENTRY_CONTENT = "entryContent";
    private static final String ARG_ENTRY_MOOD = "entryMood";
    private static final String ARG_ENTRY_DATE = "entryDate";

    private String _entryTitle;
    private String _entryContent;
    private MoodType _entryMood;
    private Calendar _entryDate;

    public JournalEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param entry The entry to be shown.
     * @return A new instance of fragment JournalEntryFragment.
     */
    public static JournalEntryFragment newInstance(JournalEntry entry) {
        JournalEntryFragment fragment = new JournalEntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ENTRY_TITLE, entry.getTitle());
        args.putString(ARG_ENTRY_CONTENT, entry.getContent());
        args.putSerializable(ARG_ENTRY_MOOD, entry.getMood());
        args.putSerializable(ARG_ENTRY_DATE, entry.getDate());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _entryTitle = getArguments().getString(ARG_ENTRY_TITLE);
            _entryContent = getArguments().getString(ARG_ENTRY_CONTENT);
            _entryMood = (MoodType) getArguments().getSerializable(ARG_ENTRY_MOOD);
            _entryDate = (Calendar) getArguments().getSerializable(ARG_ENTRY_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal_entry, container, false);

        TextView title = view.findViewById(R.id.entryTitle);
        if (_entryTitle.isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            title.setText(format.format(_entryDate.getTime()));
        }
        else title.setText(_entryTitle);

        TextView content = view.findViewById(R.id.entryContent);
        content.setText(_entryContent);

        ImageView mood = view.findViewById(R.id.moodImage);
        mood.setImageResource(MoodType.getDrawableId(_entryMood));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = requireView();
        TextView content = view.findViewById(R.id.entryContent);

        ViewTreeObserver viewTreeObserver = content.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(() -> {
            Layout contentLayout = content.getLayout();

            if (contentLayout != null) {
                int lines = contentLayout.getLineCount();
                if (lines > 0) {
                    if (content.getLayout().getEllipsisCount(content.getLineCount() - 1) > 0) {
                        ImageButton expandButton = view.findViewById(R.id.expandButton);
                        expandButton.setVisibility(View.VISIBLE);
                        expandButton.setOnClickListener(contentView -> {
                            if (content.getMaxLines() == 5) {
                                content.setMaxLines(Integer.MAX_VALUE);
                                expandButton.setImageResource(R.drawable.expand_less);
                            }
                            else {
                                content.setMaxLines(5);
                                expandButton.setImageResource(R.drawable.expand_more);
                            }
                        });
                    }
                }
            }
        });


    }
}