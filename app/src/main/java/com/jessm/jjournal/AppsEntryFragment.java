package com.jessm.jjournal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppsEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppsEntryFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_APP_NAME = "appName";
    private static final String ARG_APP_ICON = "appIcon";
    private static final String ARG_APP_MINUTES = "appMinutes";

    private String _appName;
    private Drawable _appIcon;
    private long _appMinutes;
    private boolean _expanded;

    public AppsEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param app the app data to be displayed.
     * @return A new instance of fragment AppsEntryFragment.
     */
    public static AppsEntryFragment newInstance(App app) {
        AppsEntryFragment fragment = new AppsEntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_APP_NAME, app.getName());
        args.putLong(ARG_APP_MINUTES, app.getUsage());

//        Drawable icon = app.getIcon();

        Bitmap bitmap = app.getIcon();

//        Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bitmapData = stream.toByteArray();

        args.putByteArray(ARG_APP_ICON, bitmapData);

//        args.putParcelable(ARG_APP_ICON, (Parcelable) icon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            _appName = getArguments().getString(ARG_APP_NAME);
            _appMinutes = getArguments().getLong(ARG_APP_MINUTES);

            byte[] byteArray = getArguments().getByteArray(ARG_APP_ICON);

            if (byteArray != null) {
                Bitmap iconBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                _appIcon = new BitmapDrawable(getResources(), iconBitmap);
            }
            else _appIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.round_background_large, null);
        }

        _expanded = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apps_entry, container, false);

        TextView appName = view.findViewById(R.id.appName);
        appName.setText(_appName);

        TextView appHours = view.findViewById(R.id.appHours);
        setTextViewTime(appHours, _appMinutes);

        ImageView appIcon = view.findViewById(R.id.appIcon);
        appIcon.setImageDrawable(_appIcon);

        ImageButton expandButton = view.findViewById(R.id.expandButton);
        expandButton.setOnClickListener(buttonView -> {
            LinearLayout appDetails = view.findViewById(R.id.appDetails);
            if (!_expanded) {
                appDetails.setVisibility(View.VISIBLE);
                expandButton.setImageResource(R.drawable.expand_less);
                _expanded = true;
            }
            else {
                appDetails.setVisibility(View.GONE);
                expandButton.setImageResource(R.drawable.expand_more);
                _expanded = false;
            }
        });

        Map<MoodType, Long> moodTimeMap = new HashMap<>();

        TextView veryHappy = view.findViewById(R.id.timeSpentVeryHappy);
        TextView happy = view.findViewById(R.id.timeSpentHappy);
        TextView neutral = view.findViewById(R.id.timeSpentNeutral);
        TextView sad = view.findViewById(R.id.timeSpentSad);
        TextView verySad = view.findViewById(R.id.timeSpentVerySad);
        TextView noMood = view.findViewById(R.id.timeSpentNoDetails);


//        AppsFragment.moodMap

        moodTimeMap.put(MoodType.VeryHappy, 0L);
        moodTimeMap.put(MoodType.Happy, 0L);
        moodTimeMap.put(MoodType.Neutral, 0L);
        moodTimeMap.put(MoodType.Sad, 0L);
        moodTimeMap.put(MoodType.VerySad, 0L);

        long noMoodTime = _appMinutes;

        Map<Integer, Long> usageMap = JournalApp.dailyUsageList.get(appName.getText().toString());
        if (usageMap != null) {
            for (Map.Entry<Integer, Long> map : usageMap.entrySet()) {
                if (AppsFragment.moodMap.containsKey(map.getKey())) {
                    long time = moodTimeMap.get(AppsFragment.moodMap.get(map.getKey()));
                    time += map.getValue();
                    moodTimeMap.put(AppsFragment.moodMap.get(map.getKey()), time);
                }
            }
        }

        setTextViewTime(veryHappy, moodTimeMap.get(MoodType.VeryHappy));
        setTextViewTime(happy, moodTimeMap.get(MoodType.Happy));
        setTextViewTime(neutral, moodTimeMap.get(MoodType.Neutral));
        setTextViewTime(sad, moodTimeMap.get(MoodType.Sad));
        setTextViewTime(verySad, moodTimeMap.get(MoodType.VerySad));

        for (Long time : moodTimeMap.values()) {
            noMoodTime -= time;
        }

        setTextViewTime(noMood, noMoodTime);

        return view;
    }

    void setTextViewTime(TextView textView, long minutes) {
        String timeString;
        if (minutes < 60) timeString = minutes + "m";
        else if (minutes == 120) timeString = "1hr";
        else {
            double time = ((double) minutes / 60);
            time = round(time, 1);
            timeString = time + "hrs";
        }
        textView.setText(timeString);
    }

    private static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }
}