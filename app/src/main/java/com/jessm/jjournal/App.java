package com.jessm.jjournal;

import android.graphics.Bitmap;

public class App {
    private final String name;
    private long usage;
    private final Bitmap icon;

    public App(String name, long usageMinutes, Bitmap icon) {
        this.name = name;
        this.usage = usageMinutes;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public long getUsage() {
        return usage;
    }

    public void setUsage(long usage) {
        this.usage = usage;
    }

    public Bitmap getIcon() {
        return icon;
    }
}
