package com.jessm.jjournal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashFragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashFragment1 extends Fragment {

    public SplashFragment1() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment SplashFragment1.
     */
    public static SplashFragment1 newInstance() {
        return new SplashFragment1();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash1, container, false);

        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(buttonView -> {
            SplashActivity parent = (SplashActivity) requireActivity();
            parent.viewPager.setCurrentItem(1);
        });

        return view;
    }
}