package com.jessm.jjournal;

import java.util.Calendar;

public class JournalEntry {

    private final String _title;
    private final String _content;
    private final Calendar _date;
    private final MoodType _mood;

    public JournalEntry(String title, String content, Calendar dateTime, MoodType mood) {
        this._title = title;
        this._content = content;
        this._date = dateTime;
        this._mood = mood;
    }


    public String getTitle() { return _title; }
    public String getContent() { return _content; }
    public Calendar getDate() { return _date; }
    public MoodType getMood() { return _mood; }
}
