package com.jessm.jjournal;

public enum MoodType {VerySad, Sad, Neutral, Happy, VeryHappy;

    static int getDrawableId(MoodType mood) {
        switch(mood) {
            case VerySad: return R.drawable.mood_bad;
            case Sad: return R.drawable.sentiment_dissatisfied;
            case Happy: return R.drawable.sentiment_satisfied;
            case VeryHappy: return R.drawable.sentiment_very_satisfied;
            case Neutral: default: return R.drawable.sentiment_neutral;
        }
    }
}
