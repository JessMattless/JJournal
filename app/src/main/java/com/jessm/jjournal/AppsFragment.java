package com.jessm.jjournal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppsFragment extends Fragment {

    static Map<Integer, MoodType> moodMap;

    public AppsFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment AppsFragment.
     */
    public static AppsFragment newInstance() { return new AppsFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps, container, false);

        TextView refreshNotice = view.findViewById(R.id.refreshNotice);
        if (!JournalApp.permissionsGranted()) {
            refreshNotice.setVisibility(View.VISIBLE);
        }

        Calendar lastWeek = Calendar.getInstance();
        lastWeek.add(Calendar.DAY_OF_YEAR, -7);

        List<JournalEntry> entryList = JournalApp.journalList
                .stream()
                .filter(item ->
                        item.getDate().getTime().after(lastWeek.getTime())
                ).collect(Collectors.toList());

        moodMap = new HashMap<>();
        for (JournalEntry entry : entryList) {
            long differenceToToday = System.currentTimeMillis() - entry.getDate().getTimeInMillis();
            differenceToToday = differenceToToday / JournalApp.DAY_IN_MILLIS; // Difference is now in x days: 1 = today

            moodMap.put((int) differenceToToday, entry.getMood());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FragmentManager fragmentManager = getParentFragmentManager();
        boolean alreadyOpen = false;

        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment instanceof AppsEntryFragment) {
                alreadyOpen = true;
                break;
            }
        }

        if (this.isVisible() && !alreadyOpen) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            if (JournalApp.appList != null) {
                for (App app : JournalApp.appList) {
                    transaction.add(R.id.appsView, AppsEntryFragment.newInstance(app));
                }
            }

            transaction.commit();
        }
    }
}